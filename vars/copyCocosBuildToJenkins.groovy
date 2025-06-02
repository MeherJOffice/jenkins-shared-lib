def call(Map args = [:]) {
    def cocosProjectPath = args.cocosProjectPath
    def cocosVersion = args.cocosVersion

    if (!cocosProjectPath || !cocosVersion) {
        error "❌ 'cocosProjectPath' and 'cocosVersion' are required"
    }

    // 🧠 Extract product name from settings/builder.json
    def builderJsonPath = "${cocosProjectPath}/settings/builder.json"
    def productName = sh(
        script: "python3 -c \"import json; print(json.load(open('${builderJsonPath}'))['title'])\"",
        returnStdout: true
    ).trim()

    def targetBaseFolder = "$HOME/jenkinsBuild/${productName}/CocosBuild"
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
}
