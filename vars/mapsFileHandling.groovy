def call(String rootPath, String sdkPath) {
    // Ensure required environment variable is set
    if (!env.CHECKSTATUTNAME) {
        error "❌ env.CHECKSTATUTNAME is not set!"
    }
    if (!fileExists(rootPath)) {
        error "❌ rootPath not found: ${rootPath}"
    }
    if (!fileExists("${sdkPath}/functionsMap.json")) {
        error "❌ functionsMap.json not found in SDK path: ${sdkPath}"
    }

    // Save CHECKSTATUTNAME to text file
    writeFile file: "${rootPath}/scriptMap.txt", text: env.CHECKSTATUTNAME

    // Copy functionsMap.json
    sh """
        cp "${sdkPath}/functionsMap.json" "${rootPath}/functionsMap.json"
        echo "✅ Copied functionsMap.json and saved scriptMap.txt"
    """
}
