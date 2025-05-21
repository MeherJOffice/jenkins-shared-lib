def call(Map args) {
    def creatorPath = args.version == 'cocos2' 
        ? env.COCOS_CREATOR_213_PATH 
        : env.COCOS_CREATOR_373_PATH

    if (!creatorPath?.trim()) {
        error "❌ Environment variable for ${args.version} is not set."
    }

    echo "ℹ️ Using Cocos Creator path: ${creatorPath}"
    echo "ℹ️ Project path: ${args.projectPath}"

    def buildPath = "${args.projectPath}/build"
    echo "🧹 Cleaning old build at: ${buildPath}"

    sh """
        if [ -d '${buildPath}' ]; then
            echo "🗑️ Deleting old build..."
            rm -rf '${buildPath}' || echo "⚠️ Failed to delete old build folder."
        else
            echo "✅ No existing build folder to clean."
        fi
    """

    echo "🚀 Building with Cocos Creator (${args.version}): ${creatorPath}"

    try {
        if (args.version == 'cocos2') {
            sh """
                set -e
                '${creatorPath}' --path '${args.projectPath}' --build "platform=ios;debug=false" 2>&1 | tee build.log
            """
            echo '✅ Cocos 2 project built via CLI.'
        } else if (args.version == 'cocos3') {
            sh """
                set -e
                '${creatorPath}' --project '${args.projectPath}' --build "platform=ios;debug=false;outputName:ios" 2>&1 | tee build.log
            """
            echo '✅ Cocos 3 project built via CLI (using --project).'
        } else {
            error "❌ Unsupported Cocos version: ${args.version}"
        }
    } catch (Exception e) {
        echo "⚠️ Build failed. Debug logs:"
        sh "cat build.log"
        error "❌ Build failed: ${e.message}"
    }
}