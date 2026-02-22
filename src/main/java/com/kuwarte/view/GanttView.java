package com.kuwarte.view;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;
import com.kuwarte.GanttTUI;

/**
 * GanttView: The master View class that orchestrates the rendering pipeline.
 * * This class follows the "Composite" pattern. Instead of drawing everything
 * itself,
 * it delegates specific regions of the screen to specialized Renderers. It also
 * defines the "One Dark" color palette used for consistent UI branding.
 */
public class GanttView {
    private final GanttTUI app;

    // --- One Dark Theme Palette ---
    // These constants define the visual identity of the app.
    // Contributors should use these rather than defining raw RGB values in
    // sub-renderers.
    public static final TextColor OD_BG = new TextColor.RGB(40, 44, 52);
    public static final TextColor OD_BG_DARK = new TextColor.RGB(33, 37, 43);
    public static final TextColor OD_BG_DARKER = new TextColor.RGB(23, 27, 33);
    public static final TextColor OD_BG_DARKEST = new TextColor.RGB(13, 17, 13);
    public static final TextColor OD_FG = new TextColor.RGB(171, 178, 191);
    public static final TextColor OD_COMMENT = new TextColor.RGB(92, 99, 112);
    public static final TextColor OD_GREEN = new TextColor.RGB(152, 195, 121);
    public static final TextColor OD_YELLOW = new TextColor.RGB(229, 192, 123);
    public static final TextColor OD_BLUE = new TextColor.RGB(97, 175, 239);
    public static final TextColor OD_PURPLE = new TextColor.RGB(198, 120, 221);
    public static final TextColor OD_CYAN = new TextColor.RGB(86, 182, 194);
    public static final TextColor OD_WHITE = new TextColor.RGB(220, 223, 228);

    // --- Sub-Renderers ---
    // Modular components that handle specific parts of the screen.
    private final BorderRenderer borderRenderer = new BorderRenderer();
    private final RulerRenderer rulerRenderer = new RulerRenderer();
    private final TaskListRenderer taskListRenderer = new TaskListRenderer();
    private final StatusBarRenderer statusBarRenderer = new StatusBarRenderer();
    private final MessageBarRenderer messageBarRenderer = new MessageBarRenderer();

    public GanttView(GanttTUI app) {
        this.app = app;
    }

    /**
     * The main draw call. This is executed once per loop in GanttTUI.
     * * Lifecycle of a Frame:
     * 1. Detect terminal size and calculate current Layout.
     * 2. Clear the screen with the darkest background.
     * 3. Draw components in a specific Z-order (Back to Front).
     * * @param screen The Lanterna screen to draw upon.
     */
    public void drawUI(Screen screen) {
        TerminalSize size = screen.getTerminalSize();
        TextGraphics tg = screen.newTextGraphics();

        // Compute dynamic layout based on current terminal window size
        Layout layout = new Layout(size);

        // 1. Initial Clear (Prevents "ghosting" from previous frames)
        tg.setBackgroundColor(OD_BG_DARKEST);
        tg.fillRectangle(new com.googlecode.lanterna.TerminalPosition(0, 0), size, ' ');

        // 2. Component Pipeline
        // The order of these calls matters for layering!
        borderRenderer.render(tg, layout, app); // Draw lines and headers
        rulerRenderer.render(tg, layout, app); // Draw date numbers
        taskListRenderer.render(tg, layout, app); // Draw task names and bars
        statusBarRenderer.render(tg, layout, app); // Draw metadata bar
        messageBarRenderer.render(tg, layout, app); // Draw command/status line
    }
}
