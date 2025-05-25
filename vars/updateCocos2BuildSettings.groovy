def call(Map args = [:]) {
    def unityProjectPath = args.unityProjectPath
    def cocosProjectPath = args.cocosProjectPath
    def jenkinsfilesPath = args.get('jenkinsfilesPath', "${env.WORKSPACE}/JenkinsFiles")

    if (!unityProjectPath || !cocosProjectPath) {
        error "❌ 'unityProjectPath' and 'cocosProjectPath' are required"
    }

    echo '🔎 Searching for Python virtual environment...'

    def venvPath = sh(
        script: "find \$HOME/.venvs -name 'pbxproj-env' -type d | head -n 1",
        returnStdout: true
    ).trim()

    if (!venvPath) {
        error '❌ Virtual environment not found!'
    }

    echo "✅ Found VENV at: ${venvPath}"

    def productName = sh(
        script: "grep 'productName:' '${unityProjectPath}/ProjectSettings/ProjectSettings.asset' | sed 's/^[^:]*: *//'",
        returnStdout: true
    ).trim()

    def bundleId = sh(
        script: """
            awk '/applicationIdentifier:/,/^[^ ]/' '${unityProjectPath}/ProjectSettings/ProjectSettings.asset' | \
            grep 'iPhone:' | sed 's/^.*iPhone: *//' | head -n 1 | tr -d '\\n\\r'
        """,
        returnStdout: true
    ).trim()

    if (!bundleId) {
        bundleId = sh(
            script: """
                grep 'bundleIdentifier:' '${unityProjectPath}/ProjectSettings/ProjectSettings.asset' | \
                sed 's/^[^:]*: *//' | head -n 1 | tr -d '\\n\\r'
            """,
            returnStdout: true
        ).trim()
    }

    echo "📦 Product Name: ${productName}"
    echo "🔐 Bundle ID: ${bundleId}"

    def pythonFile = "${jenkinsfilesPath}/SetupCocosBuildSettings.py"
    def copiedFile = "${unityProjectPath}/unityBuild/SetupCocosBuildSettings.py"

    sh "cp '${jenkinsfilesPath}/Python/SetupCocosBuildSettings.py' '${copiedFile}'"

    sh """
        source '${venvPath}/bin/activate' && \
        python3 '${copiedFile}' '${cocosProjectPath}' '${bundleId}' '${productName}'
    """

    sh "rm -f '${copiedFile}'"
    echo '🧹 Cleanup: Deleted SetupCocosBuildSettings.py'
    echo '✅ Cocos 2 build settings updated successfully.'
}
