def call(Map args = [:]) {
    def cocosProjectPath = args.cocosProjectPath
    def jenkinsfilesPath = args.get('jenkinsfilesPath', "${env.WORKSPACE}/JenkinsFiles")

    if (!cocosProjectPath) {
        error "❌ 'cocosProjectPath' is required"
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

    def pythonFile = "${jenkinsfilesPath}/Python/ConfigureBuilderSettings.py"
    def copiedFile = "${cocosProjectPath}/ConfigureBuilderSettings.py"

    // 📝 Copy script into project
    sh "cp '${pythonFile}' '${copiedFile}'"

    // 🧠 Run script
    sh """
        source '${venvPath}/bin/activate' && \
        python3 '${copiedFile}' '${cocosProjectPath}'
    """

    // 🧹 Clean up
    sh "rm -f '${copiedFile}'"
    echo '🧹 Cleanup: Deleted ConfigureBuilderSettings.py'
    echo '✅ Cocos 2 builder settings updated successfully.'
}
