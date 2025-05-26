def call(Map args = [:]) {
    def cocosVersion = args.cocosVersion

    if (!cocosVersion) {
        error "❌ 'cocosVersion' parameter is required"
    }

    // \\\\\\ Stage: Check Cocos Creator Path
    if (cocosVersion == 'cocos2') {
        if (!env.COCOS_CREATOR_213_PATH?.trim()) {
            error '❌ Environment variable COCOS_CREATOR_213_PATH is not set. Please define it under Jenkins > Manage Jenkins > Global properties.'
        }
        echo "📌 Using Cocos Creator 2 path: ${env.COCOS_CREATOR_213_PATH}"
    } else if (cocosVersion == 'cocos3') {
        if (!env.COCOS_CREATOR_373_PATH?.trim()) {
            error '❌ Environment variable COCOS_CREATOR_373_PATH is not set. Please define it under Jenkins > Manage Jenkins > Global properties.'
        }
        echo "📌 Using Cocos Creator 3 path: ${env.COCOS_CREATOR_373_PATH}"
    } else {
        error "❌ Unknown COCOS_VERSION: ${cocosVersion}"
    }
}
