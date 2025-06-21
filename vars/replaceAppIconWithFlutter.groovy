def call(String cocosDir, String flutterDir) {
    def srcIcons = "${flutterDir}/ios/Runner/Assets.xcassets/AppIcon.appiconset"
    def dstIcons = "${cocosDir}/build/jsb-default/frameworks/runtime-src/proj.ios_mac/ios/Images.xcassets/AppIcon.appiconset"

    sh """
        set -e
        if [ ! -d "${srcIcons}" ]; then
            echo "❌ Source icon folder not found: ${srcIcons}"
            exit 1
        fi

        if [ -d "${dstIcons}" ]; then
            rm -rf "${dstIcons}"
            echo "🗑️ Removed old icon set: ${dstIcons}"
        fi

        mkdir -p "\$(dirname "${dstIcons}")"
        cp -R "${srcIcons}" "${dstIcons}"
        echo "✅ Copied AppIcon.appiconset from Flutter to Cocos: ${srcIcons} → ${dstIcons}"
    """
}
