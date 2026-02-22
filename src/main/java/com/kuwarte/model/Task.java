package com.kuwarte.model;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;

/**
 * Task: The core Data Model representing a project entity.
 * * This class stores the task's identity, its temporal bounds, and its
 * progress.
 * It provides utility methods to calculate duration and determine if the task
 * should be rendered in the current calendar view.
 */
public class Task {

    /** The display name of the task. */
    public String name;

    /** The inclusive start date of the task. */
    public LocalDate startDate;

    /** The inclusive end date of the task. */
    public LocalDate endDate;

    /**
     * * The number of days completed.
     * Logic should ensure 0 <= progressDays <= totalDays().
     */
    public int progressDays;

    /**
     * Constructs a new Task.
     * * @param name Descriptive title of the task.
     * 
     * @param startDate    When the task begins.
     * @param endDate      When the task is scheduled to finish.
     * @param progressDays Current progress in days.
     */
    public Task(String name, LocalDate startDate, LocalDate endDate, int progressDays) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.progressDays = progressDays;
    }

    /**
     * Determines if any portion of this task falls within a specific month and
     * year.
     * * This is used by the Controller and View to filter which tasks to show
     * in the current TUI window.
     * * @param ym The YearMonth to check against.
     * 
     * @return true if the task overlaps with the given month.
     */
    public boolean overlaps(YearMonth ym) {
        LocalDate monthStart = ym.atDay(1);
        LocalDate monthEnd = ym.atEndOfMonth();

        // Logical check: A task overlaps if it doesn't start after the month ends
        // AND it doesn't end before the month begins.
        return !startDate.isAfter(monthEnd) && !endDate.isBefore(monthStart);
    }

    /**
     * Calculates the total duration of the task in days.
     * * Uses ChronoUnit.DAYS to find the difference. We add +1 because
     * Gantt dates are inclusive (a task starting and ending on the same
     * day is 1 day long).
     * * @return Total inclusive day count.
     */
    public int totalDays() {
        return (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }
}
