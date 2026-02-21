@echo off

set INSTALL_DIR=%USERPROFILE%\.local\lib
set BIN_DIR=%USERPROFILE%\.local\bin
set CMD=gtt
set JAR=target\GanttTUI-1.0-SNAPSHOT.jar

echo [GANTT-TUI] STARTING INSTALLATION...

where java >nul 2>&1
if errorlevel 1 (
    echo [ERROR] JAVA IS NOT INSTALLED. PLEASE INSTALL JAVA 11+ AND TRY AGAIN.
    exit /b 1
)
echo [OK] JAVA FOUND

if not exist "%JAR%" (
    echo [ERROR] JAR NOT FOUND AT %JAR%
    echo [ERROR] MAKE SURE YOU ARE RUNNING THIS FROM THE PROJECT ROOT.
    exit /b 1
)
echo [OK] JAR FOUND: %JAR%

if not exist "%INSTALL_DIR%" mkdir "%INSTALL_DIR%"
if not exist "%BIN_DIR%" mkdir "%BIN_DIR%"

if exist "%BIN_DIR%\%CMD%.bat" (
    echo [INFO] EXISTING INSTALLATION FOUND. REINSTALLING...
)

copy "%JAR%" "%INSTALL_DIR%\GanttTUI.jar" >nul
echo [OK] JAR COPIED TO %INSTALL_DIR%\GanttTUI.jar

echo @echo off > "%BIN_DIR%\%CMD%.bat"
echo java -jar "%INSTALL_DIR%\GanttTUI.jar" >> "%BIN_DIR%\%CMD%.bat"
echo [OK] COMMAND REGISTERED: %BIN_DIR%\%CMD%.bat

setx PATH "%BIN_DIR%;%PATH%" >nul
echo [OK] ADDED %BIN_DIR% TO YOUR PATH

echo.
echo [GANTT-TUI] INSTALLATION COMPLETE.
echo [GANTT-TUI] RESTART YOUR TERMINAL THEN RUN: gtt
