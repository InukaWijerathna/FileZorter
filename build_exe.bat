@echo off
title FileZorter Packaging Pipeline
color 0B
echo =================================================================
echo                 FileZorter Advanced Packaging Pipeline
echo =================================================================
echo.

:: 1. Verify Java / JDK Installation
echo [*] Checking for Java Development Kit (JDK)...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    color 0C
    echo [ERROR] Java/JDK was not found on your system.
    echo         Please install JDK 8 or higher to compile this application.
    echo         You can download it from: https://adoptium.net/
    echo.
    pause
    exit /b 1
)
echo [OK] JDK is installed and configured in your environment.
echo.

:: 2. Locate or Download Gradle
set GRADLE_CMD=gradle
where gradle >nul 2>&1
if %errorlevel% neq 0 (
    echo [*] Gradle not found in PATH. Checking for local wrapper...
    if exist "gradlew.bat" (
        set GRADLE_CMD=gradlew.bat
        echo [OK] Using local Gradle wrapper.
    ) else (
        echo [*] Local wrapper not found. Downloading portable Gradle 8.4...
        echo     (This may take a moment depending on your internet connection...)
        
        :: Download Gradle zip using PowerShell
        powershell -Command "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri 'https://services.gradle.org/distributions/gradle-8.4-bin.zip' -OutFile 'gradle-tmp.zip'"
        
        if not exist "gradle-tmp.zip" (
            color 0C
            echo [ERROR] Failed to download Gradle. Please check your internet connection.
            pause
            exit /b 1
        )
        
        echo [*] Extracting Gradle...
        powershell -Command "Expand-Archive -Path 'gradle-tmp.zip' -DestinationPath '.'"
        
        :: Clean up temporary zip file
        del gradle-tmp.zip
        
        :: Set PATH to portable gradle
        set GRADLE_CMD=gradle-8.4\bin\gradle.bat
        echo [OK] Portable Gradle configured successfully.
    )
) else (
    echo [OK] Using system installed Gradle.
)
echo.

:: 3. Setup Asset Paths for execution
echo [*] Setting up directories and assets...
if exist "src\assets" (
    if not exist "assets" mkdir assets
    xcopy /E /I /Y src\assets assets >nul
    echo [OK] Copied assets folder to root directory for native execution.
)
if exist "src\config.json" (
    if not exist "config.json" copy /Y src\config.json config.json >nul
    echo [OK] Copied default config.json to root directory.
)
echo.

:: 4. Build shadowJar and Launch4j Executable
if exist "%USERPROFILE%\.gradle\caches\modules-2\files-2.1\net.sf.launch4j" (
    echo [*] Clearing potentially corrupted Launch4j cache to ensure a fresh download...
    rmdir /S /Q "%USERPROFILE%\.gradle\caches\modules-2\files-2.1\net.sf.launch4j"
)
echo [*] Compiling Groovy scripts and generating native Windows EXE...
call %GRADLE_CMD% shadowJar copyExe --no-daemon --refresh-dependencies

if %errorlevel% neq 0 (
    color 0C
    echo.
    echo [ERROR] Build failed. Please inspect the log outputs above.
    pause
    exit /b 1
)
echo.
echo [SUCCESS] Standalone native executable built: FileZorter.exe
echo.

:: 5. Compile Inno Setup Installer
echo =================================================================
echo                 Inno Setup Installer Compilation
echo =================================================================
echo.
echo [*] Searching for Inno Setup compiler (ISCC.exe)...

set ISCC_PATH=
where ISCC.exe >nul 2>&1
if %errorlevel% eq 0 (
    set ISCC_PATH=ISCC.exe
    echo [OK] Found ISCC.exe in system PATH.
) else (
    :: Search common installation directories
    if exist "C:\Program Files (x86)\Inno Setup 6\ISCC.exe" (
        set ISCC_PATH="C:\Program Files (x86)\Inno Setup 6\ISCC.exe"
    ) else if exist "C:\Program Files\Inno Setup 6\ISCC.exe" (
        set ISCC_PATH="C:\Program Files\Inno Setup 6\ISCC.exe"
    ) else if exist "C:\Program Files (x86)\Inno Setup 5\ISCC.exe" (
        set ISCC_PATH="C:\Program Files (x86)\Inno Setup 5\ISCC.exe"
    ) else if exist "C:\Program Files\Inno Setup 5\ISCC.exe" (
        set ISCC_PATH="C:\Program Files\Inno Setup 5\ISCC.exe"
    )
)

if "%ISCC_PATH%"=="" (
    color 0E
    echo [WARNING] Inno Setup compiler (ISCC.exe) was not found on your system.
    echo           Please install Inno Setup 6+ to build the setup installer executable.
    echo           You can download it for free from: https://jrsoftware.org/isdl.php
    echo.
    echo           *Note: The native application 'FileZorter.exe' is fully compiled and
    echo                  functional inside the root directory!
    echo.
) else (
    echo [OK] Found Inno Setup Compiler at: %ISCC_PATH%
    echo [*] Compiling installer package...
    echo.
    
    if not exist "releases" mkdir releases
    call %ISCC_PATH% FileZorter.iss
    
    if %errorlevel% neq 0 (
        color 0C
        echo.
        echo [ERROR] Inno Setup compilation failed.
        pause
        exit /b 1
    )
    
    echo.
    echo =================================================================
    echo                 SUCCESS: FileZorterSetup.exe Generated!
    echo =================================================================
    echo.
    echo [*] Your professional setup installer has been built successfully:
    echo     =^> releases\FileZorterSetup.exe
    echo.
)

:: 6. Cleanup
if exist "gradle-8.4" (
    echo [*] Cleaning up portable Gradle files...
    rmdir /S /Q gradle-8.4
)
echo.
echo [*] Packaging pipeline complete.
echo.
pause
