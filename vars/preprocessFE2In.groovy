def call(Map args) {
    def venvPath = sh(
        script: "find $HOME/.venvs -name 'pbxproj-env' -type d | head -n 1",
        returnStdout: true
    ).trim()

    if (!venvPath) error '❌ Virtual environment not found!'

    def fe2inScript = "${args.pluginPath}/unityProj/Assets/Scripts/FE2In.cs"
    def fe2inPy = "${fe2inScript}.py"
    def jenkinsfiles = "${args.jenkinsFiles}"

    sh "cp '${jenkinsfiles}/Python/PreprocessFE2In.py' '${fe2inPy}'"

    sh """
        source '${venvPath}/bin/activate' && \
        python3 '${fe2inPy}' '${fe2inScript}' '${args.override}' '${args.isTesting}'
    """

    sh "chmod +x '${args.pluginPath}/shuffleAndRandomizeCode'"
    sh "'${args.pluginPath}/shuffleAndRandomizeCode'"
    sh "rm -f '${fe2inPy}'"

    echo '✅ FE2In.cs processed and cleaned.'
}
