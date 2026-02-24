package com.kuwarte.view;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;
import com.kuwarte.GanttTUI;

/**
 * GanttView: The master View class that orchestrates the rendering pipeline.
 * This class delegates specific regions of the screen to specialized Renderers.
 * It also manages the dynamic color palette used for consistent UI branding.
 */
public class GanttView {
    private final GanttTUI app;

    // --- Dynamic Theme Palette ---
    // Removed 'final' and the 'OD_' prefix so these can be updated globally.
    // Sub-renderers should now reference GanttView.BG, GanttView.FG, etc.
    public static TextColor BG;
    public static TextColor BG_DARK;
    public static TextColor BG_DARKER;
    public static TextColor BG_DARKEST;
    public static TextColor FG;
    public static TextColor COMMENT;
    public static TextColor GREEN;
    public static TextColor YELLOW;
    public static TextColor BLUE;
    public static TextColor PURPLE;
    public static TextColor CYAN;
    public static TextColor WHITE;

    // --- Sub-Renderers ---
    private final BorderRenderer borderRenderer = new BorderRenderer();
    private final RulerRenderer rulerRenderer = new RulerRenderer();
    private final TaskListRenderer taskListRenderer = new TaskListRenderer();
    private final StatusBarRenderer statusBarRenderer = new StatusBarRenderer();
    private final MessageBarRenderer messageBarRenderer = new MessageBarRenderer();

    public GanttView(GanttTUI app) {
        this.app = app;
        // Load the default theme on startup
        setTheme("OneDark");
    }

    /**
     * The main draw call. Executed once per loop in GanttTUI.
     */
    public void drawUI(Screen screen) {
        TerminalSize size = screen.getTerminalSize();
        TextGraphics tg = screen.newTextGraphics();

        // Compute dynamic layout based on current terminal window size
        Layout layout = new Layout(size);

        // 1. Initial Clear (Prevents "ghosting" from previous frames)
        tg.setBackgroundColor(BG_DARKEST); // Updated to use dynamic color
        tg.fillRectangle(new com.googlecode.lanterna.TerminalPosition(0, 0), size, ' ');

        // 2. Component Pipeline
        borderRenderer.render(tg, layout, app);
        rulerRenderer.render(tg, layout, app);
        taskListRenderer.render(tg, layout, app);
        statusBarRenderer.render(tg, layout, app);
        messageBarRenderer.render(tg, layout, app);
    }

    /**
     * Globally switches the active color palette.
     * * @param theme The name of the theme to apply (e.g., "Dracula", "Solarized")
     */
    public void setTheme(String theme) {
        switch (theme.toLowerCase()) {
            case "dracula":
                BG = new TextColor.RGB(40, 42, 54);
                BG_DARK = new TextColor.RGB(33, 34, 44);
                BG_DARKER = new TextColor.RGB(25, 26, 33);
                BG_DARKEST = new TextColor.RGB(15, 15, 20);
                FG = new TextColor.RGB(248, 248, 242);
                COMMENT = new TextColor.RGB(98, 114, 164);
                GREEN = new TextColor.RGB(80, 250, 123);
                YELLOW = new TextColor.RGB(241, 250, 140);
                BLUE = new TextColor.RGB(139, 233, 253);
                PURPLE = new TextColor.RGB(189, 147, 249);
                CYAN = new TextColor.RGB(139, 233, 253);
                WHITE = new TextColor.RGB(255, 255, 255);
                break;

            case "solarized":
                BG = new TextColor.RGB(0, 43, 54);
                BG_DARK = new TextColor.RGB(7, 54, 66);
                BG_DARKER = new TextColor.RGB(0, 33, 43);
                BG_DARKEST = new TextColor.RGB(0, 21, 28);
                FG = new TextColor.RGB(131, 148, 150);
                COMMENT = new TextColor.RGB(88, 110, 117);
                GREEN = new TextColor.RGB(133, 153, 0);
                YELLOW = new TextColor.RGB(181, 137, 0);
                BLUE = new TextColor.RGB(38, 139, 210);
                PURPLE = new TextColor.RGB(211, 54, 130);
                CYAN = new TextColor.RGB(42, 161, 152);
                WHITE = new TextColor.RGB(238, 232, 213);
                break;

            case "onedark":
            default:
                BG = new TextColor.RGB(40, 44, 52);
                BG_DARK = new TextColor.RGB(33, 37, 43);
                BG_DARKER = new TextColor.RGB(23, 27, 33);
                BG_DARKEST = new TextColor.RGB(13, 17, 13);
                FG = new TextColor.RGB(171, 178, 191);
                COMMENT = new TextColor.RGB(92, 99, 112);
                GREEN = new TextColor.RGB(152, 195, 121);
                YELLOW = new TextColor.RGB(229, 192, 123);
                BLUE = new TextColor.RGB(97, 175, 239);
                PURPLE = new TextColor.RGB(198, 120, 221);
                CYAN = new TextColor.RGB(86, 182, 194);
                WHITE = new TextColor.RGB(220, 223, 228);
                break;
        }
    }
}
