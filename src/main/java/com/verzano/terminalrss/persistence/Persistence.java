package com.verzano.terminalrss.persistence;

import com.google.gson.Gson;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.file.Files;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Persistence {

  private static final Gson GSON = new Gson();
  // TODO for now just use the tmp dir... allow this to be configured later
  private static final String DATA_DIR = System.getProperty("java.io.tmpdir") + File.separator
      + "data" + File.separator;

  public static <T> T load(String pathname, Type type, T defaultValue) throws IOException {
    File jsonFile = getOrCreateFile(DATA_DIR + pathname);
    String jsonString = new String(Files.readAllBytes(jsonFile.toPath()), Charset.forName("UTF-8"));
    if (jsonString.isEmpty()) {
      return defaultValue;
    } else {
      return GSON.fromJson(jsonString, type);
    }
  }

  public static <T> void save(T value, String pathname) throws IOException {
    File jsonFile = getOrCreateFile(DATA_DIR + pathname);
    Files.write(jsonFile.toPath(), GSON.toJson(value).getBytes());
  }

  private static File getOrCreateFile(String pathname) throws IOException {
    File file = new File(pathname);
    if (!file.exists()) {
      file.getParentFile().mkdirs();
      file.createNewFile();
    }
    return file;
  }
}
