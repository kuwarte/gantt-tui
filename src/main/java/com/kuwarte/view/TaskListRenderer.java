package com.kuwarte.view;

import com.googlecode.lanterna.graphics.TextGraphics;
import com.kuwarte.GanttTUI;
import com.kuwarte.model.Task;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * TaskListRenderer: The primary data visualization engine.
 * * This class renders the triple-pane task view:
 * 1. LEFT: Task names with selection highlighting.
 * 2. MIDDLE: The Gantt chart bars (Calculated based on dates and terminal
 * width).
 * 3. RIGHT: Date range labels for quick reference.
 */
public class TaskListRenderer implements Renderer {

    @Override
    public void render(TextGraphics tg, Layout layout, GanttTUI app) {
        // --- 1. Temporal & Spatial Setup ---
        YearMonth ym = YearMonth.of(app.currentYear, app.currentMonth);
        int daysInMonth = ym.lengthOfMonth();
        LocalDate monthStart = ym.atDay(1);
        LocalDate monthEnd = ym.atEndOfMonth();

        List<Task> visible = app.visibleTasks();
        app.clampSelection(visible); // Ensure selection is valid after filtering

        int startY = 3; // Starts after the Header and Ruler (rows 0, 1, 2)
        int rulerOffset = layout.mid1 + 1;
        int rulerWidth = layout.mid2 - layout.mid1 - 1;
        int colsPerDay = Math.max(1, rulerWidth / daysInMonth);

        // --- 2. Iterate Through Visible Tasks ---
        for (int i = 0; i < visible.size(); i++) {
            Task t = visible.get(i);
            int y = startY + i;

            // Boundary Check: Stop rendering if we run out of screen rows
            if (y >= layout.height - 3)
                break;

            boolean isSelected = i == app.selectedIndex;

            // --- 3. Render Task Name (Left Panel) ---
            if (isSelected) {
                tg.setBackgroundColor(GanttView.OD_BG); // Highlight selection
                tg.setForegroundColor(GanttView.OD_FG);
                tg.putString(1, y, String.format(" %-" + (layout.mid1 - 2) + "s", t.name));
            } else {
                tg.setBackgroundColor(GanttView.OD_BG_DARKEST);
                tg.setForegroundColor(GanttView.OD_FG);
                // Truncate name if it exceeds the column width
                String truncated = t.name.length() > layout.mid1 - 3 ? t.name.substring(0, layout.mid1 - 3) : t.name;
                tg.putString(2, y, truncated);
            }

            // --- 4. Render Gantt Bar (Middle Panel) ---
            // Calculate day-relative offsets for the bar boundaries
            int dayBarStart = (int) (Math.max(t.startDate.toEpochDay(), monthStart.toEpochDay())
                    - monthStart.toEpochDay());
            int dayBarEnd = (int) (Math.min(t.endDate.toEpochDay(), monthEnd.toEpochDay()) - monthStart.toEpochDay());
            int dayProgressEnd = (int) (Math.min(t.startDate.plusDays(t.progressDays).toEpochDay(),
                    monthEnd.plusDays(1).toEpochDay()) - monthStart.toEpochDay());

            // Convert days to terminal column positions
            int pixBarStart = dayBarStart * colsPerDay;
            int pixBarEnd = (dayBarEnd + 1) * colsPerDay - 1;
            int pixProgressEnd = dayProgressEnd * colsPerDay;

            tg.setBackgroundColor(GanttView.OD_BG_DARKEST);
            for (int col = 0; col < rulerWidth; col++) {
                if (col >= pixBarStart && col <= pixBarEnd) {
                    if (col < pixProgressEnd) {
                        // Completed Portion: High-density block
                        tg.setForegroundColor(isSelected ? GanttView.OD_CYAN : GanttView.OD_GREEN);
                        tg.putString(rulerOffset + col, y, "▓");
                    } else {
                        // Remaining Portion: Medium-density shade
                        tg.setForegroundColor(isSelected ? GanttView.OD_BLUE : GanttView.OD_COMMENT);
                        tg.putString(rulerOffset + col, y, "▒");
                    }
                } else {
                    tg.putString(rulerOffset + col, y, " "); // Background empty space
                }
            }

            // --- 5. Render Dates (Right Panel) ---
            int rightPanelWidth = layout.width - 1 - (layout.mid2 + 1);
            tg.setBackgroundColor(isSelected ? GanttView.OD_BG : GanttView.OD_BG_DARKEST);
            tg.setForegroundColor(isSelected ? GanttView.OD_FG : GanttView.OD_COMMENT);

            if (isSelected) // Fill selection background for the full row
                tg.putString(layout.mid2 + 1, y, String.format("%-" + rightPanelWidth + "s", ""));

            String dateTag = t.startDate.format(DateTimeFormatter.ofPattern("M/d")) + "-"
                    + t.endDate.format(DateTimeFormatter.ofPattern("M/d"));

            if (layout.mid2 + 1 + dateTag.length() < layout.width - 1) {
                tg.putString(layout.mid2 + 1, y, " " + dateTag);
            }
        }

        // --- 6. Empty State ---
        if (visible.isEmpty()) {
            tg.setBackgroundColor(GanttView.OD_BG_DARKEST);
            tg.setForegroundColor(GanttView.OD_COMMENT);
            tg.putString(2, startY, "No tasks in " + GanttTUI.MONTH_NAMES[app.currentMonth - 1] + ".");
        }
    }
}
