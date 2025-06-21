def call(String flutterDir, String cocosDir) {
    def pythonScript = "${env.WORKSPACE}/JenkinsFiles/Python/CleanWorkspaceProjects.py"

    def venvPath = sh(
        script: "find \$HOME/.venvs -name 'pbxproj-env' -type d | head -n 1",
        returnStdout: true
    ).trim()

    if (!venvPath) {
        error "❌ Virtual environment 'pbxproj-env' not found!"
    }
    if (!fileExists(flutterDir)) {
        error "❌ Flutter directory not found: ${flutterDir}"
    }
    if (!fileExists(cocosDir)) {
        error "❌ Cocos directory not found: ${cocosDir}"
    }
    if (!fileExists(pythonScript)) {
        error "❌ Python CleanWorkspaceProjects.py script not found: ${pythonScript}"
    }

    sh """
        set -e
        echo "Removing build folder from Cocos dir: ${cocosDir}/build"
        rm -rf "${cocosDir}/build"
        source '${venvPath}/bin/activate'
        python3 '${pythonScript}' '${flutterDir}'
    """
}
