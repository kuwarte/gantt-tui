package com.kuwarte;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class TaskStorage {
    private static final String FILE_PATH = System.getProperty("user.home") + "/.gantttui/tasks.json";
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(java.time.LocalDate.class, new LocalDateAdapter())
            .create();

    public static void saveTasks(List<Task> tasks) {
        try {
            Files.createDirectories(Paths.get(System.getProperty("user.home") + "/.gantttui"));
            try (Writer writer = new FileWriter(FILE_PATH)) {
                gson.toJson(tasks, writer);
            }
        } catch (IOException e) {
            System.err.println("Could not save tasks: " + e.getMessage());
        }
    }

    public static List<Task> loadTasks() {
        File file = new File(FILE_PATH);
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
}
