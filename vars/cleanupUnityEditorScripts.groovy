def call(Map args = [:]) {
    def unityProjectPath = args.unityProjectPath

    if (!unityProjectPath) {
        error "❌ 'unityProjectPath' is required"
    }

    // \\\ Stage: Cleanup Unity Editor Scripts
    def targetEditorPath = "${unityProjectPath}/Assets/Editor"
    def helperScript = "${targetEditorPath}/BuildHelper.cs"
    def unityprojectsetupscript = "${targetEditorPath}/SetupUnityProject.cs"

    echo '🧹 Cleaning up temporary editor scripts...'

    if (fileExists(helperScript)) {
        sh "rm -f '${helperScript}'"
        echo '✅ Deleted BuildHelper.cs'
    } else {
        echo '⚠️ BuildHelper.cs not found, skipping'
    }

    if (fileExists(unityprojectsetupscript)) {
        sh "rm -f '${unityprojectsetupscript}'"
        echo '✅ Deleted SetupUnityProject.cs'
    } else {
        echo '⚠️ SetupUnityProject.cs not found, skipping'
    }

    echo '🧼 Editor script cleanup complete.'
}
