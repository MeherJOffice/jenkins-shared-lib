def call(String pluginPath) {
    echo "🧹 Cleaning Git repo at: ${pluginPath}"
    sh """
        cd '${pluginPath}'
        git reset --hard HEAD
        git clean -fd
    """
    echo '✅ Plugin repo reset to a clean state.'
}
