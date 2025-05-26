def call(Map args = [:]) {
    def unityProjectPath = args.unityProjectPath

    if (!unityProjectPath) {
        error "❌ 'unityProjectPath' is required"
    }

    // \\\\\\ Stage: 📂 Open Game Build Folder
    def userHome = sh(
        script: "echo $HOME",
        returnStdout: true
    ).trim()

    def productName = sh(
        script: "grep 'productName:' '${unityProjectPath}/ProjectSettings/ProjectSettings.asset' | sed 's/^[^:]*: *//'",
        returnStdout: true
    ).trim()

    def buildRootPath = "${userHome}/jenkinsBuild/${productName}"

    echo "📂 Opening game build folder: ${buildRootPath}"

    sh "open \"${buildRootPath}\""
}
