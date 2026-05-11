import groovy.swing.SwingBuilder
import javax.swing.*
import java.awt.*
import javax.imageio.ImageIO
import groovy.transform.Field

/**
 * FolderSieve - The Universal Groovy Janitor
 * Main UI and Orchestration.
 */

// 1. Setup Look and Feel
UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())

// 2. State Variables
@Field File selectedDir = null
@Field int fileCount = 0
@Field appStatusLabel
@Field appFrame

// 3. UI Helper Methods

def updateStatus() {
    if (selectedDir && selectedDir.exists()) {
        fileCount = SieveCore.countFiles(selectedDir)
        appStatusLabel.text = "<html>Folder: ${selectedDir.name}<br/>Files: ${fileCount}</html>"
    } else {
        appStatusLabel.text = "No Folder Selected"
    }
}

def organizeFolder() {
    if (!selectedDir) return
    
    int movedCount = 0
    File organizedDir = new File(selectedDir, "Organized")
    
    selectedDir.eachFile { file ->
        if (file.isFile() && !file.hidden && !SieveCore.isAppFile(file.name)) {
            String category = SieveCore.getCategory(file.name)
            File targetDir = new File(organizedDir, category)
            SieveCore.safeMove(file, targetDir)
            movedCount++
        }
    }
    
    updateStatus()
    JOptionPane.showMessageDialog(appFrame, "Organization Complete!\nMoved ${movedCount} files.", "Success", JOptionPane.INFORMATION_MESSAGE)
}

def purgeFolder() {
    if (!selectedDir) return
    
    def junkFiles = []
    selectedDir.eachFile { file ->
        if (file.isFile() && !file.hidden && file.name =~ SieveConstants.JUNK_PATTERN) {
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

// 4. GUI Construction
def swing = new SwingBuilder()
swing.edt {
    // Pre-load icon if available
    def logoImage = null
    try {
        File iconFile = new File("assets/logo.png")
        if (iconFile.exists()) {
            logoImage = ImageIO.read(iconFile)
        }
    } catch (Exception e) {
        println "Could not load icon: ${e.message}"
    }

    appFrame = frame(title: 'FolderSieve', size: [300, 220], 
                  defaultCloseOperation: JFrame.EXIT_ON_CLOSE, 
                  resizable: false, alwaysOnTop: true, show: true,
                  iconImage: logoImage) {
        
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
            label(text: 'v1.1 - Multi-File Edition', font: new Font('SansSerif', Font.ITALIC, 10), foreground: Color.GRAY)
        }
    }
}

// 5. Automation: Refresh Timer
def timer = new Timer(5000, { e ->
    swing.edt { updateStatus() }
} as java.awt.event.ActionListener)
timer.start()
