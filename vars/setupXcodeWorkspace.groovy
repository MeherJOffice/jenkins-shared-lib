def call(Map args = [:]) {
    def unityProjectPath = args.unityProjectPath
    def cocosProjectPath = args.cocosProjectPath
    def cocosVersion = args.cocosVersion

    if (!unityProjectPath || !cocosProjectPath || !cocosVersion) {
        error "❌ 'unityProjectPath', 'cocosProjectPath', and 'cocosVersion' are required"
    }

    // \\\ Stage: Setup Xcode Workspace (Unity + Cocos)
    echo '🔎 Searching for Python virtual environment...'

    def venvPath = sh(
        script: "find $HOME/.venvs -name 'pbxproj-env' -type d | head -n 1",
        returnStdout: true
    ).trim()

    if (!venvPath) {
        error "❌ Virtual environment 'pbxproj-env' not found!"
    }
    echo "✅ Found VENV at: ${venvPath}"

    def pythonfiles = "${env.WORKSPACE}/JenkinsFiles/Python"
    def sourcePyScript = "${pythonfiles}/SetupXcodeWorkspace.py"
    def targetFolder = "${unityProjectPath}/unityBuild"
    def copiedScript = "${targetFolder}/SetupXcodeWorkspace.py"

    echo "📁 Copying SetupXcodeWorkspace.py to: ${targetFolder}"
    sh """
        mkdir -p '${targetFolder}'
        cp '${sourcePyScript}' '${copiedScript}'
    """
    echo '✅ Script copied.'

    def productName = sh(
        script: "grep 'productName:' '${unityProjectPath}/ProjectSettings/ProjectSettings.asset' | sed 's/^[^:]*: *//'",
        returnStdout: true
    ).trim()

    def sanitizedName = productName.replaceAll(/[^a-zA-Z0-9]/, '')
    def targetBuildFolder = "$HOME/jenkinsBuild/${productName}"

    def unityXcodeProj = "${targetBuildFolder}/UnityBuild/Unity-iPhone.xcodeproj"
    def cocosProjectFolderName = cocosProjectPath.tokenize('/').last()
    def cocosXcodeProj = cocosVersion == 'cocos2'
        ? "${targetBuildFolder}/CocosBuild/jsb-default/frameworks/runtime-src/proj.ios_mac/${sanitizedName}.xcodeproj"
        : "${targetBuildFolder}/CocosBuild/${cocosProjectFolderName}/build/ios/proj/${sanitizedName}.xcodeproj"

    if (!fileExists(unityXcodeProj)) {
        error "❌ Unity Xcode project not found at: ${unityXcodeProj}"
    }
    if (!fileExists(cocosXcodeProj)) {
        error "❌ Cocos Xcode project not found at: ${cocosXcodeProj}"
    }

    echo '🚀 Running SetupXcodeWorkspace.py...'
    sh """
        source '${venvPath}/bin/activate' && \
        python3 '${copiedScript}' '${unityXcodeProj}' '${cocosXcodeProj}'
    """

    sh "rm -f '${copiedScript}'"
    echo '🧹 Cleanup: Deleted SetupXcodeWorkspace.py'
    echo '🎉 Workspace with Unity and Cocos projects created successfully!'
}
