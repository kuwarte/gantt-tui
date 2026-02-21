#!/bin/bash

set -e

INSTALL_DIR="/usr/local/lib"
BIN_DIR="/usr/local/bin"
CMD="gtt"
JAR="target/GanttTUI-1.0-SNAPSHOT.jar"

echo "[GANTT-TUI] STARTING INSTALLATION..."

if ! command -v java &> /dev/null; then
    echo "[ERROR] JAVA IS NOT INSTALLED. PLEASE INSTALL JAVA 11+ AND TRY AGAIN."
    exit 1
fi
echo "[OK] JAVA FOUND: $(java -version 2>&1 | head -n 1)"

if [ ! -f "$JAR" ]; then
    echo "[ERROR] JAR NOT FOUND AT $JAR"
    echo "[ERROR] MAKE SURE YOU ARE RUNNING THIS FROM THE PROJECT ROOT."
    exit 1
fi
echo "[OK] JAR FOUND: $JAR"

if [ ! -w "$INSTALL_DIR" ] || [ ! -w "$BIN_DIR" ]; then
    echo "[ERROR] PERMISSION DENIED. TRY RUNNING WITH SUDO:"
    echo "        sudo ./install.sh"
    exit 1
fi

if [ -f "$BIN_DIR/$CMD" ]; then
    echo "[INFO] EXISTING INSTALLATION FOUND. REINSTALLING..."
fi

cp "$JAR" "$INSTALL_DIR/GanttTUI.jar"
echo "[OK] JAR COPIED TO $INSTALL_DIR/GanttTUI.jar"

cat > "$BIN_DIR/$CMD" << 'EOF'
#!/bin/bash
java -jar /usr/local/lib/GanttTUI.jar
EOF
chmod +x "$BIN_DIR/$CMD"
echo "[OK] COMMAND REGISTERED: $CMD"

echo ""
echo "[GANTT-TUI] INSTALLATION COMPLETE."
echo "[GANTT-TUI] RUN ANYWHERE WITH: gtt"
