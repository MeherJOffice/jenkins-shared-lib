def call(Map args) {
        def cocosVersion = args.cocosVersion
        def pluginsPath = args.pluginsPath
        def cocosProjectPath = args.cocosProjectPath

        def bootFolder = 'Boot213'
        def bootPath = "${pluginsPath}/${bootFolder}"

        echo "🔄 Syncing ${bootFolder} into Cocos ${cocosVersion} project..."

        def foldersToCopy = ['node_modules']
        def foldersToCopyInsideAssets = ['LoadScene', 'Ext']
        def filesToCopy = ['2-changeLibCC']
        def commands = []

        // Copy folders at root
        foldersToCopy.each { folder ->
                commands << "rm -rf '${cocosProjectPath}/${folder}'"
                commands << "cp -R '${bootPath}/${folder}' '${cocosProjectPath}/'"
        }

        // Copy single files at root
        filesToCopy.each { file ->
                commands << "rm -f '${cocosProjectPath}/${file}'"
                commands << "cp '${bootPath}/${file}' '${cocosProjectPath}/'"
        }

        // Copy folders inside /assets
        foldersToCopyInsideAssets.each { folder ->
                commands << "rm -rf '${cocosProjectPath}/assets/${folder}'"
                commands << "cp -R '${bootPath}/assets/${folder}' '${cocosProjectPath}/assets/'"
        }

        // 🔄 Copy specific .cpp/.h/.mm files from Boot213 templates to Cocos project
        def filePairs = [
        'build-templates/jsb-default/frameworks/runtime-src/Classes/AppDelegate.cpp',
        'build-templates/jsb-default/frameworks/runtime-src/proj.ios_mac/ios/AppController.h',
        'build-templates/jsb-default/frameworks/runtime-src/proj.ios_mac/ios/AppController.mm'
        ]

        filePairs.each { relativePath ->
                def src = "${bootPath}/${relativePath}"
                def dst = "${cocosProjectPath}/${relativePath}"
                commands << "cp '${src}' '${dst}'"
        }

        // 🔧 Execute all collected shell commands
        sh """
        set -e
        echo "📁 Plugin repo path: ${pluginsPath}"
        echo "🎮 Cocos project path: ${cocosProjectPath}"
        ${commands.join('\n')}
        echo "✅ BootUnity213 fully synced for Cocos 2."
    """
}
