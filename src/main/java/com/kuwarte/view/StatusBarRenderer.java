package com.kuwarte.view;

import com.googlecode.lanterna.graphics.TextGraphics;
import com.kuwarte.GanttTUI;
import com.kuwarte.model.Task;
import java.util.List;

/**
 * StatusBarRenderer: Renders the informative metadata bar near the bottom.
 * * This class handles the 'Powerline' style bar located at row (height - 2).
 * It displays:
 * 1. Active Mode (Normal/Insert/etc.)
 * 2. Workspace name
 * 3. Selection statistics (e.g., Task 2/10)
 * 4. Currently viewed Month/Year
 */
public class StatusBarRenderer implements Renderer {
    @Override
    public void render(TextGraphics tg, Layout layout, GanttTUI app) {
        // Positioned exactly one row above the MessageBar
        int statusY = layout.height - 2;
        List<Task> visible = app.visibleTasks();

        // --- 1. Background Setup ---
        tg.setBackgroundColor(GanttView.BG_DARKER);
        // Clear the entire line to ensure the background color is uniform
        tg.putString(0, statusY, String.format("%" + layout.width + "s", ""));

        // --- 2. Left Segment: Mode Indicator ---
        // Uses high-contrast Green background (classic Vim style)
        tg.setForegroundColor(GanttView.BG_DARKER);
        tg.setBackgroundColor(GanttView.GREEN);
        String modeStr = " NORMAL ";
        tg.putString(0, statusY, modeStr);

        // --- 3. Middle Segment: Workspace Branding ---
        tg.setBackgroundColor(GanttView.BG_DARKER);
        tg.setForegroundColor(GanttView.FG);

        String brand = " [" + app.config.workspaceName + "] ";
        tg.putString(modeStr.length(), statusY, brand);

        // --- 4. Right Segment: Status & Date ---
        // Displays "Task X/Y" and "MMM YYYY" (e.g., Task 1/5 Feb 2026)
        tg.setForegroundColor(GanttView.YELLOW);
        String progressStr = String.format(" Task %d/%d  %s %d ",
                visible.isEmpty() ? 0 : app.selectedIndex + 1,
                visible.size(),
                GanttTUI.MONTH_NAMES[app.currentMonth - 1].substring(0, 3),
                app.currentYear);

        // Right-align the status string by subtracting its length from the total width
        tg.putString(layout.width - progressStr.length(), statusY, progressStr);
    }
}
