package com.kuwarte.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * PersistenceManager: The central authority for disk I/O operations.
 * * This class handles the serialization and deserialization of both project
 * data (Tasks)
 * and application state (AppConfig). By centralizing these operations, we
 * ensure
 * consistent file paths and GSON configurations across the application.
 */
public class PersistenceManager {

    /** Base directory for application data (~/.gantttui) */
    private static final String APP_DIR = System.getProperty("user.home") + "/.gantttui";

    /** Path to the project tasks JSON file */
    private static final String TASKS_FILE = APP_DIR + "/tasks.json";

    /** Path to the user preferences JSON file */
    private static final String CONFIG_FILE = APP_DIR + "/config.json";

    /**
     * Pre-configured GSON instance with:
     * - Pretty Printing for human-readability.
     * - Custom adapter for Java 8 LocalDate support.
     */
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(java.time.LocalDate.class, new LocalDateAdapter())
            .create();

    // --- Configuration Persistence ---

    /**
     * Saves the current application settings to config.json.
     * 
     * @param config The AppConfig object containing theme and border settings.
     */
    public static void saveConfig(AppConfig config) {
        try {
            ensureDirectoryExists();
            try (Writer writer = new FileWriter(CONFIG_FILE)) {
                gson.toJson(config, writer);
            }
        } catch (IOException e) {
            System.err.println("Could not save configuration: " + e.getMessage());
        }
    }

    /**
     * Loads application settings from disk.
     * 
     * @return The stored AppConfig or a new instance with defaults if none exists.
     */
    public static AppConfig loadConfig() {
        File file = new File(CONFIG_FILE);
        if (!file.exists())
            return new AppConfig();

        try (Reader reader = new FileReader(file)) {
            AppConfig config = gson.fromJson(reader, AppConfig.class);
            return config != null ? config : new AppConfig();
        } catch (IOException e) {
            return new AppConfig();
        }
    }

    // --- Task Persistence ---

    /**
     * Saves the project task list to tasks.json.
     * 
     * @param tasks The list of Task objects to persist.
     */
    public static void saveTasks(List<Task> tasks) {
        try {
            ensureDirectoryExists();
            try (Writer writer = new FileWriter(TASKS_FILE)) {
                gson.toJson(tasks, writer);
            }
        } catch (IOException e) {
            System.err.println("Could not save tasks: " + e.getMessage());
        }
    }

    /**
     * Loads project tasks from disk.
     * 
     * @return A list of tasks, or an empty list if no file is found or an error
     *         occurs.
     */
    public static List<Task> loadTasks() {
        File file = new File(TASKS_FILE);
        if (!file.exists())
            return new ArrayList<>();

        try (Reader reader = new FileReader(file)) {
            Type listType = new TypeToken<ArrayList<Task>>() {
            }.getType();
            List<Task> loaded = gson.fromJson(reader, listType);
            return loaded != null ? loaded : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    /**
     * Utility to ensure the hidden application directory exists.
     */
    private static void ensureDirectoryExists() throws IOException {
        Files.createDirectories(Paths.get(APP_DIR));
    }
}
