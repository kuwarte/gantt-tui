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
 * TaskStorage: Handles persistent storage of Task data using JSON format.
 * * This class manages the lifecycle of data on the physical disk. It uses
 * Google GSON
 * for serialization and ensures that the application data directory exists
 * before
 * attempting to write.
 */
public class TaskStorage {

    /**
     * * The location of the database file.
     * Stored in the user's home directory under a hidden folder for Unix-standard
     * compliance.
     */
    private static final String FILE_PATH = System.getProperty("user.home") + "/.gantttui/tasks.json";

    /**
     * Configured GSON instance.
     * - Pretty Printing: Makes the JSON file human-readable for manual edits.
     * - LocalDateAdapter: Essential for handling Java 8 Date objects.
     */
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(java.time.LocalDate.class, new LocalDateAdapter())
            .create();

    /**
     * Serializes the current task list to a JSON file.
     * * Lifecycle:
     * 1. Check/Create the ~/.gantttui directory.
     * 2. Open a Writer to the tasks.json file.
     * 3. Convert the List of Task objects into a JSON string and write to disk.
     * * @param tasks The current list of project tasks to be saved.
     */
    public static void saveTasks(List<Task> tasks) {
        try {
            // Ensure the directory exists to avoid FileNotFoundException
            Files.createDirectories(Paths.get(System.getProperty("user.home") + "/.gantttui"));

            try (Writer writer = new FileWriter(FILE_PATH)) {
                gson.toJson(tasks, writer);
            }
        } catch (IOException e) {
            // Logs to stderr to avoid disrupting the Lanterna TUI screen if possible
            System.err.println("Could not save tasks: " + e.getMessage());
        }
    }

    /**
     * Loads tasks from the JSON file on disk.
     * * If the file does not exist, it returns an empty list, allowing the
     * main application to provide default "seed" data.
     * * @return A List of Task objects recovered from disk, or an empty ArrayList
     * if no data is found.
     */
    public static List<Task> loadTasks() {
        File file = new File(FILE_PATH);
        if (!file.exists())
            return new ArrayList<>();

        try (Reader reader = new FileReader(file)) {
            // Type erasure in Java requires TypeToken to capture the Generic List<Task>
            // type
            Type listType = new TypeToken<ArrayList<Task>>() {
            }.getType();

            List<Task> loaded = gson.fromJson(reader, listType);
            return loaded != null ? loaded : new ArrayList<>();
        } catch (IOException e) {
            // Return empty list on failure to let the app start with a clean slate
            return new ArrayList<>();
        }
    }
}
