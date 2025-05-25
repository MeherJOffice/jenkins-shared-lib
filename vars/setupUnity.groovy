def call(Map args = [:]) {
    def unityProjectPath = args.unityProjectPath
    def pluginsProjectPath = args.pluginsProjectPath
    def sceneIndexToPatch = args.get('sceneIndexToPatch', '0')
    def jenkinsfilesPath = "${env.WORKSPACE}/JenkinsFiles"
    def packageName = 'com.unity.sharp-zip-lib'
    def packageVersion = '1.3.9'

    if (!unityProjectPath || !pluginsProjectPath) {
        error "❌ 'unityProjectPath' and 'pluginsProjectPath' are required"
    }

    // \\\ Stage: Copy Plugin Files to Unity Project
    echo '🔄 Copying plugin folders (Editor, Plugins, Scripts) to Unity project...'

    def foldersToCopy = ['Editor', 'Plugins', 'Scripts']
    def copiedScriptName = ''
    def unityScriptPath = ''

    foldersToCopy.each { folder ->
        def sourcePath = "${pluginsProjectPath}/unityProj/Assets/${folder}"
        def targetPath = "${unityProjectPath}/Assets/${folder}"

        echo "📁 Copying: ${sourcePath} → ${targetPath}"
        sh "mkdir -p '${targetPath}'"
        sh "rsync -av --exclude='*.meta' '${sourcePath}/' '${targetPath}/'"

        if (folder == 'Scripts') {
            def pluginScriptsPath = "${pluginsProjectPath}/unityProj/Assets/Scripts"
            def detectedCs = sh(
                script: "find '${pluginScriptsPath}' -name '*.cs' | head -n 1",
                returnStdout: true
            ).trim()

            if (!detectedCs) {
                error '❌ No .cs script found in plugin Scripts folder!'
            }

            copiedScriptName = detectedCs.tokenize('/').last()
            unityScriptPath = "${unityProjectPath}/Assets/Scripts/${copiedScriptName}"
        }
    }

    if (!unityScriptPath) {
        error '❌ Could not determine SCRIPT_TO_PATCH!'
    }

    def editorSource = "${jenkinsfilesPath}/UnityScripts/Editor"
    def editorTarget = "${unityProjectPath}/Assets/Editor"
    sh "mkdir -p '${editorTarget}'"
    sh "rsync -av --exclude='*.meta' '${editorSource}/' '${editorTarget}/'"

    env.SCRIPT_TO_PATCH = unityScriptPath
    env.SCENE_INDEX_TO_PATCH = sceneIndexToPatch

    echo "📌 SCRIPT_TO_PATCH set to: ${env.SCRIPT_TO_PATCH}"
    echo "📌 SCENE_INDEX_TO_PATCH set to: ${env.SCENE_INDEX_TO_PATCH}"

    // \\\ Stage: Add SharpZipLib Package via Package Manager
    def manifestPath = "${unityProjectPath}/Packages/manifest.json"
    def manifestContent = readFile(manifestPath)
    if (!manifestContent.contains(packageName)) {
        echo '🔧 Adding SharpZipLib to manifest.json...'
        sh """
            tmpfile=\$(mktemp)
            jq '.dependencies += {\"${packageName}\": \"${packageVersion}\"}' '${manifestPath}' > \$tmpfile && mv \$tmpfile '${manifestPath}'
        """
    } else {
        echo '✅ SharpZipLib already present in manifest.json.'
    }

    // \\\ Stage: Setup Unity Project
    def versionFile = "${unityProjectPath}/ProjectSettings/ProjectVersion.txt"
    def unityVersion = sh(script: "grep 'm_EditorVersion:' '${versionFile}' | awk '{print \$2}'", returnStdout: true).trim()
    def unityBinary = "/Applications/Unity/Hub/Editor/${unityVersion}/Unity.app/Contents/MacOS/Unity"

    echo "⚡ Setting up Unity project with version ${unityVersion}"

    sh """
        SCRIPT_TO_PATCH='${env.SCRIPT_TO_PATCH}' \\
        SCENE_INDEX_TO_PATCH='${env.SCENE_INDEX_TO_PATCH}' \\
        '${unityBinary}' -quit -batchmode -projectPath '${unityProjectPath}' \\
        -executeMethod SetupUnityProject.SetupProjectForSDK
    """

    // \\\ Stage: Trigger Unity Compilation (Auto Detect Unity Version)
    echo "🚀 Triggering Unity compilation..."
    sh "'${unityBinary}' -quit -batchmode -projectPath '${unityProjectPath}'"

    echo '✅ Unity plugin setup and compilation complete.'
}
