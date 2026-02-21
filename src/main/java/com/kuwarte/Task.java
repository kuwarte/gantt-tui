package com.kuwarte;

import java.time.LocalDate;
import java.time.YearMonth;

public class Task {
    String name;
    LocalDate startDate;
    LocalDate endDate;
    int progressDays;

    Task(String name, LocalDate startDate, LocalDate endDate, int progressDays) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate.isBefore(startDate) ? startDate : endDate;
        int totalDays = (int) (this.endDate.toEpochDay() - this.startDate.toEpochDay()) + 1;
        this.progressDays = Math.max(0, Math.min(progressDays, totalDays));
    }

    int totalDays() {
        return (int) (endDate.toEpochDay() - startDate.toEpochDay()) + 1;
    }

    boolean overlaps(YearMonth ym) {
        LocalDate monthStart = ym.atDay(1);
        LocalDate monthEnd = ym.atEndOfMonth();
        return !startDate.isAfter(monthEnd) && !endDate.isBefore(monthStart);
    }
}
