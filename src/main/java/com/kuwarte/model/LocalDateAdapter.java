package com.kuwarte.model;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.LocalDate;

/**
 * LocalDateAdapter: A custom GSON TypeAdapter for {@link java.time.LocalDate}.
 * * Standard GSON (at the time of writing) does not natively support the Java 8
 * Date/Time API. This class ensures that LocalDates are saved as standard
 * ISO-8601 strings (e.g., "2026-02-22") and parsed back into objects correctly.
 * * @see TaskStorage
 */
public class LocalDateAdapter extends TypeAdapter<LocalDate> {

    /**
     * Serializes a LocalDate object into a JSON string.
     * * @param out The JSON writer stream.
     * 
     * @param value The LocalDate to save (can be null).
     * @throws IOException If the stream cannot be written to.
     */
    @Override
    public void write(JsonWriter out, LocalDate value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            // Saves in ISO_LOCAL_DATE format (YYYY-MM-DD)
            out.value(value.toString());
        }
    }

    /**
     * Deserializes a JSON string back into a LocalDate object.
     * * @param in The JSON reader stream.
     * 
     * @return The parsed LocalDate, or null if the JSON value was null.
     * @throws IOException                             If the stream cannot be read.
     * @throws java.time.format.DateTimeParseException if the string is not a valid
     *                                                 date.
     */
    @Override
    public LocalDate read(JsonReader in) throws IOException {
        if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        // Parses the YYYY-MM-DD string back into a LocalDate instance
        return LocalDate.parse(in.nextString());
    }
}
