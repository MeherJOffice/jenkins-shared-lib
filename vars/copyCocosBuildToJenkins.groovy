def call(Map args = [:]) {
    def unityProjectPath = args.unityProjectPath
    def cocosProjectPath = args.cocosProjectPath
    def cocosVersion = args.cocosVersion

    if (!unityProjectPath || !cocosProjectPath || !cocosVersion) {
        error "❌ 'unityProjectPath', 'cocosProjectPath', and 'cocosVersion' are required"
    }

    def productName = sh(
        script: "grep 'productName:' '${unityProjectPath}/ProjectSettings/ProjectSettings.asset' | sed 's/^[^:]*: *//'",
        returnStdout: true
    ).trim()

    def targetBaseFolder = "$HOME/jenkinsBuild/${productName}/CocosBuild"

    if (cocosVersion == 'cocos2') {
        def sourceBuildFolder = "${cocosProjectPath}/build"
        echo "📂 Copying Cocos 2 build from ${sourceBuildFolder} to ${targetBaseFolder}"

        sh """
            rm -rf '${targetBaseFolder}'
            mkdir -p '${targetBaseFolder}'
            cp -R '${sourceBuildFolder}/.' '${targetBaseFolder}/'
            rm -rf '${sourceBuildFolder}'
        """

        echo '✅ Cocos 2 build copied and original build folder deleted.'
        return targetBaseFolder
    } else if (cocosVersion == 'cocos3') {
        def sourceFullProject = cocosProjectPath
        echo "📂 Copying full Cocos 3 project from ${sourceFullProject} to ${targetBaseFolder}"

        if (params.ENVIRONMENT == 'Testing') {
            sh """
            mkdir -p '${targetBaseFolder}'
            cp -R '${sourceFullProject}/.' '${targetBaseFolder}/'
            cd '${targetBaseFolder}'
            find . -mindepth 1 -maxdepth 1 ! -name 'build' ! -name 'native' -exec rm -rf {} +
        """
            echo '✅ Cocos 3 project copied and cleaned (kept only build and native folders).'
        } 
        else {
            targetBaseFolder = "$HOME/jenkinsBuild/${productName}/CocosProject"
            sh """
            mkdir -p '${targetBaseFolder}'
            cp -R '${sourceFullProject}/.' '${targetBaseFolder}/'
        """
            echo '✅ Cocos 3 project copied (all files and folders kept).'
        }

        return targetBaseFolder
    }
 else {
        error "❌ Unsupported cocosVersion: ${cocosVersion}"
 }
}
