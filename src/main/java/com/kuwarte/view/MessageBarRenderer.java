package com.kuwarte.view;

import com.googlecode.lanterna.graphics.TextGraphics;
import com.kuwarte.GanttTUI;

/**
 * MessageBarRenderer: The interactive footer of the application.
 * * This class renders the final line of the terminal (height - 1).
 * It is responsible for displaying system feedback, keybinding help,
 * and command prompts.
 */
public class MessageBarRenderer implements Renderer {

    /**
     * Renders the message bar at the absolute bottom of the terminal.
     * * @param tg The graphics object used for drawing.
     * 
     * @param layout The current screen dimensions and layout metadata.
     * @param app    The main application state containing the message string.
     */
    @Override
    public void render(TextGraphics tg, Layout layout, GanttTUI app) {
        // Set the distinct 'dark' background to separate the message bar from the main
        // chart
        tg.setBackgroundColor(GanttView.BG_DARK);
        tg.setForegroundColor(GanttView.COMMENT);

        // 1. CLEARING THE LINE
        // We use String.format to create a string of spaces equal to the terminal
        // width.
        // This ensures the background color is applied to the entire row, even where
        // there is no text.
        tg.putString(0, layout.height - 1, String.format("%-" + layout.width + "s", ""));

        // 2. RENDERING THE CONTENT
        // We prefix the message with a colon ':' to follow the standard TUI
        // command-line aesthetic.
        String displayContent = app.message.isEmpty() ? ":" : ": " + app.message;
        tg.putString(0, layout.height - 1, displayContent);
    }
}
