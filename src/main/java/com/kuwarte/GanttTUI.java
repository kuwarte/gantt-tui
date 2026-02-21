package com.kuwarte;

import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class GanttTUI {

    final List<Task> tasks = new ArrayList<>();
    int selectedIndex = 0;
    String message = "Welcome to GanttTUI. Press '?' for help.";
    boolean running = true;
    int currentMonth;
    int currentYear;

    static final String[] MONTH_NAMES = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
    };

    public static void main(String[] args) {
        try {
            new GanttTUI().run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() throws Exception {
        LocalDate today = LocalDate.now();
        currentMonth = today.getMonthValue();
        currentYear = today.getYear();

        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        Screen screen = terminalFactory.createScreen();
        screen.startScreen();
        screen.setCursorPosition(null);

        // Sample tasks
        LocalDate base = today.withDayOfMonth(1);
        tasks.add(new Task("Design UI", base, base.plusDays(5), 6));
        tasks.add(new Task("Develop Backend", base.plusDays(3), base.plusDays(10), 4));
        tasks.add(new Task("Testing API", base.plusDays(8), base.plusDays(11), 1));
        tasks.add(new Task("Documentation", base.plusDays(10), base.plusDays(12), 0));
        tasks.add(new Task("Deployment", base.plusDays(13), base.plusDays(14), 0));
        tasks.add(new Task("Next Phase", base.plusDays(20), base.plusMonths(1).plusDays(4), 0));

        GanttView renderer = new GanttView(this);
        GanttController inputHandler = new GanttController(this);

        while (running) {
            screen.clear();
            renderer.drawUI(screen);
            screen.refresh();
            inputHandler.handleInput(screen);
        }

        screen.stopScreen();
    }

    List<Task> visibleTasks() {
        YearMonth ym = YearMonth.of(currentYear, currentMonth);
        List<Task> visible = new ArrayList<>();
        for (Task t : tasks) {
            if (t.overlaps(ym))
                visible.add(t);
        }
        return visible;
    }

    void clampSelection(List<Task> visible) {
        if (visible.isEmpty())
            selectedIndex = 0;
        else
            selectedIndex = Math.max(0, Math.min(visible.size() - 1, selectedIndex));
    }

    void prevMonth() {
        currentMonth--;
        if (currentMonth < 1) {
            currentMonth = 12;
            currentYear--;
        }
        selectedIndex = 0;
        message = "Month: " + MONTH_NAMES[currentMonth - 1] + " " + currentYear;
    }

    void nextMonth() {
        currentMonth++;
        if (currentMonth > 12) {
            currentMonth = 1;
            currentYear++;
        }
        selectedIndex = 0;
        message = "Month: " + MONTH_NAMES[currentMonth - 1] + " " + currentYear;
    }
}
