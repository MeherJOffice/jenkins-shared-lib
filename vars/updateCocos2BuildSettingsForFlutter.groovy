def call(String cocosProjectPath, String flutterProjectPath) {
    // Path to your Python script
    def pythonScript = "${env.WORKSPACE}/JenkinsFiles/Python/SetupCocosBuildSettingsFlutter.py"

    // Find the pbxproj-env venv in ~/.venvs
    def venvPath = sh(
        script: "find \$HOME/.venvs -name 'pbxproj-env' -type d | head -n 1",
        returnStdout: true
    ).trim()

    if (!venvPath) {
        error "❌ Virtual environment 'pbxproj-env' not found!"
    }

    if (!fileExists(cocosProjectPath)) {
        error "❌ Cocos project path not found: ${cocosProjectPath}"
    }
    if (!fileExists(flutterProjectPath)) {
        error "❌ Flutter project path not found: ${flutterProjectPath}"
    }
    if (!fileExists(pythonScript)) {
        error "❌ Python setup script not found: ${pythonScript}"
    }

    sh """
        set -e
        source '${venvPath}/bin/activate'
        python3 '${pythonScript}' '${cocosProjectPath}' '${flutterProjectPath}'
    """
}
