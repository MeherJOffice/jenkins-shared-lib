def call(String cocosDir) {
    def pythonScript = "${env.WORKSPACE}/JenkinsFiles/Python/InsertRegistrant.py"

    if (!fileExists(cocosDir)) {
        error "❌ Cocos directory not found: ${cocosDir}"
    }
    if (!fileExists(pythonScript)) {
        error "❌ Python InsertRegistrant.py script not found: ${pythonScript}"
    }

    sh """
        python3 '${pythonScript}' '${cocosDir}'
    """
}
