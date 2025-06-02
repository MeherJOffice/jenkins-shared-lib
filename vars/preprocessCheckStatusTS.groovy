def call(Map args) {
    def pluginsPath = args.pluginsPath
    def cocosVersion = args.cocosVersion
    def overrideValue = args.override
    def isTesting = args.isTesting
    def workspace = args.workspace

    echo '⚙️ Preprocessing CheckStatus.ts with override and date...'

    // 🔍 Get Python virtual environment
    def venvPath = sh(
        script: "find \$HOME/.venvs -name 'pbxproj-env' -type d | head -n 1",
        returnStdout: true
    ).trim()

    if (!venvPath) {
        error "❌ Virtual environment 'pbxproj-env' not found!"
    }

    // 🧠 Resolve version-specific paths
    def bootFolder = cocosVersion == 'cocos2' ? 'Boot213' : 'Boot373'
    def tsFilePath = "${pluginsPath}/${bootFolder}/assets/LoadScene/CheckStatus.ts"
    def pythonScript = "${workspace}/JenkinsFiles/Python/PreprocessCheckStatus.py"

    // 🛠️ Run Python preprocessor
    sh """
        source '${venvPath}/bin/activate' && \
        python3 '${pythonScript}' '${tsFilePath}' '${overrideValue}' '${isTesting}'
    """

    sh "${pluginsPath}/${bootFolder}/addDummyCode-213"
    sh "${pluginsPath}/${bootFolder}/changeLibCC"
    
    // 🔄 Run prepareUpStore binary
    def prepareCmd = "'${pluginsPath}/${bootFolder}/3-prepareUpStore' 2>&1"
    def prepareOutput = sh(script: prepareCmd, returnStdout: true).trim()

    echo '📋 prepareUpStore output:'
    prepareOutput.readLines().each { line -> echo "│ ${line}" }

    // 📦 Extract the renamed file
    def newFileName = null
    prepareOutput.readLines().each { line ->
        def match = line =~ /__updating ts file from: .*CheckStatus\.ts to .*\/([A-Za-z0-9_]+\.ts)/
        if (match.find()) {
            newFileName = match.group(1)
            return
        }
    }

    if (!newFileName) {
        echo '❗ Could not match CheckStatus.ts rename. Full output:'
        prepareOutput.readLines().each { line -> echo "  >> ${line}" }
        error '❌ Failed to extract new filename for CheckStatus.ts!'
    }

    env.CHECKSTATUTNAME = newFileName
    echo "✅ New CheckStatus.ts filename: ${newFileName}"
}
