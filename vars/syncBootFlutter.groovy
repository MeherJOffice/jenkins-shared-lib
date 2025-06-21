def call(Map args) {
    def pluginsPath = args.pluginsPath
    def cocosProjectPath = args.cocosProjectPath

    def bootFolder = 'BootFlutter213'
    def bootUnityPath = "${pluginsPath}/${bootFolder}"

    echo "🔄 Syncing ${bootFolder} into Cocos project..."

    def foldersToCopy = ['build-templates', 'settings', 'node_modules']
    def filesToCopy = ['creator.d.ts', 'jsconfig.json', 'project.json']
    def insideFoldersToCopy = ['LoadScene', 'Ext', 'Sprite']

    def checkNodeModulesScript = """
        cd '${bootUnityPath}'
        if [ ! -d node_modules ] || [ -z "\$(ls -A node_modules)" ]; then
            echo '📦 node_modules missing or empty. Running npm ci...'
            npm ci
        else
            echo '📦 node_modules already present and non-empty in SDK.'
        fi
    """

    sh """
        set -e
        ${checkNodeModulesScript}
        echo "📁 SDK repo path: ${pluginsPath}"
        echo "🎮 Cocos project path: ${cocosProjectPath}"
        # Start copy commands
        ${foldersToCopy.collect { folder -> "rm -rf '${cocosProjectPath}/${folder}' && cp -R '${bootUnityPath}/${folder}' '${cocosProjectPath}/'" }.join('\n')}
        ${filesToCopy.collect { file -> "rm -f '${cocosProjectPath}/${file}' && cp '${bootUnityPath}/${file}' '${cocosProjectPath}/'" }.join('\n')}
        ${insideFoldersToCopy.collect { folder -> "rm -rf '${cocosProjectPath}/assets/${folder}' && cp -R '${bootUnityPath}/${folder}' '${cocosProjectPath}/assets'" }.join('\n')}
        echo "✅ BootFlutter213 fully synced for Cocos 2 project."
    """
}
