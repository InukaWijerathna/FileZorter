# FileZorter — The Universal File Janitor

 **FileZorter** is a lightweight, standalone Windows utility that automatically organizes cluttered directories and eliminates junk files — with a clean native GUI and zero configuration required out of the box.

---

## ✨ Features

- 📁 **Smart File Organization** — Sorts files into categorized sub-folders (Images, Documents, Code, Media, etc.) based on file extensions.
- 🗑️ **Junk Purge** — Scans for and safely deletes temporary files (`.tmp`, `.log`, `.bak`, `.crdownload`) after user confirmation.
- ⚙️ **Live Configuration Editor** — Edit category rules and junk patterns in real-time through the built-in Settings panel without restarting.
- 🔄 **Auto-Refresh Status** — Automatically polls the selected directory every 5 seconds to display up-to-date file counts.
- 🛡️ **Non-Destructive** — Files are moved into an `Organized/` sub-folder; originals are never overwritten. Filenames are auto-incremented on conflict (e.g., `report_1.pdf`).
- 🖼️ **Native Look & Feel** — Uses the Windows System UI theme for a compact, familiar desktop utility aesthetic.

---

## 🚀 Installation

### Option 1: Run the Installer *(Recommended)*
1. Download **`FileZorterSetup.exe`** from the [Releases](https://github.com/InukaWijerathna/FileZorter/releases) page.
2. Run the installer — no administrator rights required.
3. FileZorter will be installed to your user profile (`%LocalAppData%\FileZorter`) with a Start Menu shortcut.

### Option 2: Build from Source
**Prerequisites:** [JDK 8+](https://adoptium.net/) and [Inno Setup 6+](https://jrsoftware.org/isdl.php) *(for installer packaging)*.

```
1. Clone the repository
2. Double-click build_exe.bat
3. FileZorter.exe is generated in build\launch4j\
4. FileZorterSetup.exe is generated in releases\
```

---

## 🕹️ How to Use

1. **Select Folder** — Click "Select Folder" to choose the directory you want to clean up.
2. **Organize** — Click "Organize (The Sieve)" to sort all files into categorized sub-folders inside an `Organized/` directory.
3. **Purge Junk** — Click "Purge Junk (The Purge)" to detect and delete temporary junk files after confirmation.
4. **Configure** — Go to **Settings → Configuration** to customize category rules and junk file patterns.

---

## 🏗️ Project Structure

```
FileZorter/
├── src/
│   ├── FileZorter.groovy       # Main UI & orchestration
│   ├── ZorterCore.groovy       # File operations (move, categorize, count)
│   ├── ZorterConstants.groovy  # Config loader/saver & default categories
│   ├── ZorterConfigUI.groovy   # Configuration editor UI
│   ├── config.json             # Default category rules & settings
│   └── assets/
│       ├── logo.ico            # Application icon (multi-resolution)
│       └── logo.png            # Source logo image
├── build.gradle                # Gradle build & packaging config
├── settings.gradle             # Gradle project settings
├── FileZorter.iss              # Inno Setup installer script
├── build_exe.bat               # One-click build & installer pipeline
├── LICENSE
└── README.md
```

---

## ⚙️ Technical Stack

| Component        | Technology                          |
|------------------|-------------------------------------|
| Language         | Groovy 3.0 (JVM)                    |
| GUI              | Java Swing (SwingBuilder)           |
| Persistence      | Groovy JSON (JsonSlurper/JsonOutput) |
| Threading        | EDT (Event Dispatch Thread)         |
| Packaging        | Gradle + Shadow JAR + Launch4j      |
| Installer        | Inno Setup 6                        |

---

## ⚠️ Safety

- Hidden OS files (`.ds_store`, `desktop.ini`, etc.) are automatically skipped.
- Application source files are never moved by the organizer.
- "Purge" and "Remove Category" actions require explicit user confirmation before executing.

---

*Designed for the digital outrun.* 🏙️🧹💾
