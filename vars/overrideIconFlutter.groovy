def call(String flutterDir, String cocosDir) {
    def flutterIconDir = "${flutterDir}/ios/Runner/Assets.xcassets/AppIcon.appiconset"
    def cocosSpritePath = "${cocosDir}/assets/Sprite/icon_place_holder.png"

    // Find the biggest icon in the Flutter appiconset folder
    def iconFile = sh(
        script: """
            cd "${flutterIconDir}"
            ls -S *.png | head -n 1
        """,
        returnStdout: true
    ).trim()

    if (!iconFile) {
        error "❌ No PNG icon found in ${flutterIconDir}"
    }

    def sourceIconPath = "${flutterIconDir}/${iconFile}"
    def destIconPath = cocosSpritePath

    // Overwrite the cocos sprite with the flutter icon
    sh """
        cp "${sourceIconPath}" "${destIconPath}"
        echo "✅ Copied ${sourceIconPath} to ${destIconPath}"
    """
}
