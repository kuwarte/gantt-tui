package com.kuwarte.controller;

import com.kuwarte.GanttTUI;
import com.kuwarte.model.Task;
import com.kuwarte.model.PersistenceManager;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * TaskService: The "Brain" of the application logic.
 * * This class performs all state mutations on Task objects. By centralizing
 * these operations here, we ensure that business rules (like progress not
 * exceeding duration) are applied consistently across the app.
 */
public class TaskService {
    private final GanttTUI app;

    /** Formatter for user-friendly date feedback in the status message. */
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MMM d");

    public TaskService(GanttTUI app) {
        this.app = app;
    }

    /**
     * Triggers the persistence layer to write the current task list to disk.
     * Appends a "@ Synced" indicator to the UI message to confirm success.
     */
    public void save() {
        PersistenceManager.saveTasks(app.tasks);
        if (!app.message.contains("@ Synced")) {
            app.message += " @ Synced";
        }
    }

    /**
     * Changes the highlighted task.
     * 
     * @param delta   Direction of movement (usually -1 for up, 1 for down).
     * @param visible The current filtered list of tasks.
     */
    public void moveSelection(int delta, List<Task> visible) {
        if (visible.isEmpty())
            return;
        app.selectedIndex = Math.max(0, Math.min(visible.size() - 1, app.selectedIndex + delta));
        app.message = ""; // Clear old messages when navigating
    }

    /**
     * Shifts the entire task (both start and end) in time.
     * Effectively slides the task bar left or right on the Gantt chart.
     */
    public void moveTaskStart(int delta, List<Task> visible) {
        if (visible.isEmpty())
            return;
        Task t = visible.get(app.selectedIndex);
        t.startDate = t.startDate.plusDays(delta);
        t.endDate = t.endDate.plusDays(delta);
        app.message = "Task: " + t.startDate.format(DATE_FMT) + " – " + t.endDate.format(DATE_FMT);
        save();
    }

    /**
     * Extends or shrinks the task duration by moving the end date.
     * * Guardrail: Prevents the end date from being set before the start date.
     * Side Effect: Adjusts progressDays if the task is shrunk beyond its current
     * progress.
     */
    public void adjustDuration(int delta, List<Task> visible) {
        if (visible.isEmpty())
            return;
        Task t = visible.get(app.selectedIndex);
        LocalDate newEnd = t.endDate.plusDays(delta);

        // Logical Guard: A task must be at least 1 day long
        if (newEnd.isBefore(t.startDate))
            return;

        t.endDate = newEnd;

        // Consistency Guard: Ensure progress doesn't exceed new shorter duration
        t.progressDays = Math.min(t.progressDays, t.totalDays());

        app.message = "Duration: " + t.totalDays() + " day(s), ends " + t.endDate.format(DATE_FMT);
        save();
    }

    /**
     * Adjusts the number of completed days.
     * * Guardrail: Caps progress between 0 and the total duration of the task.
     */
    public void adjustProgress(int delta, List<Task> visible) {
        if (visible.isEmpty())
            return;
        Task t = visible.get(app.selectedIndex);
        t.progressDays = Math.max(0, Math.min(t.totalDays(), t.progressDays + delta));
        app.message = "Progress: " + t.progressDays + "/" + t.totalDays() + " day(s)";
        save();
    }

    /**
     * Permanently removes a task from the master list.
     * Triggers a selection 'clamp' to ensure the UI doesn't crash if the
     * deleted task was the last one in the list.
     */
    public void deleteTask(List<Task> visible) {
        if (visible.isEmpty())
            return;

        // Find and remove the actual object from the master list
        app.tasks.remove(visible.get(app.selectedIndex));

        // Adjust UI focus
        app.clampSelection(app.visibleTasks());
        app.message = "Task deleted.";
        save();
    }
}
