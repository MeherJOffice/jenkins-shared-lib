def call(Map args) {
    def creatorPath = args.version == 'cocos2' 
        ? env.COCOS_CREATOR_213_PATH 
        : env.COCOS_CREATOR_373_PATH

    if (!creatorPath?.trim()) {
        error "❌ Environment variable for ${args.version} is not set."
    }

    def buildPath = "${args.projectPath}/build"
    echo "🧹 Cleaning old build at: ${buildPath}"

    sh """
        if [ -d '${buildPath}' ]; then
            echo "🗑️ Deleting old build..."
            rm -rf '${buildPath}'
        else
            echo "✅ No existing build folder to clean."
        fi
    """

    echo "🚀 Building with Cocos Creator (${args.version}): ${creatorPath}"

    if (args.version == 'cocos2') {
        sh """
            '${creatorPath}' --path '${args.projectPath}' --build "platform=ios;debug=false"
        """
        echo '✅ Cocos 2 project built via CLI.'
    } else if (args.version == 'cocos3') {
        sh """
            '${creatorPath}' --project '${args.projectPath}' --build "platform=ios;debug=false"
        """
        echo '✅ Cocos 3 project built via CLI (using --project).'
    } else {
        error "❌ Unsupported Cocos version: ${args.version}"
    }
}
