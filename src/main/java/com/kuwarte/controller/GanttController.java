package com.kuwarte.controller;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.kuwarte.GanttTUI;
import com.kuwarte.model.Task;
import com.kuwarte.model.PersistenceManager;

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

    private final String[] THEMES = { "OneDark", "Dracula", "Solarized" };
    private int currentThemeIndex = 0;

    private int helpPageIndex = 0;

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
    /**
     * Internal dispatcher for character-based commands.
     * Maps keyboard characters to specific application actions using a standard
     * switch block.
     * * @param screen The active Lanterna screen.
     * 
     * @param c       The character input to process.
     * @param visible The list of tasks currently visible in the view.
     * @throws IOException If terminal interaction fails.
     */
    private void handleCharInput(Screen screen, char c, List<Task> visible) throws IOException {
        switch (c) {
            case 'q':
            case 'Q':
                taskService.save();
                app.running = false;
                break;

            // Navigation (Vim-style)
            case 'j':
                taskService.moveSelection(1, visible);
                break;
            case 'k':
                taskService.moveSelection(-1, visible);
                break;
            case 'h':
                taskService.moveTaskStart(-1, visible);
                break;
            case 'l':
                taskService.moveTaskStart(1, visible);
                break;

            // Task Modification
            case '+':
            case '=':
                taskService.adjustDuration(1, visible);
                break;
            case '-':
                taskService.adjustDuration(-1, visible);
                break;
            case '>':
            case '.':
                taskService.adjustProgress(1, visible);
                break;
            case '<':
            case ',':
                taskService.adjustProgress(-1, visible);
                break;

            // View Controls
            case '[':
                app.prevMonth();
                break;
            case ']':
                app.nextMonth();
                break;
            case 'b':
                toggleBorder();
                break;
            case 't':
                toggleTheme();
                break;

            // CRUD Operations
            case 'i':
                handleInsertion(screen, app.selectedIndex);
                break;
            case 'o':
                handleInsertion(screen, app.selectedIndex + 1);
                break;
            case 'r':
                handleRename(screen, visible);
                break;
            case 'd':
                taskService.deleteTask(visible);
                break;
            case 'W':
                handleWorkspaceRename(screen);
                break;

            // Help
            case '?':
                showHelp();
                break;

            default:
                break;
        }
    }

    /**
     * Toggles between thick (double-line) and thin (single-line) Unicode borders.
     * Synchronizes the setting across the live application state and the persistent
     * configuration file.
     */
    private void toggleBorder() {
        // Toggle the live UI state
        app.thickBorder = !app.thickBorder;

        // Update the configuration model for persistence
        app.config.thickBorder = app.thickBorder;
        PersistenceManager.saveConfig(app.config);

        app.message = "Border: " + (app.thickBorder ? "thick" : "thin");
    }

    /**
     * Cycles through the available color themes (e.g., OneDark, Dracula,
     * Solarized).
     * Updates the View's color palette and persists the choice to the configuration
     * file.
     */
    private void toggleTheme() {
        currentThemeIndex = (currentThemeIndex + 1) % THEMES.length;
        String nextTheme = THEMES[currentThemeIndex];

        // Apply new colors to the renderer
        app.view.setTheme(nextTheme);

        // Update and save the configuration
        app.config.theme = nextTheme;
        PersistenceManager.saveConfig(app.config);

        app.message = "Theme: " + nextTheme;
    }

    /**
     * Triggers an interactive prompt to change the workspace branding name.
     * The new name is displayed in the StatusBar and saved to config.json.
     * * @param screen The active Lanterna screen for rendering the input prompt.
     * 
     * @throws IOException If terminal communication fails during input.
     */
    private void handleWorkspaceRename(Screen screen) throws IOException {
        String newName = inputHandler.promptInput(screen, ":set workspace name> ", " BRANDING ");

        if (newName != null && !newName.trim().isEmpty()) {
            // Update the workspace name in config and persist
            app.config.workspaceName = newName.trim();
            PersistenceManager.saveConfig(app.config);

            app.message = "Workspace renamed to: " + app.config.workspaceName;
        }
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

    /**
     * Cycles through different help message variations to keep the status bar
     * clean.
     * Each press of '?' reveals a different category of commands.
     */
    private void showHelp() {
        switch (helpPageIndex) {
            case 0:
                app.message = "[NAV] j/k: Select | h/l: Shift Date | [/]: Month | Arrows: Move";
                break;
            case 1:
                app.message = "[EDIT] +/-: Duration | </>: Progress | i/o: Insert | r: Rename | d: Delete";
                break;
            case 2:
                app.message = "[UI] b: Borders | t: Themes | W: Workspace Name | q: Save & Quit";
                break;
            default:
                app.message = "Press '?' again for more commands...";
                break;
        }

        // Increment and wrap around using the modulo operator
        helpPageIndex = (helpPageIndex + 1) % 3;
    }
}
