package com.kuwarte.view;

import com.googlecode.lanterna.graphics.TextGraphics;
import com.kuwarte.GanttTUI;
import java.time.YearMonth;

/**
 * RulerRenderer: Draws the horizontal date timeline at the top of the chart.
 * * This class handles the complex math of mapping 28-31 days into a dynamic
 * terminal width. It provides a visual scale (ruler) with numbered day
 * indicators to help the user align task bars with specific dates.
 */
public class RulerRenderer implements Renderer {
    @Override
    public void render(TextGraphics tg, Layout layout, GanttTUI app) {
        // --- 1. Date and Dimension Math ---
        YearMonth ym = YearMonth.of(app.currentYear, app.currentMonth);
        int daysInMonth = ym.lengthOfMonth();

        // Offset starts after the task name column (mid1)
        int rulerOffset = layout.mid1 + 1;
        // Total available width for the date ruler
        int rulerWidth = layout.mid2 - layout.mid1 - 1;

        // Determine how many characters each day occupies.
        // For example, if width is 60 and days is 30, colsPerDay = 2.
        int colsPerDay = Math.max(1, rulerWidth / daysInMonth);

        // --- 2. Initial Background Pass ---
        tg.setBackgroundColor(GanttView.BG_DARKER);
        for (int x = rulerOffset; x < rulerOffset + rulerWidth; x++)
            tg.putString(x, 1, " ");

        // --- 3. Drawing the Ruler Scale ---
        for (int d = 0; d < daysInMonth; d++) {
            int dayNum = d + 1;
            int slotCol = d * colsPerDay;

            // Optimization: Only label the first day, every 5th day, and the last day
            // to keep the UI clean and prevent text overlapping.
            boolean isLabeled = (dayNum == 1 || dayNum % 5 == 0 || dayNum == daysInMonth);

            // Draw the base line of the ruler
            tg.setForegroundColor(GanttView.COMMENT);
            for (int p = 0; p < colsPerDay && slotCol + p < rulerWidth; p++)
                tg.putString(rulerOffset + slotCol + p, 1, "─");

            if (isLabeled) {
                String label = String.valueOf(dayNum);

                // Alignment logic for the last day to prevent it from clipping the border
                int labelCol = (dayNum == daysInMonth) ? rulerWidth - label.length()
                        : slotCol + colsPerDay - label.length();

                if (labelCol < slotCol)
                    labelCol = slotCol;

                // Render the date number (Yellow for high visibility)
                if (labelCol >= 0 && labelCol + label.length() <= rulerWidth) {
                    tg.setForegroundColor(GanttView.YELLOW);
                    tg.putString(rulerOffset + labelCol, 1, label);
                }
            }
        }

        // --- 4. Column Continuity ---
        // Draws the vertical dividers to maintain the "grid" look even in the ruler
        // area.
        String v = app.thickBorder ? "║" : "│";
        tg.setForegroundColor(GanttView.COMMENT);
        tg.putString(0, 1, v);
        tg.putString(layout.mid1, 1, v);
        tg.putString(layout.mid2, 1, v);
        tg.putString(layout.width - 1, 1, v);
    }
}
