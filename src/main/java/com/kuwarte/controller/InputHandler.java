package com.kuwarte.controller;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.kuwarte.view.GanttView;

import java.io.IOException;

/**
 * InputHandler: A utility for capturing string input within the TUI
 * environment.
 * * This class implements a "modal" input loop. When called, it halts the main
 * application logic and takes over the bottom two rows of the screen to
 * provide a command-line-like experience for the user.
 */
public class InputHandler {

    /**
     * Displays a prompt at the bottom of the screen and captures user keystrokes
     * until Enter or Escape is pressed.
     * * @param screen The active Lanterna screen.
     * 
     * @param prompt      The label shown to the user (e.g., ":rename> ").
     * @param modeOverlay The status text shown in the status bar area (e.g., "
     *                    RENAME ").
     * @return The string entered by the user, or null if the user cancelled via
     *         Escape.
     * @throws IOException If terminal interaction fails.
     */
    public String promptInput(Screen screen, String prompt, String modeOverlay) throws IOException {
        TerminalSize size = screen.getTerminalSize();
        TextGraphics tg = screen.newTextGraphics();
        int width = size.getColumns();
        int height = size.getRows();
        StringBuilder input = new StringBuilder();

        // Blocking Modal Loop
        while (true) {
            // 1. Draw the "Mode Indicator" on the Status Bar row (height - 2)
            tg.setBackgroundColor(GanttView.OD_BG_DARKER);
            tg.setForegroundColor(GanttView.OD_CYAN);
            tg.putString(0, height - 2, modeOverlay);

            // 2. Draw the "Prompt and Input Field" on the Message Bar row (height - 1)
            tg.setBackgroundColor(GanttView.OD_BG_DARK);
            tg.setForegroundColor(GanttView.OD_COMMENT);

            // Clear the row first with a full-width empty string
            tg.putString(0, height - 1, String.format("%-" + width + "s", ""));

            // Draw the prompt, the current input, and a simple underscore cursor "_"
            tg.putString(0, height - 1, prompt + input.toString() + "_");

            screen.refresh();

            // 3. Process Keystrokes
            KeyStroke key = screen.readInput();

            // Submit
            if (key.getKeyType() == KeyType.Enter)
                return input.toString();

            // Cancel
            if (key.getKeyType() == KeyType.Escape)
                return null;

            // Edit
            if (key.getKeyType() == KeyType.Backspace && input.length() > 0) {
                input.deleteCharAt(input.length() - 1);
            }
            // Append
            else if (key.getKeyType() == KeyType.Character) {
                input.append(key.getCharacter());
            }
        }
    }
}
