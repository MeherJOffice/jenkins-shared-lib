def call(Map args = [:]) {
    def unityProjectPath = args.unityProjectPath
    def pluginsProjectPath = args.pluginsProjectPath

    if (!unityProjectPath || !pluginsProjectPath) {
        error "❌ 'unityProjectPath' and 'pluginsProjectPath' are required"
    }

    // \\\\\\ Stage: Copy functionsMap.json to Cocos Build
    def productName = sh(
        script: "grep 'productName:' '${unityProjectPath}/ProjectSettings/ProjectSettings.asset' | sed 's/^[^:]*: *//'",
        returnStdout: true
    ).trim()

    def buildpath = "$HOME/jenkinsBuild/${productName}"
    def sourceJsonPath = "${pluginsProjectPath}/functionsMap.json"
    def targetJsonPath = "${buildpath}/functionsMap.json"

    if (!fileExists(sourceJsonPath)) {
        error "❌ Missing functionsMap.json at: ${sourceJsonPath}"
    }

    echo "📁 Copying functionsMap.json to ${targetJsonPath}"
    sh "cp '${sourceJsonPath}' '${targetJsonPath}'"
    echo '✅ functionsMap.json copied successfully.'
}
