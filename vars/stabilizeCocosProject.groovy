def call(Map args = [:]) {
    def projectPath = args.projectPath
    def cleanNative = args.get('cleanNative', true)

    if (!projectPath) {
        error "❌ 'projectPath' is required for stabilizeCocosProject"
    }

    echo '🧹 Cleaning temp and library folders...'
    sh "rm -rf '${projectPath}/temp' '${projectPath}/library'"

    if (cleanNative) {
        echo '🧹 Also cleaning native folder...'
        sh "rm -rf '${projectPath}/native'"
    }

    echo '🕒 Waiting 2s to let file system settle...'
    sleep time: 2, unit: 'SECONDS'

    echo '✅ Project state stabilized.'
}
