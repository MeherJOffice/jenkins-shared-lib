def call(Map args) {
    def creatorPath = args.version == 'cocos2' ? env.COCOS_CREATOR_213_PATH : env.COCOS_CREATOR_373_PATH

    if (!creatorPath?.trim()) {
        error "❌ Environment variable for ${args.version} is not set."
    }

    def buildPath = "${args.projectPath}/build"
    echo "🧹 Cleaning old build at: ${buildPath}"
    sh """
        if [ -d '${buildPath}' ]; then
            echo "🗑️ Deleting old build..."
            rm -rf '${buildPath}'
        fi
    """

    echo "🚀 Building with Cocos Creator: ${creatorPath}"
    sh """
        '${creatorPath}' --path '${args.projectPath}' --build "platform=ios;debug=false"
    """

    echo '✅ Cocos project build completed.'
}
