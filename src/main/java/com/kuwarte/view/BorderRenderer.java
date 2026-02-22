package com.kuwarte.view;

import com.googlecode.lanterna.graphics.TextGraphics;
import com.kuwarte.GanttTUI;

/**
 * BorderRenderer: Responsible for drawing the application's structural frame.
 * * This class handles the "Chrome" of the UI:
 * 1. The outer perimeter of the application.
 * 2. The internal dividers between the Task list and the Gantt grid.
 * 3. The Header area containing labels and dates.
 */
public class BorderRenderer implements Renderer {

    @Override
    public void render(TextGraphics tg, Layout layout, GanttTUI app) {
        // --- 1. Character Selection ---
        // We define local variables for all box-drawing parts to support
        // dynamic "skinning" based on the app.thickBorder toggle.
        String h, v, tl, tr, bl, br, mt, mb, ml, mr, mx, hd;

        if (app.thickBorder) {
            h = "═";
            v = "║";
            tl = "╔";
            tr = "╗";
            bl = "╚";
            br = "╝";
            mt = "╦";
            mb = "╩";
            ml = "╠";
            mr = "╣";
            mx = "╬";
            hd = "═";
        } else {
            h = "─";
            v = "│";
            tl = "┌";
            tr = "┐";
            bl = "└";
            br = "┘";
            mt = "┬";
            mb = "┴";
            ml = "├";
            mr = "┤";
            mx = "┼";
            hd = "─";
        }

        // --- 2. Background and Line Colors ---
        tg.setBackgroundColor(GanttView.OD_BG_DARKER);
        tg.setForegroundColor(GanttView.OD_COMMENT);

        // --- 3. Horizontal Line Rendering ---
        // Top border (row 0)
        for (int x = 1; x < layout.width - 1; x++)
            tg.putString(x, 0, h);

        // Header separator (row 2)
        for (int x = 1; x < layout.width - 1; x++)
            tg.putString(x, 2, hd);

        // Bottom border (height - 3: leaves space for Status and Message bars)
        for (int x = 1; x < layout.width - 1; x++)
            tg.putString(x, layout.height - 3, h);

        // --- 4. Vertical Line Rendering ---
        // Draws columns at the left edge, mid-dividers (mid1, mid2), and right edge.
        for (int y = 1; y < layout.height - 3; y++) {
            tg.putString(0, y, v);
            tg.putString(layout.mid1, y, v);
            tg.putString(layout.mid2, y, v);
            tg.putString(layout.width - 1, y, v);
        }

        // --- 5. Intersection/Corner Rendering ---
        // These 'patch' the points where horizontal and vertical lines meet.
        tg.putString(0, 0, tl); // Top Left
        tg.putString(layout.width - 1, 0, tr); // Top Right
        tg.putString(0, layout.height - 3, bl); // Bottom Left
        tg.putString(layout.width - 1, layout.height - 3, br); // Bottom Right

        tg.putString(layout.mid1, 0, mt); // Top T-junction 1
        tg.putString(layout.mid2, 0, mt); // Top T-junction 2
        tg.putString(layout.mid1, layout.height - 3, mb); // Bottom T-junction 1
        tg.putString(layout.mid2, layout.height - 3, mb); // Bottom T-junction 2

        tg.putString(0, 2, ml); // Left Side T-junction
        tg.putString(layout.width - 1, 2, mr); // Right Side T-junction
        tg.putString(layout.mid1, 2, mx); // Cross-junction 1
        tg.putString(layout.mid2, 2, mx); // Cross-junction 2

        // --- 6. Header Text Content ---
        // Fill row 1 with a solid background to ensure no 'gaps' in the color.
        tg.setBackgroundColor(GanttView.OD_BG_DARKER);
        tg.fillRectangle(new com.googlecode.lanterna.TerminalPosition(1, 1),
                new com.googlecode.lanterna.TerminalSize(layout.width - 2, 1), ' ');

        tg.setForegroundColor(GanttView.OD_YELLOW);
        tg.putString(2, 1, "TASK");

        String monthLabel = GanttTUI.MONTH_NAMES[app.currentMonth - 1].toUpperCase() + " " + app.currentYear;
        // Right-align the date label in the final column block
        tg.putString(layout.mid2 + 2, 1, monthLabel);
    }
}
