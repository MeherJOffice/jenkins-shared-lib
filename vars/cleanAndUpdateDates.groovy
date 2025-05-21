def call(Map args) {
    def unityProjectPath = args.unityProjectPath
    def cocosProjectPath = args.cocosProjectPath
    def workspace = args.workspace

    // 🧹 Step 1: Clean previous build folders
    echo '🧹 Cleaning previous build folders...'

    def productName = sh(
        script: "grep 'productName:' '${unityProjectPath}/ProjectSettings/ProjectSettings.asset' | sed 's/^[^:]*: *//'",
        returnStdout: true
    ).trim()

    def buildPath = "${env.HOME}/jenkinsBuild/${productName}"
    def folders = ['CocosBuild', 'UnityBuild', 'XcodeWorkspace']

    folders.each { folder ->
        def fullPath = "${buildPath}/${folder}"
        if (fileExists(fullPath)) {
            echo "🗑 Deleting: ${fullPath}"
            sh "rm -rf '${fullPath}'"
        } else {
            echo "✅ ${folder} does not exist, skipping."
        }
    }

    echo "✅ Cleanup finished for ${buildPath}"

    // 🧠 Step 2: Update script dates
    echo '📆 Updating date inside CheckStatus and FE2In scripts...'

    def venvPath = sh(
        script: "find \$HOME/.venvs -name 'pbxproj-env' -type d | head -n 1",
        returnStdout: true
    ).trim()

    if (!venvPath) {
        error '❌ Virtual environment not found!'
    }

    echo "✅ Found VENV at: ${venvPath}"

    def jsonFilePath = "${env.HOME}/jenkinsBuild/${productName}/filenameMap.json"
    def jenkinsfiles = "${workspace}/JenkinsFiles"
    def pythonScript = "${jenkinsfiles}/Python/UpdateScriptDate.py"

    def jsonContent = readJSON file: jsonFilePath
    def checkStatusPath = "${cocosProjectPath}/assets/LoadScene/${jsonContent.CheckstatutName}"
    def feInPath = jsonContent['FEln Name']

    
    echo "📄 Parsed JSON: CheckStatus = ${checkStatusPath}, FE2In = ${feInPath}"

    sh """
        source '${venvPath}/bin/activate' && \
        python3 '${pythonScript}' '${checkStatusPath}' && \
        python3 '${pythonScript}' '${feInPath}'
    """

    echo "✅ Date updated in ${checkStatusPath} and ${feInPath}"
}
