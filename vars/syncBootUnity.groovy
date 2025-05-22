def call(Map args) {
    def cocosVersion = args.cocosVersion
    def pluginsPath = args.pluginsPath
    def cocosProjectPath = args.cocosProjectPath

    // Use the correct folder based on version
    def bootFolder = cocosVersion == 'cocos2' ? 'BootUnity213' : 'BootUnity373'
    def bootUnityPath = "${pluginsPath}/${bootFolder}"

    echo "🔄 Syncing ${bootFolder} into Cocos ${cocosVersion} project..."

    if (cocosVersion == 'cocos2') {
        def foldersToCopy = ['assets', 'build-templates', 'settings']
        def filesToCopy = ['changeLibCC', 'creator.d.ts', 'jsconfig.json', 'project.json']
        def commands = []

        foldersToCopy.each { folder ->
            commands << "rm -rf '${cocosProjectPath}/${folder}'"
            commands << "cp -R '${bootUnityPath}/${folder}' '${cocosProjectPath}/'"
        }

        filesToCopy.each { file ->
            commands << "rm -f '${cocosProjectPath}/${file}'"
            commands << "cp '${bootUnityPath}/${file}' '${cocosProjectPath}/'"
        }

        sh """
            set -e
            echo "📁 Plugin repo path: ${pluginsPath}"
            echo "🎮 Cocos project path: ${cocosProjectPath}"
            ${commands.join('\n')}
            echo "✅ BootUnity213 fully synced for Cocos 2."
        """

    } else if (cocosVersion == 'cocos3') {

        def source = "${bootUnityPath}/assets/LoadScene"
        def destination = "${cocosProjectPath}/assets/LoadScene"

        sh """
            echo "📁 Syncing only LoadScene folder for Cocos 3 from ${bootFolder}..."
            rm -rf '${destination}'
            mkdir -p '${cocosProjectPath}/assets'
            cp -R '${source}' '${destination}'
            echo "✅ LoadScene from BootUnity373 synced for Cocos 3."
        """

        def settingssource = "${bootUnityPath}/settings/v2/packages"
        def settingsdestination = "${cocosProjectPath}/settings/v2/packages"

        sh """
            echo "📁 Syncing settings folder for Cocos 3 from ${bootFolder}..."
            rm -rf '${settingsdestination}'
            mkdir -p '${cocosProjectPath}/settings'
            cp -R '${settingssource}' '${settingsdestination}'
            echo "✅ SettingsScene from BootUnity373 synced for Cocos 3."
        """
    } else {
        error "❌ Unsupported Cocos version: ${cocosVersion}"
    }
}
