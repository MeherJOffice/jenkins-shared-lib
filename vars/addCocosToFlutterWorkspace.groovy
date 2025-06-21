def call(String flutterDir, String cocosDir) {
    def pythonScript = "${env.WORKSPACE}/JenkinsFiles/Python/AddCocosToFlutterWorkspace.py"

    // Find pbxproj-env venv
    def venvPath = sh(
        script: "find \$HOME/.venvs -name 'pbxproj-env' -type d | head -n 1",
        returnStdout: true
    ).trim()

    if (!venvPath) {
        error "❌ Virtual environment 'pbxproj-env' not found!"
    }
    if (!fileExists(flutterDir)) {
        error "❌ Flutter project path not found: ${flutterDir}"
    }
    if (!fileExists(cocosDir)) {
        error "❌ Cocos project path not found: ${cocosDir}"
    }
    if (!fileExists(pythonScript)) {
        error "❌ Python add-to-workspace script not found: ${pythonScript}"
    }

    sh """
        set -e
        source '${venvPath}/bin/activate'
        python3 '${pythonScript}' '${flutterDir}' '${cocosDir}'
    """
}
