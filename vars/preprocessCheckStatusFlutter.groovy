def call(Map args) {
    def pluginsPath = args.pluginsPath
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

    def bootFolder = 'BootFlutter213'
    def tsFilePath = "${pluginsPath}/${bootFolder}/assets/LoadScene/CheckStatus.ts"
    def pythonScript = "${workspace}/JenkinsFiles/Python/PreprocessCheckStatusFlutter.py"

    def exePath = "${pluginsPath}/shuffleAndRandomizeCode"
    if (!fileExists(exePath)) {
        error "❌ Executable not found: ${exePath}"
    }
    sh "chmod +x '${exePath}'"
    sh "'${exePath}'"

    // 🛠️ Run Python preprocessor
    sh """
        source '${venvPath}/bin/activate' && \
        python3 '${pythonScript}' '${tsFilePath}' '${overrideValue}' '${isTesting}'
    """

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

    def newtsFilePath = "${pluginsPath}/${bootFolder}/assets/LoadScene/${env.CHECKSTATUTNAME}"

    def datetestingFlag = params.testing ? 'true' : 'false'
    // Run the Python script
    // 🛠️ Run Python date preprocessor
    sh """
        source '${venvPath}/bin/activate' && \
        sh "python3 /path/to/UpdateBdate.py '${newtsFilePath}' ${datetestingFlag}"
    """
}
