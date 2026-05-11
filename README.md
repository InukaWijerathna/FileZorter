# FolderSieve (The Universal Groovy Janitor)

FolderSieve is a compact, standalone Groovy utility designed to keep your directories clean and organized.

## Features
- **The Sieve**: Automatically categorizes files into subfolders (Images, Documents, Code, Media, Misc).
- **The Purge**: Safely identifies and deletes temporary junk files (`.tmp`, `.log`, `.bak`, etc.).
- **Safe-Move**: Intelligent collision handling prevents overwriting existing files by auto-incrementing filenames.
- **Native Experience**: Native Windows look and feel with an "Always-on-top" window.
- **Real-time Monitoring**: Automatically refreshes the file count of the selected folder every 5 seconds.

## Requirements
- [Groovy](https://groovy.apache.org/download.html) installed on your system.

## Usage
1. Open a terminal in the folder containing `FolderSieve.groovy`.
2. Run the command:
   ```powershell
   groovy FolderSieve.groovy
   ```
3. Use the **Select Folder** button to pick a target directory.
4. Click **Organize** to categorize files or **Purge Junk** to clean up.

## Safety First
- **Non-Destructive**: Files are moved into an `Organized/` folder, never deleted unless you use "The Purge".
- **Confirmation**: "The Purge" always asks for confirmation before deleting files.
- **System Protection**: Skips hidden system files and the script itself.
