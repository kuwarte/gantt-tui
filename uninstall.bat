@echo off

set INSTALL_DIR=%USERPROFILE%\.local\lib
set BIN_DIR=%USERPROFILE%\.local\bin
set CMD=gtt

echo [GANTT-TUI] STARTING UNINSTALLATION...

if exist "%BIN_DIR%\%CMD%.bat" (
    del "%BIN_DIR%\%CMD%.bat" >nul
    echo [OK] COMMAND REMOVED: %BIN_DIR%\%CMD%.bat
) else (
    echo [WARN] COMMAND NOT FOUND: %BIN_DIR%\%CMD%.bat -- SKIPPING.
)

if exist "%INSTALL_DIR%\GanttTUI.jar" (
    del "%INSTALL_DIR%\GanttTUI.jar" >nul
    echo [OK] JAR REMOVED: %INSTALL_DIR%\GanttTUI.jar
) else (
    echo [WARN] JAR NOT FOUND: %INSTALL_DIR%\GanttTUI.jar -- SKIPPING.
)

echo.
echo [GANTT-TUI] UNINSTALLATION COMPLETE.
