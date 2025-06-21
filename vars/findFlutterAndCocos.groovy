def call(String rootPath) {
    def rootFolders = sh(
        script: """
            cd "${rootPath}"
            for d in */ ; do
                if [ -d "\$d" ]; then
                    echo "\${d%/}"
                fi
            done
        """,
        returnStdout: true
    )
    .trim()
    .split('\n')
    .findAll { it && !it.endsWith('@tmp') }

    if (rootFolders.size() != 2) {
        error "❌ Expected exactly 2 folders (Flutter and Cocos) under ${rootPath}, found: ${rootFolders}"
    }

    def flutterDir = null
    def cocosDir = null

    for (folder in rootFolders) {
        def pubspecExists = fileExists("${rootPath}/${folder}/pubspec.yaml")
        def libDirExists = fileExists("${rootPath}/${folder}/lib")
        if (pubspecExists && libDirExists) {
            flutterDir = folder
        } else {
            cocosDir = folder
        }
    }

    if (!flutterDir || !cocosDir) {
        error "❌ Could not uniquely identify Flutter and Cocos folders."
    }

    echo "✅ Found Flutter project: ${flutterDir}"
    echo "✅ Found Cocos project: ${cocosDir}"

    return [
        flutterDir: "${rootPath}/${flutterDir}",
        cocosDir:   "${rootPath}/${cocosDir}"
    ]
}
