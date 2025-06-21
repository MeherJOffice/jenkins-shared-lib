def call(String tsPath, boolean testing) {
    def pythonScript = "${env.WORKSPACE}/JenkinsFiles/Python/UpdateBdate.py"

    // Find pbxproj-env venv
    def venvPath = sh(
        script: "find \$HOME/.venvs -name 'pbxproj-env' -type d | head -n 1",
        returnStdout: true
    ).trim()

    if (!venvPath) {
        error "❌ Virtual environment 'pbxproj-env' not found!"
    }
    if (!fileExists(tsPath)) {
        error "❌ CheckStatus.ts file not found at: ${tsPath}"
    }
    if (!fileExists(pythonScript)) {
        error "❌ Python UpdateBdate.py script not found: ${pythonScript}"
    }

    def flag = testing ? 'true' : 'false'

    sh """
        set -e
        source '${venvPath}/bin/activate'
        python3 '${pythonScript}' '${tsPath}' '${flag}'
    """
}
