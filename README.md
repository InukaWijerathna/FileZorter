# FileZorter - The Universal File Janitor

**FileZorter** is a high-performance, standalone directory orchestration utility that synthesizes automated file categorization with tactical junk-data elimination. It serves as a comprehensive "janitor" for digital workspaces, ensuring zero-latency organization through intelligent pattern matching.

---

## 🕹️ System Operation

Designed as a native desktop utility, the system operates as a non-destructive file filter for any local directory.

1. **Geospatial Target:** Deploy the "Select Folder" interface to designate the primary operations sector (target directory).
2. **The Sieve Protocol:** Activating "Organize" triggers a recursive sweep, sorting files into optimized jurisdictions (Images, Docs, Code, etc.) based on telemetry signatures.
3. **The Purge Sequence:** A specialized cleanup engine that identifies atmospheric junk (.tmp, .log, .bak) and executes a safe terminal-delete after user confirmation.
4. **Logic Calibration:** Access the **Settings > Configuration** module to override default categorization maps and junk regex patterns in real-time.

---

## ⚙️ Technical Architecture

### 📡 Data Telemetry & Persistence
- **JSON Configuration Engine:** Utilizes a persistent `config.json` framework to store user-defined categorization maps and UI preferences.
- **Regex Pattern Matching:** Employs a high-precision Regular Expression matrix to identify and isolate temporary "junk" files across varied file systems.
- **Safe-Move Collision Logic:** Implements an automated name-reconciliation algorithm that prevents data loss by auto-incrementing filenames (e.g., `report_1.pdf`) during destination conflicts.

### 📼 Visual & Interface Engineering
- **Native Look & Feel:** Leverages the Windows System `UIManager` to provide a high-fidelity, compact native utility aesthetic.
- **SwingBuilder Framework:** A declarative UI engine that orchestrates complex Java Swing components into a reactive, multi-module interface.
- **Dynamic Status Downlink:** Features a 5000ms polling cycle that continuously monitors and refreshes file counts in the active operations sector.

### 🧬 Logical Framework
- **Modular Class Architecture:** Divided into specialized components (`ZorterCore`, `ZorterConstants`, `ZorterConfigUI`) for maximum stability and ease of future feature expansion.
- **Safety Constraints:** Implements strict metadata filtering to skip hidden system files and application source code, ensuring the system never "sieves" itself.

---

## 🛠️ Stack Summary
- **Core Engine**: Groovy 4.0 (Enhanced GDK)
- **GUI Architecture**: Java Swing (via SwingBuilder)
- **Persistence**: Groovy JSON (Slurper/Output)
- **Threading**: EDT (Event Dispatch Thread) synchronization

---

## ⚠️ Safety Protocols
- **Non-Destructive Routing:** Files are moved into a designated `Organized/` sub-sector; original data is never overwritten.
- **Mandatory Confirmation:** "The Purge" and "Category Removal" actions are locked behind verification dialogs to prevent accidental data loss.
- **System Isolation:** Automatically ignores hidden OS files (e.g., `.ds_store`, `desktop.ini`) to maintain system integrity.

*Designed for the digital outrun.* 🏙️🧹💾
