import groovy.swing.SwingBuilder
import javax.swing.*
import javax.swing.table.DefaultTableModel
import java.awt.*

/**
 * Advanced Key-Value configuration UI for FileZorta.
 */
class ZortaConfigUI {

    static void showConfig(JFrame parent) {
        def swing = new SwingBuilder()
        
        // Prepare table data from existing categories
        def columnNames = ['Category', 'Extensions (comma separated)']
        def data = ZortaConstants.CATEGORIES.collect { k, v -> [k, v.join(", ")] } as Object[][]
        def model = new DefaultTableModel(data, columnNames as Object[])

        def dialog = swing.dialog(id: 'configDialog', title: 'FileZorta Configuration', modal: true, owner: parent, size: [500, 450], resizable: true) {
            borderLayout()
            
            tabbedPane(constraints: BorderLayout.CENTER) {
                // Tab 1: Categories Editor
                panel(title: 'Categories', border: BorderFactory.createEmptyBorder(10, 10, 10, 10)) {
                    borderLayout()
                    scrollPane(constraints: BorderLayout.CENTER) {
                        table(id: 'categoryTable', model: model)
                    }
                    panel(constraints: BorderLayout.SOUTH) {
                        flowLayout(alignment: FlowLayout.LEFT)
                        button(text: '+ Add Category', actionPerformed: {
                            model.addRow(['NewCategory', '.ext1, .ext2'] as Object[])
                        })
                        button(text: '- Remove Selected', actionPerformed: {
                            int selected = swing.categoryTable.selectedRow
                            if (selected != -1) {
                                String categoryName = model.getValueAt(selected, 0)
                                int confirm = JOptionPane.showConfirmDialog(swing.configDialog, 
                                    "Are you sure you want to remove the category '${categoryName}'?", 
                                    "Confirm Removal", JOptionPane.YES_NO_OPTION)
                                if (confirm == JOptionPane.YES_OPTION) {
                                    model.removeRow(selected)
                                }
                            }
                        })
                    }
                }
                
                // Tab 2: General Settings
                panel(title: 'General', border: BorderFactory.createEmptyBorder(10, 10, 10, 10)) {
                    gridLayout(columns: 1, rows: 6, vgap: 5)
                    label(text: "Junk File Pattern (Regex):")
                    textField(text: ZortaConstants.junkPatternString, id: 'junkPatternField')
                    label(text: "") // Spacer
                    checkBox(text: "Always on top", selected: ZortaConstants.alwaysOnTop, id: 'alwaysOnTopCheck')
                    label(text: "Changes to 'Always on top' require restart.", font: new Font('SansSerif', Font.ITALIC, 10))
                }
            }
            
            panel(constraints: BorderLayout.SOUTH, border: BorderFactory.createEmptyBorder(0, 10, 10, 10)) {
                flowLayout(alignment: FlowLayout.RIGHT)
                button(text: 'Save', actionPerformed: {
                    // Stop cell editing to ensure the latest value is captured
                    if (swing.categoryTable.isEditing()) {
                        swing.categoryTable.cellEditor.stopCellEditing()
                    }

                    // 1. Save Categories from Table
                    Map newCategories = [:]
                    for (int i = 0; i < model.rowCount; i++) {
                        String key = model.getValueAt(i, 0).toString().trim()
                        String val = model.getValueAt(i, 1).toString().trim()
                        if (key) {
                            newCategories[key] = val.split(",").collect { it.trim() }.findAll { it }
                        }
                    }
                    ZortaConstants.CATEGORIES = newCategories
                    
                    // 2. Save General Settings
                    ZortaConstants.junkPatternString = swing.junkPatternField.text
                    ZortaConstants.alwaysOnTop = swing.alwaysOnTopCheck.selected
                    
                    // Apply immediate UI changes if possible
                    parent.alwaysOnTop = ZortaConstants.alwaysOnTop
                    
                    ZortaConstants.saveConfig()
                    swing.configDialog.dispose()
                })
                button(text: 'Cancel', actionPerformed: { swing.configDialog.dispose() })
            }
        }
        
        dialog.setLocationRelativeTo(parent)
        dialog.show()
    }
}
