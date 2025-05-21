def call(String unityPath, String pluginPath) {
    if (!fileExists(unityPath)) {
        error("❌ Unity project path does not exist: ${unityPath}")
    }
    if (!fileExists(pluginPath)) {
        error("❌ Plugins repo path does not exist: ${pluginPath}")
    }
    echo "✅ Paths validated: Unity and Plugin repos exist."
}
