def call(Map args) {
    def cocosVersion = args.cocosVersion
    def pluginsPath = args.pluginsPath
    def cocosProjectPath = args.cocosProjectPath

    // Use the correct folder based on version
    def bootFolder = 'Boot213'
    def bootPath = "${pluginsPath}/${bootFolder}"

    echo "🔄 Syncing ${bootFolder} into Cocos ${cocosVersion} project..."

    def foldersToCopy = ['build-templates']
    def foldersToCopyInsideAssets = ['LoadScene', 'Ext']
    def filesToCopy = ['2-changeLibCC']
    def commands = []

    foldersToCopy.each { folder ->
            commands << "rm -rf '${cocosProjectPath}/${folder}'"
            commands << "cp -R '${bootPath}/${folder}' '${cocosProjectPath}/'"
    }

    filesToCopy.each { file ->
            commands << "rm -f '${cocosProjectPath}/${file}'"
            commands << "cp '${bootPath}/${file}' '${cocosProjectPath}/'"
    }
    foldersToCopyInsideAssets.each { folder ->
        commands << "rm -rf '${cocosProjectPath}/assets/${folder}'"
        commands << "cp -R '${bootPath}/assets/${folder}' '${cocosProjectPath}/assets/'"
    }

    sh """
            set -e
            echo "📁 Plugin repo path: ${pluginsPath}"
            echo "🎮 Cocos project path: ${cocosProjectPath}"
            ${commands.join('\n')}
            echo "✅ BootUnity213 fully synced for Cocos 2."
        """
}
