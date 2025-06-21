def call(String sdkFlutterProject, String rootPath) {
    // Path to source and destination
    def sourceFile = "${sdkFlutterProject}/flutter_proj/flutterBuildUpdate"
    def destFile = "${rootPath}/flutterBuildUpdate"

    // Validate files/folders
    if (!fileExists(sourceFile)) {
        error "❌ Source executable not found: ${sourceFile}"
    }

    // Copy to rootPath
    sh """
        cp '${sourceFile}' '${destFile}'
        chmod +x '${destFile}'
    """
    echo "✅ Copied flutterBuildUpdate to root: ${destFile}"

    // Run it
    sh """
        '${destFile}'
    """
    echo "✅ Executed flutterBuildUpdate."
}
