@echo off

set INSTALL_DIR=%USERPROFILE%\.local\lib
set BIN_DIR=%USERPROFILE%\.local\bin
set CMD=gtt
set JAR=target\GanttTUI-1.0-SNAPSHOT.jar

echo. > "%INSTALL_DIR%\.writetest" 2>nul
if errorlevel 1 (
    echo [ERROR] PERMISSION DENIED. TRY RUNNING AS ADMINISTRATOR.
    exit /b 1
)
del "%INSTALL_DIR%\.writetest" >nul 2>&1

if exist "%BIN_DIR%\%CMD%.bat" (
    echo [INFO] EXISTING INSTALLATION FOUND. REINSTALLING...
)

copy "%JAR%" "%INSTALL_DIR%\GanttTUI.jar" >nul
echo [OK] JAR COPIED TO %INSTALL_DIR%\GanttTUI.jar

echo @echo off > "%BIN_DIR%\%CMD%.bat"
echo java -jar "%INSTALL_DIR%\GanttTUI.jar" >> "%BIN_DIR%\%CMD%.bat"
echo [OK] COMMAND REGISTERED: %CMD%

echo.
echo [GANTT-TUI] INSTALLATION COMPLETE.
echo [GANTT-TUI] RUN ANYWHERE WITH: gtt
