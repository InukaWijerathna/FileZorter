import groovy.swing.SwingBuilder
import javax.swing.*
import java.awt.*
import javax.imageio.ImageIO
import groovy.transform.Field

/**
 * FileZorter - The Universal File Janitor
 * Main UI and Orchestration.
 */

// 1. Setup Look and Feel
UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
UIManager.put("MenuItem.maxCheckIconWidth", 0)
UIManager.put("MenuItem.checkIconOffset", 0)

// 2. State Variables
@Field File selectedDir = null
@Field int fileCount = 0
@Field appStatusLabel
@Field appFrame

// 3. UI Helper Methods

def updateStatus() {
    if (selectedDir && selectedDir.exists()) {
        fileCount = ZorterCore.countFiles(selectedDir)
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
        if (file.isFile() && !file.hidden && !ZorterCore.isAppFile(file.name)) {
            String category = ZorterCore.getCategory(file.name)
            File targetDir = new File(organizedDir, category)
            ZorterCore.safeMove(file, targetDir)
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
        if (file.isFile() && !file.hidden && file.name =~ ZorterConstants.getJunkPattern()) {
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

    appFrame = frame(title: 'FileZorter', size: [300, 240], 
                  defaultCloseOperation: JFrame.EXIT_ON_CLOSE, 
                  resizable: false, alwaysOnTop: ZorterConstants.alwaysOnTop, show: true,
                  iconImage: logoImage) {
        
        menuBar {
            menu(text: 'File') {
                menuItem(text: 'Exit', actionPerformed: { System.exit(0) })
            }
            menu(text: 'Settings') {
                menuItem(text: 'Configuration', actionPerformed: { ZorterConfigUI.showConfig(appFrame) })
            }
            menu(text: 'Help') {
                menuItem(text: 'About', actionPerformed: {
                    JOptionPane.showMessageDialog(appFrame, "FileZorter v1.1\nThe Universal File Janitor", "About", JOptionPane.INFORMATION_MESSAGE)
                })
            }
        }
        
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
            label(text: 'v1.1 - FileZorter', font: new Font('SansSerif', Font.ITALIC, 10), foreground: Color.GRAY)
        }
    }
}

// 5. Automation: Refresh Timer
def timer = new Timer(5000, { e ->
    swing.edt { updateStatus() }
} as java.awt.event.ActionListener)
timer.start()
