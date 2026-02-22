package com.kuwarte.view;

import com.googlecode.lanterna.graphics.TextGraphics;
import com.kuwarte.GanttTUI;

/**
 * Renderer: The functional contract for all UI components.
 * * This interface follows the Strategy Pattern. By decoupling the "what" to
 * draw
 * from the "how" to draw it, we allow the application to scale. New UI elements
 * (like a calendar popup or a settings menu) can be added simply by
 * implementing this interface and registering them in the GanttView pipeline.
 */
public interface Renderer {

    /**
     * Executes the drawing logic for a specific component.
     * * @param tg The Lanterna {@link TextGraphics} object used to "paint" the
     * terminal.
     * 
     * @param layout The calculated {@link Layout} containing spatial boundaries and
     *               midpoints.
     * @param app    The {@link GanttTUI} instance providing access to the current
     *               application state.
     */
    void render(TextGraphics tg, Layout layout, GanttTUI app);
}
