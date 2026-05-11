/**
 * Core Logic for FileZorter organization and cleanup.
 */
class ZorterCore {

    /**
     * Determines the category for a given file name based on ZorterConstants.
     */
    static String getCategory(String fileName) {
        def ext = fileName.lastIndexOf('.').with { it != -1 ? fileName.substring(it).toLowerCase() : "" }
        def category = ZorterConstants.CATEGORIES.find { key, list -> ext in list }?.key ?: "Misc"
        return category
    }

    /**
     * Safely moves a file to a destination directory, handling name collisions.
     */
    static void safeMove(File source, File destDir) {
        if (!destDir.exists()) destDir.mkdirs()
        
        String baseName = source.name.lastIndexOf('.').with { it != -1 ? source.name.substring(0, it) : source.name }
        String ext = source.name.lastIndexOf('.').with { it != -1 ? source.name.substring(it) : "" }
        
        File target = new File(destDir, source.name)
        int counter = 1
        
        while (target.exists()) {
            target = new File(destDir, "${baseName}_${counter}${ext}")
            counter++
        }
        
        source.renameTo(target)
    }

    /**
     * Counts valid files in a directory (skipping hidden and the app itself).
     */
    static int countFiles(File dir) {
        if (!dir || !dir.exists()) return 0
        int count = 0
        dir.eachFile { file ->
            if (!file.hidden && !file.isDirectory() && !isAppFile(file.name)) {
                count++
            }
        }
        return count
    }

    /**
     * Checks if a filename belongs to the application components.
     */
    static boolean isAppFile(String name) {
        return name in [
            "FileZorter.groovy", "ZorterConstants.groovy", "ZorterCore.groovy", "ZorterConfigUI.groovy", 
            "config.json", "assets", "FileZorta.groovy", "ZortaConstants.groovy", "ZortaCore.groovy", "ZortaConfigUI.groovy",
            "FolderSieve.groovy", "SieveConstants.groovy", "SieveCore.groovy", "SieveConfigUI.groovy"
        ]
    }
}
