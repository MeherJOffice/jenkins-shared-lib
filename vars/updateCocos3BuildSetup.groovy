def call(Map args = [:]) {
    def unityProjectPath = args.unityProjectPath
    def cocosProjectPath = args.cocosProjectPath
    def pluginsProjectPath = args.pluginsProjectPath
    def targetBuildRoot = args.get('targetBuildRoot', "$HOME/jenkinsBuild")

    if (!unityProjectPath || !cocosProjectPath || !pluginsProjectPath) {
        error "❌ 'unityProjectPath', 'cocosProjectPath', and 'pluginsProjectPath' are required"
    }

    // Patch native engine
    def sourceDir = "${pluginsProjectPath}/BootUnity373/nativePatch/engine/ios"
    def targetDir = "${cocosProjectPath}/native/engine/ios"

    echo '🛠️ Patching native engine files (merge only, no delete)...'
    echo "🔄 From: ${sourceDir}"
    echo "➡️ To:   ${targetDir}"

    sh "mkdir -p '${targetDir}'"
    sh "rsync -av '${sourceDir}/' '${targetDir}/'"

    echo '✅ Native engine files patched successfully.'

    // Read product name
    def productName = sh(
        script: "grep 'productName:' '${unityProjectPath}/ProjectSettings/ProjectSettings.asset' | sed 's/^[^:]*: *//'",
        returnStdout: true
    ).trim()
    def sanitizedProductName = productName.replaceAll(/[^A-Za-z0-9]/, '')

    // Read bundle ID
    def bundleId = sh(
        script: """
            awk '/applicationIdentifier:/,/^[^ ]/' '${unityProjectPath}/ProjectSettings/ProjectSettings.asset' | \
            grep 'iPhone:' | sed 's/^.*iPhone: *//' | head -n 1 | tr -d '\\n\\r'
        """,
        returnStdout: true
    ).trim()

    if (!bundleId) {
        bundleId = sh(
            script: """
                grep 'bundleIdentifier:' '${unityProjectPath}/ProjectSettings/ProjectSettings.asset' | \
                sed 's/^[^:]*: *//' | head -n 1 | tr -d '\\n\\r'
            """,
            returnStdout: true
        ).trim()
    }

    // Gather scenes
    def loadSceneDir = "${cocosProjectPath}/assets/LoadScene"
    def sceneFiles = sh(
        script: "find '${loadSceneDir}' -name '*.scene' -exec basename {} \\;",
        returnStdout: true
    ).trim().split('\n')

    if (sceneFiles.size() == 0) {
        error "❌ No scenes found in ${loadSceneDir}"
    }

    def scenesList = []
    def startSceneUuid = ''

    for (scene in sceneFiles) {
        def sceneMeta = "${loadSceneDir}/${scene}.meta"
        def uuid = sh(
            script: "grep '\"uuid\"' '${sceneMeta}' | sed 's/.*\"uuid\": *\"\\(.*\\)\".*/\\1/'",
            returnStdout: true
        ).trim()

        if (!uuid) {
            echo "❌ Could not extract UUID from: ${sceneMeta}"
            sh "cat '${sceneMeta}'"
            error "❌ UUID not found in ${sceneMeta}"
        }

        echo "📄 Found scene: ${scene} → UUID: ${uuid}"
        scenesList << [url: "db://assets/LoadScene/${scene}", uuid: uuid, inBundle: false]

        if (scene.toLowerCase().endsWith('s.scene')) {
            startSceneUuid = uuid
            echo "✅ Marked as startScene: ${scene}"
        }
    }

    if (!startSceneUuid && scenesList.size() > 0) {
        startSceneUuid = scenesList[0].uuid
        echo "⚠️ No scene ending in 's.scene' found, fallback to: ${scenesList[0].url}"
    }

    def buildPath = "${targetBuildRoot}/${productName}/CocosBuild"
    def nativeOutPath = "${buildPath}/native"

    def config = [
        platform   : 'ios',
        buildPath  : buildPath,
        nativeEnginePath : nativeOutPath,
        debug      : false,
        name       : sanitizedProductName,
        outputName : 'ios',
        startScene : startSceneUuid,
        scenes     : scenesList,
        packages   : [
            ios: [
                packageName   : bundleId,
                orientation   : [portrait: true, upsideDown: true, landscapeRight: true, landscapeLeft: true],
                osTarget      : [iphoneos: true, simulator: false],
                targetVersion : '12.0',
                developerTeam : ''
            ],
            native: [
                encrypted   : false,
                compressZip : false,
                JobSystem   : 'tbb'
            ]
        ]
    ]

    def configPath = "${cocosProjectPath}/buildConfig_ios.json"
    writeJSON file: configPath, json: config, pretty: 2
    echo "✅ buildConfig_ios.json generated at ${configPath}"
}
