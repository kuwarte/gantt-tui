package com.kuwarte.controller;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.kuwarte.GanttTUI;
import com.kuwarte.model.Task;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * GanttController: The primary input processor for the application.
 * * This class implements a Vim-style interaction model. It bridges the gap
 * between the View (Lanterna Screen) and the Model (Task data).
 * * Responsibilities:
 * 1. Capture keyboard events.
 * 2. Delegate data mutations to {@link TaskService}.
 * 3. Manage UI-specific prompts (Renaming, Inserting) via {@link InputHandler}.
 */
public class GanttController {
    private final GanttTUI app;
    private final TaskService taskService;
    private final InputHandler inputHandler;

    public GanttController(GanttTUI app) {
        this.app = app;
        this.taskService = new TaskService(app);
        this.inputHandler = new InputHandler();
    }

    /**
     * Primary entry point for input handling.
     * This method blocks until a key is pressed, then dispatches to the correct
     * logic.
     * * @param screen The active Lanterna screen for reading input.
     * 
     * @throws IOException If terminal communication fails.
     */
    public void handleInput(Screen screen) throws IOException {
        KeyStroke key = screen.readInput();
        List<Task> visible = app.visibleTasks();

        // Handle Special Keys (Arrows)
        if (key.getKeyType() == KeyType.ArrowUp)
            taskService.moveSelection(-1, visible);
        else if (key.getKeyType() == KeyType.ArrowDown)
            taskService.moveSelection(1, visible);
        else if (key.getKeyType() == KeyType.ArrowLeft)
            taskService.moveTaskStart(-1, visible);
        else if (key.getKeyType() == KeyType.ArrowRight)
            taskService.moveTaskStart(1, visible);

        // Handle Character keys (j, k, h, l, etc.)
        else if (key.getKeyType() == KeyType.Character) {
            handleCharInput(screen, key.getCharacter(), visible);
        }
    }

    /**
     * Internal dispatcher for character-based commands.
     * Logic follows a Vim-like mapping:
     * - j/k: Vertical navigation
     * - h/l: Horizontal task shifting
     * - i/o: Insertion (above/below)
     */
    private void handleCharInput(Screen screen, char c, List<Task> visible) throws IOException {
        switch (c) {
            case 'q', 'Q' -> {
                taskService.save();
                app.running = false;
            }
            // Navigation (Vim-style)
            case 'j' -> taskService.moveSelection(1, visible);
            case 'k' -> taskService.moveSelection(-1, visible);
            case 'h' -> taskService.moveTaskStart(-1, visible);
            case 'l' -> taskService.moveTaskStart(1, visible);

            // Task Modification
            case '+', '=' -> taskService.adjustDuration(1, visible);
            case '-' -> taskService.adjustDuration(-1, visible);
            case '>', '.' -> taskService.adjustProgress(1, visible);
            case '<', ',' -> taskService.adjustProgress(-1, visible);

            // View Controls
            case '[' -> app.prevMonth();
            case ']' -> app.nextMonth();
            case 'b' -> toggleBorder();

            // CRUD Operations
            case 'i', 'o' -> handleInsertion(screen, c == 'i' ? app.selectedIndex : app.selectedIndex + 1);
            case 'r' -> handleRename(screen, visible);
            case 'd' -> taskService.deleteTask(visible);

            // Help
            case '?' ->
                app.message = "Keys: [j|k]=Sel [h|l]=MvDate [+/-]=Dur [<|>]=Progress [i|o]=Ins [r]=Ren [d]=Del [[|]]=Month";
        }
    }

    private void toggleBorder() {
        app.thickBorder = !app.thickBorder;
        app.message = "Border: " + (app.thickBorder ? "thick" : "thin");
    }

    /**
     * Triggers an interactive input prompt to create a new task.
     * New tasks default to the 1st of the current viewed month.
     */
    private void handleInsertion(Screen screen, int index) throws IOException {
        String name = inputHandler.promptInput(screen, ":insert name> ", " INSERT ");
        if (name != null && !name.trim().isEmpty()) {
            LocalDate start = LocalDate.of(app.currentYear, app.currentMonth, 1);
            // Inserts at the selected position to maintain user's desired order
            app.tasks.add(Math.min(app.tasks.size(), index), new Task(name.trim(), start, start.plusDays(2), 0));
            app.selectedIndex = index;
            taskService.save();
            app.message = "Task inserted.";
        }
    }

    /**
     * Triggers an interactive input prompt to modify a task's name.
     */
    private void handleRename(Screen screen, List<Task> visible) throws IOException {
        if (visible.isEmpty())
            return;
        String newName = inputHandler.promptInput(screen, ":rename> ", " RENAME ");
        if (newName != null && !newName.trim().isEmpty()) {
            visible.get(app.selectedIndex).name = newName.trim();
            taskService.save();
        }
    }
}
