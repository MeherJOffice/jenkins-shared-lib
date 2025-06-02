def call(Map args = [:]) {
    def cocosProjectPath = args.cocosProjectPath

    if (!cocosProjectPath) {
        error "❌ 'cocosProjectPath' is required"
    }

    // 🏠 Get user home
    def userHome = sh(
        script: "echo $HOME",
        returnStdout: true
    ).trim()

    // 📄 Read product name from Cocos builder.json
    def builderJson = readJSON file: "${cocosProjectPath}/settings/builder.json"
    def productName = builderJson.title

    if (!productName) {
        error "❌ Could not extract product name from builder.json"
    }

    def buildRootPath = "${userHome}/jenkinsBuild/${productName}"

    echo "📂 Opening game build folder: ${buildRootPath}"
    sh "open \"${buildRootPath}\""
}
