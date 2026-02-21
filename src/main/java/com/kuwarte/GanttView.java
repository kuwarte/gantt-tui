package com.kuwarte;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GanttView {
    private final GanttTUI app;

    public GanttView(GanttTUI app) {
        this.app = app;
    }

    public void drawUI(Screen screen) {
        TerminalSize size = screen.getTerminalSize();
        TextGraphics tg = screen.newTextGraphics();

        int width = size.getColumns();
        int height = size.getRows();

        int mid1 = width / 5;
        int mid2 = width - width / 5;

        YearMonth ym = YearMonth.of(app.currentYear, app.currentMonth);
        int daysInMonth = ym.lengthOfMonth();
        LocalDate monthStart = ym.atDay(1);
        LocalDate monthEnd = ym.atEndOfMonth();

        List<Task> visible = app.visibleTasks();
        app.clampSelection(visible);

        int startY = 3;

        tg.setForegroundColor(TextColor.ANSI.BLACK_BRIGHT);
        for (int x = 1; x < width - 1; x++)
            tg.putString(x, 0, "─");
        for (int x = 1; x < width - 1; x++)
            tg.putString(x, 2, "─");
        for (int x = 1; x < width - 1; x++)
            tg.putString(x, height - 3, "─");

        for (int y = 1; y < height - 3; y++) {
            tg.putString(0, y, "│");
            tg.putString(mid1, y, "│");
            tg.putString(mid2, y, "│");
            tg.putString(width - 1, y, "│");
        }

        tg.putString(0, 0, "┌");
        tg.putString(width - 1, 0, "┐");
        tg.putString(0, height - 3, "└");
        tg.putString(width - 1, height - 3, "┘");
        tg.putString(mid1, 0, "┬");
        tg.putString(mid2, 0, "┬");
        tg.putString(mid1, height - 3, "┴");
        tg.putString(mid2, height - 3, "┴");
        tg.putString(0, 2, "├");
        tg.putString(width - 1, 2, "┤");
        tg.putString(mid1, 2, "┼");
        tg.putString(mid2, 2, "┼");

        tg.setForegroundColor(TextColor.ANSI.CYAN);
        tg.putString(2, 1, "TASK");

        int rulerOffset = mid1 + 1;
        int rulerWidth = mid2 - mid1 - 2;
        int colsPerDay = Math.max(1, rulerWidth / daysInMonth);

        tg.setForegroundColor(TextColor.ANSI.BLACK_BRIGHT);
        for (int d = 0; d < daysInMonth; d++) {
            int dayNum = d + 1;
            int slotCol = d * colsPerDay;
            for (int p = 0; p < colsPerDay && slotCol + p < rulerWidth; p++)
                tg.putString(rulerOffset + slotCol + p, 1, "─");

            if (dayNum == 1 || dayNum % 5 == 0 || dayNum == daysInMonth) {
                String label = String.valueOf(dayNum);
                int labelCol = slotCol + colsPerDay - label.length();
                if (labelCol < slotCol)
                    labelCol = slotCol;
                if (labelCol + label.length() <= rulerWidth)
                    tg.putString(rulerOffset + labelCol, 1, label);
            }
        }

        String monthLabel = " " + GanttTUI.MONTH_NAMES[app.currentMonth - 1].toUpperCase() + " " + app.currentYear;
        int rightPanelStart = mid2 + 1;
        tg.setForegroundColor(TextColor.ANSI.YELLOW);
        tg.putString(rightPanelStart, 1, monthLabel);

        for (int i = 0; i < visible.size(); i++) {
            Task t = visible.get(i);
            int y = startY + i;
            if (y >= height - 3)
                break;

            if (i == app.selectedIndex) {
                tg.enableModifiers(SGR.REVERSE);
                tg.setForegroundColor(TextColor.ANSI.DEFAULT);
                tg.putString(1, y, String.format(" %-" + (mid1 - 2) + "s", t.name));
                tg.disableModifiers(SGR.REVERSE);
            } else {
                tg.setForegroundColor(TextColor.ANSI.WHITE);
                String truncated = t.name.length() > mid1 - 3 ? t.name.substring(0, mid1 - 3) : t.name;
                tg.putString(2, y, truncated);
            }

            LocalDate barStart = t.startDate.isBefore(monthStart) ? monthStart : t.startDate;
            LocalDate barEnd = t.endDate.isAfter(monthEnd) ? monthEnd : t.endDate;

            int dayBarStart = (int) (barStart.toEpochDay() - monthStart.toEpochDay());
            int dayBarEnd = (int) (barEnd.toEpochDay() - monthStart.toEpochDay());

            LocalDate progressEndDate = t.startDate.plusDays(t.progressDays);
            if (progressEndDate.isAfter(monthEnd.plusDays(1)))
                progressEndDate = monthEnd.plusDays(1);
            int dayProgressEnd = (int) (progressEndDate.toEpochDay() - monthStart.toEpochDay());

            int pixBarStart = dayBarStart * colsPerDay;
            int pixBarEnd = (dayBarEnd + 1) * colsPerDay - 1;
            int pixProgressEnd = dayProgressEnd * colsPerDay;

            tg.setForegroundColor(TextColor.ANSI.BLACK_BRIGHT);
            for (int col = 0; col < pixBarStart && col < rulerWidth; col++)
                tg.putString(rulerOffset + col, y, " ");

            for (int col = pixBarStart; col <= pixBarEnd && col < rulerWidth; col++) {
                if (col < pixProgressEnd) {
                    tg.setForegroundColor(i == app.selectedIndex ? TextColor.ANSI.CYAN : TextColor.ANSI.GREEN);
                    tg.putString(rulerOffset + col, y, "▓");
                } else {
                    tg.setForegroundColor(i == app.selectedIndex ? TextColor.ANSI.CYAN : TextColor.ANSI.BLACK_BRIGHT);
                    tg.putString(rulerOffset + col, y, "▒");
                }
            }

            tg.setForegroundColor(TextColor.ANSI.BLACK_BRIGHT);
            for (int col = pixBarEnd + 1; col < rulerWidth; col++)
                tg.putString(rulerOffset + col, y, " ");

            String dateTag = t.startDate.format(DateTimeFormatter.ofPattern("M/d"))
                    + "-" + t.endDate.format(DateTimeFormatter.ofPattern("M/d"));
            if (rightPanelStart + dateTag.length() < width - 1) {
                tg.setForegroundColor(i == app.selectedIndex ? TextColor.ANSI.CYAN : TextColor.ANSI.BLACK_BRIGHT);
                tg.putString(rightPanelStart, y, " " + dateTag);
            }
        }

        if (visible.isEmpty()) {
            tg.setForegroundColor(TextColor.ANSI.BLACK_BRIGHT);
            tg.putString(2, startY, "No tasks in " + GanttTUI.MONTH_NAMES[app.currentMonth - 1] + ".");
            tg.putString(2, startY + 1, "Press [i] to add one.");
        }

        int statusY = height - 2;
        tg.enableModifiers(SGR.REVERSE);
        tg.setForegroundColor(TextColor.ANSI.GREEN);
        String modeStr = " NORMAL ";
        String fileStr = " [Gantt Workspace] ";
        String progressStr = String.format(" Task %d/%d  %s %d ",
                visible.isEmpty() ? 0 : app.selectedIndex + 1, visible.size(),
                GanttTUI.MONTH_NAMES[app.currentMonth - 1].substring(0, 3), app.currentYear);
        tg.putString(0, statusY, String.format("%-" + width + "s", ""));
        tg.putString(0, statusY, modeStr + fileStr);
        tg.putString(width - progressStr.length(), statusY, progressStr);
        tg.disableModifiers(SGR.REVERSE);

        tg.setForegroundColor(TextColor.ANSI.WHITE);
        tg.putString(0, height - 1, String.format("%-" + width + "s", ""));
        tg.putString(0, height - 1, app.message.isEmpty() ? ":" : ": " + app.message);
    }
}
