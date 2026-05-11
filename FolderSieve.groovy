import groovy.swing.SwingBuilder
import javax.swing.*
import java.awt.*
import java.util.regex.Pattern
import groovy.transform.Field

/**
 * FolderSieve - The Universal Groovy Janitor
 * A compact utility to organize and clean directories.
 */

// 1. Setup Look and Feel
UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())

// 2. Map-Based Categorization Logic
@Field Map categories = [
    'Images'   : ['.jpg', '.jpeg', '.png', '.gif', '.svg', '.heic', '.webp', '.bmp', '.ico', '.tif', '.tiff'],
    'Documents': ['.pdf', '.docx', '.txt', '.xlsx', '.pptx', '.csv', '.rtf', '.odt', '.ods', '.odp', '.md'],
    'Code'     : ['.groovy', '.java', '.py', '.html', '.css', '.js', '.json', '.c', '.cpp', '.h', '.cs', '.php', '.sql', '.xml', '.yaml', '.yml'],
    'Media'    : ['.mp4', '.mov', '.mp3', '.wav', '.mkv', '.avi', '.wmv', '.flv', '.m4a', '.flac', '.ogg'],
    'Compressed' : ['.zip', '.rar', '.7z', '.tar', '.gz', '.bz2', '.xz', '.iso', '.dmg', '.pkg'],
    'Fonts'    : ['.ttf', '.otf', '.woff', '.woff2', '.eot'],
    'Design'   : ['.psd', '.ai', '.xd', '.fig', '.sketch', '.dwg', '.dxf'],
    'Installers'    : ['.exe', '.msi', '.apk', '.app', '.jar']
]

// Junk file regex pattern
@Field Pattern junkPattern = ~/(?i).*\.(tmp|log|crdownload|bak)$/

// 3. State Variables
@Field File selectedDir = null
@Field int fileCount = 0
@Field appStatusLabel
@Field appFrame

// 4. Core Logic Functions

/**
 * Determines the category for a given file extension.
 * Uses Safe Navigation and Elvis Operator.
 */
def getCategory(String fileName) {
    def ext = fileName.lastIndexOf('.').with { it != -1 ? fileName.substring(it).toLowerCase() : "" }
    def category = categories.find { key, list -> ext in list }?.key ?: "Misc"
    return category
}

/**
 * Safe Move with Collision Handling.
 * If file exists, appends _1, _2, etc.
 */
def safeMove(File source, File destDir) {
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
 * Scans the directory and updates the UI status.
 */
def updateStatus() {
    if (selectedDir && selectedDir.exists()) {
        fileCount = 0
        selectedDir.eachFile { file ->
            if (!file.hidden && !file.isDirectory() && file.name != "FolderSieve.groovy") {
                fileCount++
            }
        }
        appStatusLabel.text = "<html>Folder: ${selectedDir.name}<br/>Files: ${fileCount}</html>"
    } else {
        appStatusLabel.text = "No Folder Selected"
    }
}

/**
 * The Sieve: Organizes files into Organized/ subfolders.
 */
def organizeFolder() {
    if (!selectedDir) return
    
    int movedCount = 0
    File organizedDir = new File(selectedDir, "Organized")
    
    selectedDir.eachFile { file ->
        // Safety constraints: skip self, skip hidden, skip directories
        if (file.isFile() && !file.hidden && file.name != "FolderSieve.groovy") {
            String category = getCategory(file.name)
            File targetDir = new File(organizedDir, category)
            safeMove(file, targetDir)
            movedCount++
        }
    }
    
    updateStatus()
    JOptionPane.showMessageDialog(appFrame, "Organization Complete!\nMoved ${movedCount} files.", "Success", JOptionPane.INFORMATION_MESSAGE)
}

/**
 * The Purge: Deletes junk files after confirmation.
 */
def purgeFolder() {
    if (!selectedDir) return
    
    def junkFiles = []
    selectedDir.eachFile { file ->
        if (file.isFile() && !file.hidden && file.name =~ junkPattern) {
            junkFiles << file
        }
    }
    
    if (junkFiles.isEmpty()) {
        JOptionPane.showMessageDialog(appFrame, "No junk files found.", "Clean", JOptionPane.INFORMATION_MESSAGE)
        return
    }
    
    int confirm = JOptionPane.showConfirmDialog(appFrame, 
        "Found ${junkFiles.size()} junk files.\nDo you want to safely purge them?", 
        "The Purge", JOptionPane.YES_NO_OPTION)
        
    if (confirm == JOptionPane.YES_OPTION) {
        junkFiles.each { it.delete() }
        updateStatus()
        JOptionPane.showMessageDialog(appFrame, "Junk Purge Complete.", "Success", JOptionPane.INFORMATION_MESSAGE)
    }
}

// 5. GUI Construction with SwingBuilder
def swing = new SwingBuilder()
swing.edt {
    appFrame = frame(title: 'FolderSieve', size: [300, 220], 
                  defaultCloseOperation: JFrame.EXIT_ON_CLOSE, 
                  resizable: false, alwaysOnTop: true, show: true) {
        
        borderLayout()
        
        panel(constraints: BorderLayout.NORTH, border: BorderFactory.createEmptyBorder(10, 10, 5, 10)) {
            appStatusLabel = label(text: 'No Folder Selected', horizontalAlignment: JLabel.CENTER)
        }
        
        panel(constraints: BorderLayout.CENTER, layout: new GridLayout(3, 1, 5, 5), border: BorderFactory.createEmptyBorder(5, 20, 5, 20)) {
            button(text: 'Select Folder', actionPerformed: {
                def chooser = new JFileChooser(dialogTitle: "Choose a folder to sieve", fileSelectionMode: JFileChooser.DIRECTORIES_ONLY)
                if (chooser.showOpenDialog(appFrame) == JFileChooser.APPROVE_OPTION) {
                    selectedDir = chooser.selectedFile
                    updateStatus()
                }
            })
            button(text: 'Organize (The Sieve)', actionPerformed: { organizeFolder() })
            button(text: 'Purge Junk (The Purge)', actionPerformed: { purgeFolder() })
        }
        
        panel(constraints: BorderLayout.SOUTH, border: BorderFactory.createEmptyBorder(5, 10, 10, 10)) {
            label(text: 'v1.0 - Groovy Janitor', font: new Font('SansSerif', Font.ITALIC, 10), foreground: Color.GRAY)
        }
    }
}

// 6. Automation: Refresh Timer (every 5 seconds)
def timer = new Timer(5000, { e ->
    swing.edt { updateStatus() }
} as java.awt.event.ActionListener)
timer.start()
