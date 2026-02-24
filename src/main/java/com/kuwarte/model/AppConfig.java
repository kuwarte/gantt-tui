package com.kuwarte.model;

/**
 * AppConfig: A data transfer object (DTO) representing persistent user
 * preferences.
 * * This class is serialized to and from JSON via
 * {@link com.kuwarte.model.TaskStorage}.
 * It decouples the UI state from the project data, allowing themes and layout
 * settings to persist across different project files.
 * * @author kuwarte
 */
public class AppConfig {

    /**
     * The identifier for the active color palette (e.g., "OneDark", "Dracula",
     * "Solarized").
     * Defaults to "OneDark".
     */
    public String theme = "OneDark";

    /**
     * Controls the Unicode box-drawing style used for the UI frames.
     * If true, uses double-line (thick) characters; if false, uses single-line
     * (thin).
     * Defaults to false.
     */
    public boolean thickBorder = false;

    public String workspaceName = "Gantt Workspace";

    /**
     * Default constructor required for GSON deserialization.
     */
    public AppConfig() {
    }
}
