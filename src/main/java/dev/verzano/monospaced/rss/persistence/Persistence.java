package dev.verzano.monospaced.rss.persistence;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;

// TODO log errors, duh
public class Persistence {
    public static final String PERSISTENCE_DIR_KEY = "com.verzano.terminalrss.persistencedir";

    private static final Gson GSON = new Gson();
    private static final String DATA_DIR = System.getProperty(PERSISTENCE_DIR_KEY);

    private Persistence() {
    }

    private static File getOrCreateFile(String pathname) throws IOException {
        var file = new File(pathname);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        return file;
    }

    public static <T> T load(String pathname, Type type, T defaultValue) throws IOException {
        var jsonFile = getOrCreateFile(DATA_DIR + pathname);
        var jsonString = Files.readString(jsonFile.toPath());
        if (jsonString.isEmpty()) {
            return defaultValue;
        } else {
            return GSON.fromJson(jsonString, type);
        }
    }

    public static <T> void save(T value, String pathname) throws IOException {
        var jsonFile = getOrCreateFile(DATA_DIR + pathname);
        Files.write(jsonFile.toPath(), GSON.toJson(value).getBytes());
    }
}
