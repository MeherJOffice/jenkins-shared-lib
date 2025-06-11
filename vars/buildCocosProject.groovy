def call(Map args) {
    def creatorPath = args.version == 'cocos2' 
        ? env.COCOS_CREATOR_213_PATH 
        : env.COCOS_CREATOR_373_PATH

    if (!creatorPath?.trim()) {
        error "❌ Environment variable for ${args.version} is not set."
    }
    // Skip condition
    if (params.ENVIRONMENT == 'Production' && args.version == 'cocos3') {
        echo "⏭️ Skipping build for cocos3 in Production mode."
        return
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
            def localConfigPath = "${args.projectPath}/buildConfig_ios.json"
            def fallbackConfigPath = "${env.WORKSPACE}/JenkinsFiles/buildConfig_ios.json"
            def configPath = ""

            def configExists = fileExists(localConfigPath)
            if (configExists) {
                echo "✅ Found local config file at ${localConfigPath}"
                configPath = localConfigPath
            } else {
                echo "⚠️ Local config not found. Using fallback config: ${fallbackConfigPath}"
                configPath = fallbackConfigPath
            }

            sh """
                set -e
                '${creatorPath}' --project '${args.projectPath}' --build "platform=ios;debug=false;configPath=${configPath}" 2>&1 | tee build.log
            """
            echo '✅ Cocos 3 project built via CLI with config file.'
        } else {
            error "❌ Unsupported Cocos version: ${args.version}"
        }
    } catch (Exception e) {
        echo "⚠️ Build failed. Debug logs:"
        sh "cat build.log"
        error "❌ Build failed: ${e.message}"
    }
}
