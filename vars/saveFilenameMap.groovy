def call(Map args = [:]) {
    def unityProjectPath = args.unityProjectPath
    def checkStatutEnv = args.get('checkStatutEnv', env.CHECKSTATUTNAME ?: 'undefined')
    def scriptToPatchEnv = args.get('scriptToPatchEnv', env.SCRIPT_TO_PATCH ?: 'undefined')

    if (!unityProjectPath) {
        error "❌ 'unityProjectPath' is required"
    }

    // \\\ Stage: Save filenameMap.json
    def productName = sh(
        script: "grep 'productName:' '${unityProjectPath}/ProjectSettings/ProjectSettings.asset' | sed 's/^[^:]*: *//'",
        returnStdout: true
    ).trim()

    def outputDir = "${env.HOME}/jenkinsBuild/${productName}"
    def jsonFilePath = "${outputDir}/filenameMap.json"

    def jsonContent = """{
  \"CheckstatutName\": \"${checkStatutEnv}\",
  \"FEln Name\": \"${scriptToPatchEnv}\"
}"""

    sh "mkdir -p '${outputDir}'"
    writeFile file: jsonFilePath, text: jsonContent
    echo "✅ Saved filenameMap.json to: ${jsonFilePath}"
}
