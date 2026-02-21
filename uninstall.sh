#!/bin/bash

set -e

INSTALL_DIR="/usr/local/lib"
BIN_DIR="/usr/local/bin"
CMD="gtt"

echo "[GANTT-TUI] STARTING UNINSTALLATION..."

if [ ! -w "$INSTALL_DIR" ] || [ ! -w "$BIN_DIR" ]; then
    echo "[ERROR] PERMISSION DENIED. TRY RUNNING WITH SUDO:"
    echo "        sudo ./uninstall.sh"
    exit 1
fi

if [ -f "$BIN_DIR/$CMD" ]; then
    rm -f "$BIN_DIR/$CMD"
    echo "[OK] COMMAND REMOVED: $BIN_DIR/$CMD"
else
    echo "[WARN] COMMAND NOT FOUND: $BIN_DIR/$CMD — SKIPPING."
fi

if [ -f "$INSTALL_DIR/GanttTUI.jar" ]; then
    rm -f "$INSTALL_DIR/GanttTUI.jar"
    echo "[OK] JAR REMOVED: $INSTALL_DIR/GanttTUI.jar"
else
    echo "[WARN] JAR NOT FOUND: $INSTALL_DIR/GanttTUI.jar — SKIPPING."
fi

echo ""
echo "[GANTT-TUI] UNINSTALLATION COMPLETE."
