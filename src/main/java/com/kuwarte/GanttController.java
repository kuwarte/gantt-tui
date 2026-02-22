package com.kuwarte;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GanttController {
    private final GanttTUI app;

    public GanttController(GanttTUI app) {
        this.app = app;
    }

    private void save() {
        TaskStorage.saveTasks(app.tasks);
        if (!app.message.contains("@ Synced")) {
            app.message += " @ Synced";
        }
    }

    public void handleInput(Screen screen) throws IOException {
        KeyStroke key = screen.readInput();
        List<Task> visible = app.visibleTasks();

        if (key.getKeyType() == KeyType.ArrowUp) {
            moveSelection(-1, visible);
        } else if (key.getKeyType() == KeyType.ArrowDown) {
            moveSelection(1, visible);
        } else if (key.getKeyType() == KeyType.ArrowLeft) {
            moveTaskStart(-1, visible);
            save();
        } else if (key.getKeyType() == KeyType.ArrowRight) {
            moveTaskStart(1, visible);
            save();
        } else if (key.getKeyType() == KeyType.Character) {
            char c = key.getCharacter();
            switch (c) {
                case 'q':
                case 'Q':
                    save();
                    app.running = false;
                    break;
                case 'j':
                    moveSelection(1, visible);
                    break;
                case 'k':
                    moveSelection(-1, visible);
                    break;
                case 'h':
                    moveTaskStart(-1, visible);
                    save();
                    break;
                case 'l':
                    moveTaskStart(1, visible);
                    save();
                    break;
                case '+':
                case '=':
                    adjustDuration(1, visible);
                    save();
                    break;
                case '-':
                    adjustDuration(-1, visible);
                    save();
                    break;
                case '>':
                case '.':
                    adjustProgress(1, visible);
                    save();
                    break;
                case '<':
                case ',':
                    adjustProgress(-1, visible);
                    save();
                    break;
                case 'i':
                    insertTask(screen, app.selectedIndex);
                    save();
                    break;
                case 'o':
                    insertTask(screen, app.selectedIndex + 1);
                    save();
                    break;
                case 'd':
                    deleteTask(visible);
                    save();
                    break;
                case 'r':
                    renameTask(screen, visible);
                    save();
                    break;
                case '[':
                    app.prevMonth();
                    break;
                case ']':
                    app.nextMonth();
                    break;
                case '?':
                    app.message = "Keys: [j|k]=Sel [h|l]=MvDate [+/-]=Dur [<|>]=Progress [i|o]=Ins [r]=Ren [d]=Del [[|]]=Month";
                    break;
            }
        }
    }

    private void moveSelection(int delta, List<Task> visible) {
        if (visible.isEmpty())
            return;
        app.selectedIndex = Math.max(0, Math.min(visible.size() - 1, app.selectedIndex + delta));
        app.message = "";
    }

    private void moveTaskStart(int delta, List<Task> visible) {
        if (visible.isEmpty())
            return;
        Task t = visible.get(app.selectedIndex);
        t.startDate = t.startDate.plusDays(delta);
        t.endDate = t.endDate.plusDays(delta);
        app.message = "Task: " + t.startDate.format(DateTimeFormatter.ofPattern("MMM d"))
                + " – " + t.endDate.format(DateTimeFormatter.ofPattern("MMM d"));
    }

    private void adjustDuration(int delta, List<Task> visible) {
        if (visible.isEmpty())
            return;
        Task t = visible.get(app.selectedIndex);
        LocalDate newEnd = t.endDate.plusDays(delta);
        if (newEnd.isBefore(t.startDate))
            return;
        t.endDate = newEnd;
        t.progressDays = Math.min(t.progressDays, t.totalDays());
        app.message = "Duration: " + t.totalDays() + " day(s), ends "
                + t.endDate.format(DateTimeFormatter.ofPattern("MMM d"));
    }

    private void adjustProgress(int delta, List<Task> visible) {
        if (visible.isEmpty())
            return;
        Task t = visible.get(app.selectedIndex);
        t.progressDays = Math.max(0, Math.min(t.totalDays(), t.progressDays + delta));
        app.message = "Progress: " + t.progressDays + "/" + t.totalDays() + " day(s)";
    }

    private void insertTask(Screen screen, int index) throws IOException {
        String name = promptInput(screen, ":insert name> ", " INSERT ");
        if (name != null && !name.trim().isEmpty()) {
            LocalDate start = LocalDate.of(app.currentYear, app.currentMonth, 1);
            int insertPos = Math.min(app.tasks.size(), Math.max(0, index));
            app.tasks.add(insertPos, new Task(name.trim(), start, start.plusDays(2), 0));
            app.selectedIndex = insertPos;
            app.message = "Task inserted.";
        } else {
            app.message = "Insertion cancelled.";
        }
    }

    private void deleteTask(List<Task> visible) {
        if (visible.isEmpty())
            return;
        app.tasks.remove(visible.get(app.selectedIndex));
        app.clampSelection(app.visibleTasks());
        app.message = "Task deleted.";
    }

    private void renameTask(Screen screen, List<Task> visible) throws IOException {
        if (visible.isEmpty())
            return;
        String newName = promptInput(screen, ":rename> ", " RENAME ");
        if (newName != null && !newName.trim().isEmpty()) {
            visible.get(app.selectedIndex).name = newName.trim();
            app.message = "Task renamed to '" + newName.trim() + "'.";
        } else {
            app.message = "Rename cancelled.";
        }
    }

    private String promptInput(Screen screen, String prompt, String modeOverlay) throws IOException {
        TerminalSize size = screen.getTerminalSize();
        TextGraphics tg = screen.newTextGraphics();
        int width = size.getColumns();
        int height = size.getRows();
        StringBuilder input = new StringBuilder();

        while (true) {
            tg.enableModifiers(SGR.REVERSE);
            tg.setForegroundColor(TextColor.ANSI.YELLOW);
            tg.putString(0, height - 2, modeOverlay);
            tg.disableModifiers(SGR.REVERSE);

            tg.setForegroundColor(TextColor.ANSI.WHITE);
            tg.putString(0, height - 1, String.format("%-" + width + "s", ""));
            tg.putString(0, height - 1, prompt + input.toString() + "_");
            screen.refresh();

            KeyStroke key = screen.readInput();
            if (key.getKeyType() == KeyType.Enter) {
                return input.toString();
            } else if (key.getKeyType() == KeyType.Escape) {
                return null;
            } else if (key.getKeyType() == KeyType.Backspace && input.length() > 0) {
                input.deleteCharAt(input.length() - 1);
            } else if (key.getKeyType() == KeyType.Character) {
                input.append(key.getCharacter());
            }
        }
    }
}
