def call(Map args = [:]) {
    def unityProjectPath = args.unityProjectPath
    def homeDir = env.HOME
    def jenkinsfiles = "${env.WORKSPACE}/JenkinsFiles"
    def patchScript = 'updateUnityXcodeProj.go'
    def privacyFile = 'PrivacyInfo.xcprivacy'

    if (!unityProjectPath) {
        error "❌ 'unityProjectPath' is required"
    }

    // \\\ Stage: Patch Unity Xcode Project
    def productName = sh(
        script: "grep 'productName:' '${unityProjectPath}/ProjectSettings/ProjectSettings.asset' | sed 's/^[^:]*: *//'",
        returnStdout: true
    ).trim()

    def targetBuildFolder = "${homeDir}/jenkinsBuild/${productName}"

    echo "📦 Patching Unity Xcode project in: ${targetBuildFolder}"

    sh """
        set +e
        mkdir -p '${targetBuildFolder}'
        cp '${jenkinsfiles}/Golang/${patchScript}' '${targetBuildFolder}/'
        cp '${jenkinsfiles}/${privacyFile}' '${targetBuildFolder}/'
        cd '${targetBuildFolder}'
        go mod init patchproject
        go get howett.net/plist
        go build -o patch_unity_xcode ${patchScript}
        ./patch_unity_xcode
        BUILD_RESULT=\$?
        echo '🔍 Dumping log before cleanup:'
        cat /tmp/unity_xcode_patch.log || echo '⚠️ No log found.'
        rm -f ${patchScript} ${privacyFile} patch_unity_xcode go.mod go.sum
        exit \$BUILD_RESULT
    """

    echo '✅ Unity Xcode project patched successfully.'
}
