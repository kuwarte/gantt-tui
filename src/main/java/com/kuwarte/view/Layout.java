package com.kuwarte.view;

import com.googlecode.lanterna.TerminalSize;

/**
 * Layout: Calculates and stores the spatial dimensions of the TUI.
 * * This class acts as a "Value Object" that translates the raw terminal size
 * into specific functional zones. By using ratios instead of fixed integers,
 * the UI remains responsive to terminal resizing.
 */
public class Layout {
    /** Total columns available in the terminal. */
    public final int width;

    /** Total rows available in the terminal. */
    public final int height;

    /**
     * * The first vertical divider.
     * Currently set to 20% of the width. This column separates
     * the Task Names from the Task Metadata (Dates/Progress).
     */
    public final int mid1;

    /**
     * * The second vertical divider.
     * Currently set to 80% of the width. This column separates
     * the Task Metadata from the actual Gantt Grid.
     */
    public final int mid2;

    /**
     * Constructs a new Layout snapshot based on the current terminal size.
     * * @param size The current dimensions provided by the Lanterna Screen.
     */
    public Layout(TerminalSize size) {
        this.width = size.getColumns();
        this.height = size.getRows();

        // --- Proportional Calculation ---
        // mid1: Allocates 1/5th of the screen for the task list.
        this.mid1 = width / 5;

        // mid2: Allocates the middle 3/5ths for metadata,
        // leaving the final 1/5th for the Gantt chart visualization.
        this.mid2 = width - (width / 5);
    }
}
