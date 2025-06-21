def call(String cocosProjectPath, String creatorPath) {
    if (!fileExists(cocosProjectPath)) {
        error "❌ Cocos project path not found: ${cocosProjectPath}"
    }
    if (!fileExists(creatorPath)) {
        error "❌ Cocos Creator executable not found at: ${creatorPath}"
    }
    sh """
        set -e
        '${creatorPath}' --path '${cocosProjectPath}' --build "platform=ios;debug=false" 2>&1 | tee build.log
    """
    echo '✅ Cocos 2 project built via CLI.'
}
