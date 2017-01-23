package com.verzano.terminalrss.source;

import com.google.gson.reflect.TypeToken;
import com.verzano.terminalrss.persistence.Persistence;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

// TODO most likely synchronization isn't necessary, but maybe when the UI stuff is done
// TODO if synchronization is used might want to make everything more thread safe
// TODO don't allow duplicates of sources based on uri
public class SourceManager {
  private static final Map<Long, Source> SOURCES = new HashMap<>();
  private static final AtomicLong SOURCE_ID = new AtomicLong(0);

  private static final String TEMP_DIR = System.getProperty("java.io.tmpdir");
  private static final String SOURCES_FILE = TEMP_DIR + File.separator + "sources.json";
  private static final String SOURCES_ID_FILE = TEMP_DIR + File.separator + "sources.id";

  private static final Type SOURCES_FILE_TYPE = new TypeToken<LinkedList<Source>>(){}.getType();

  static {
    try {
      List<Source> sources = Persistence.load(SOURCES_FILE, SOURCES_FILE_TYPE, new LinkedList<Source>());
      SOURCES.putAll(sources.stream().collect(Collectors.toMap(Source::getId, source -> source)));

      SOURCE_ID.set(Persistence.load(SOURCES_ID_FILE, Long.class, 0L));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private SourceManager() {}

  public static Long createSource(
      String uri,
      String contentTag,
      Date publishedDate,
      String title)
      throws IOException {
    Long id;
    synchronized (SOURCE_ID) {
      id = SOURCE_ID.incrementAndGet();
      Persistence.save(id, SOURCES_ID_FILE);
    }

    Source source = new Source(id, uri, contentTag, publishedDate, title);
    synchronized (SOURCES) {
      SOURCES.put(id, source);
      saveSources();
    }

    return id;
  }

  // TODO catch this and if it fails put the Source back in
  public static boolean deleteSource(Long id) throws IOException {
    boolean removed;
    synchronized (SOURCES) {
      removed = SOURCES.remove(id) != null;
      if (removed) {
        saveSources();
      }
    }

    return removed;
  }

  public static Collection<Source> getSources() {
    return SOURCES.values();
  }

  public static Source getSource(Long id) {
    return SOURCES.getOrDefault(id, Source.NULL_SOURCE);
  }

  private static void saveSources() throws IOException {
    Persistence.save(SOURCES.values(), SOURCES_FILE);
  }

}
