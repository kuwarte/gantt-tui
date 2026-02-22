package com.kuwarte;

import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.kuwarte.controller.GanttController;
import com.kuwarte.model.Task;
import com.kuwarte.model.TaskStorage;
import com.kuwarte.view.GanttView;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 * GanttTUI: A Terminal User Interface for Gantt Chart Project Management.
 * * This class acts as the 'App Container' or 'Mediator' in an MVC-inspired
 * architecture.
 * It maintains the shared state (tasks, selection, view dates) and manages
 * the application lifecycle.
 * * @author kuwarte
 * 
 * @version 1.0
 */
public class GanttTUI {

    // --- Application State ---

    /** The master list of all project tasks. */
    public final List<Task> tasks = new ArrayList<>();

    /** Index of the currently highlighted task in the filtered 'visible' list. */
    public int selectedIndex = 0;

    /** Global message string displayed in the MessageBar (Footer). */
    public String message = "Welcome to GanttTUI. Press '?' for help.";

    /**
     * Main loop control flag. Setting this to false gracefully shuts down the app.
     */
    public boolean running = true;

    /**
     * Toggle for using Double-line (true) vs Single-line (false) Unicode box
     * characters.
     */
    public boolean thickBorder = false;

    /** The month currently being rendered in the Gantt grid (1-12). */
    public int currentMonth;

    /** The year currently being rendered in the Gantt grid. */
    public int currentYear;

    /** Human-readable month labels for headers and status bars. */
    public static final String[] MONTH_NAMES = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
    };

    /**
     * Entry point of the application.
     */
    public static void main(String[] args) {
        try {
            new GanttTUI().run();
        } catch (Exception e) {
            // Log fundamental startup failures
            e.printStackTrace();
        }
    }

    /**
     * Initializes the environment, loads data, and starts the main TUI loop.
     * * Lifecycle:
     * 1. Set default view to today's date.
     * 2. Load tasks from persistence (JSON).
     * 3. Initialize Lanterna Screen and modular View/Controller.
     * 4. Run loop: Draw -> Refresh -> Handle Input.
     * 5. Shutdown: Save tasks and stop terminal screen.
     */
    public void run() throws Exception {
        // Initialize temporal state
        LocalDate today = LocalDate.now();
        currentMonth = today.getMonthValue();
        currentYear = today.getYear();

        // Data Persistence Layer
        List<Task> loadedTasks = TaskStorage.loadTasks();
        if (loadedTasks.isEmpty()) {
            // Seed data for new users
            LocalDate base = today.withDayOfMonth(1);
            tasks.add(new Task("Design UI", base, base.plusDays(5), 3));
            tasks.add(new Task("Develop Backend", base.plusDays(3), base.plusDays(10), 0));
        } else {
            tasks.addAll(loadedTasks);
        }

        // Terminal Abstraction Layer
        Screen screen = new DefaultTerminalFactory().createScreen();
        screen.startScreen();
        screen.setCursorPosition(null); // Hide terminal cursor for better TUI immersion

        // Modular MVC Components
        GanttView renderer = new GanttView(this);
        GanttController controller = new GanttController(this);

        try {
            while (running) {
                // VIEW: Delegate drawing to the renderer module
                renderer.drawUI(screen);
                screen.refresh();

                // CONTROLLER: Block for input and delegate logic
                controller.handleInput(screen);
            }
        } finally {
            // Ensure data is saved even if an error occurs
            TaskStorage.saveTasks(tasks);
            screen.stopScreen();
        }
    }

    // --- State Management Helpers ---

    /**
     * Filters the master task list to only those that occur within the current view
     * month.
     * 
     * @return A list of tasks overlapping the active YearMonth.
     */
    public List<Task> visibleTasks() {
        YearMonth ym = YearMonth.of(currentYear, currentMonth);
        List<Task> visible = new ArrayList<>();
        for (Task t : tasks) {
            if (t.overlaps(ym))
                visible.add(t);
        }
        return visible;
    }

    /**
     * Prevents the selectedIndex from pointing to a non-existent task
     * after list filtering or task deletion.
     * 
     * @param visible The current filtered task list.
     */
    public void clampSelection(List<Task> visible) {
        if (visible.isEmpty())
            selectedIndex = 0;
        else
            selectedIndex = Math.max(0, Math.min(visible.size() - 1, selectedIndex));
    }

    /**
     * Shifts the calendar view backward by one month.
     */
    public void prevMonth() {
        if (--currentMonth < 1) {
            currentMonth = 12;
            currentYear--;
        }
        selectedIndex = 0; // Reset selection to top of list for the new month
        message = "Month: " + MONTH_NAMES[currentMonth - 1] + " " + currentYear;
    }

    /**
     * Shifts the calendar view forward by one month.
     */
    public void nextMonth() {
        if (++currentMonth > 12) {
            currentMonth = 1;
            currentYear++;
        }
        selectedIndex = 0; // Reset selection to top of list for the new month
        message = "Month: " + MONTH_NAMES[currentMonth - 1] + " " + currentYear;
    }
}
