def call(Map args = [:]) {
    def unityProjectPath = args.unityProjectPath
    def executeMethod = args.get('executeMethod', 'BuildHelper.PerformBuild')

    if (!unityProjectPath) {
        error "❌ 'unityProjectPath' is required"
    }

    // \\\ Stage: Build Unity Project
    def versionFile = "${unityProjectPath}/ProjectSettings/ProjectVersion.txt"
    def unityVersion = sh(script: "grep 'm_EditorVersion:' '${versionFile}' | awk '{print \$2}'", returnStdout: true).trim()
    def unityBinary = "/Applications/Unity/Hub/Editor/${unityVersion}/Unity.app/Contents/MacOS/Unity"

    echo "Detected Unity version: ${unityVersion}"
    echo "Starting Unity build using binary: ${unityBinary}"

    sh """
        '${unityBinary}' -quit -batchmode -projectPath '${unityProjectPath}' -executeMethod ${executeMethod}
    """

    echo '✅ Unity build completed successfully.'
}
