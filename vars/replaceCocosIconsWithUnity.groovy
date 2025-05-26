def call(Map args = [:]) {
    def unityProjectPath = args.unityProjectPath
    def cocosVersion = args.cocosVersion

    if (!unityProjectPath || !cocosVersion) {
        error "❌ 'unityProjectPath' and 'cocosVersion' are required"
    }

    // \\\ Stage: Replace Cocos iOS Icons with Unity Icons (supports cocos2 and cocos3)
    echo '🔎 Reading product name from Unity settings...'

    def productName = sh(
        script: "grep 'productName:' '${unityProjectPath}/ProjectSettings/ProjectSettings.asset' | sed 's/^[^:]*: *//'",
        returnStdout: true
    ).trim()

    def unityIconsPath = "${env.HOME}/jenkinsBuild/${productName}/UnityBuild/Unity-iPhone/Images.xcassets/AppIcon.appiconset"

    def cocosIconsPath = (cocosVersion == 'cocos2')
        ? "${env.HOME}/jenkinsBuild/${productName}/CocosBuild/jsb-default/frameworks/runtime-src/proj.ios_mac/ios/Images.xcassets/AppIcon.appiconset"
        : "${env.HOME}/jenkinsBuild/${productName}/CocosBuild/native/engine/ios/Images.xcassets/AppIcon.appiconset"

    if (!fileExists(unityIconsPath)) {
        error "❌ Unity AppIcon path not found: ${unityIconsPath}"
    }

    echo "🔁 Replacing Cocos (${cocosVersion}) icons using Unity's icon set..."
    sh """
        rm -rf '${cocosIconsPath}'
        mkdir -p \$(dirname '${cocosIconsPath}')
        cp -R '${unityIconsPath}' '${cocosIconsPath}'
    """

    echo "✅ Cocos ${cocosVersion} iOS app icons successfully replaced!"
}
