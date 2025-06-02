def call(Map args = [:]) {
    def cocosProjectPath = args.cocosProjectPath
    def scriptName = args.get('scriptName', '2-changeLibCC')

    if (!cocosProjectPath) {
        error "❌ 'cocosProjectPath' is required"
    }

    // \\\ Stage: Run changeLibCC Script After Cocos Build
    def scriptPath = "${cocosProjectPath}/${scriptName}"

    echo "🔧 Running script: ${scriptPath}"
    sh "chmod +x '${scriptPath}'"
    sh "'${scriptPath}'"

    echo '✅ changeLibCC script executed successfully.'
}
