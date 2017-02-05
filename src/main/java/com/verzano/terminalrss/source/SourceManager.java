package com.verzano.terminalrss.source;

import com.google.gson.reflect.TypeToken;
import com.verzano.terminalrss.content.ContentType;
import com.verzano.terminalrss.exception.SourceExistsException;
import com.verzano.terminalrss.persistence.Persistence;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class SourceManager {
  private SourceManager() {}

  private static final Map<Long, Source> SOURCES = new ConcurrentHashMap<>();
  private static final AtomicLong SOURCE_ID = new AtomicLong(0);

  private static final String SOURCES_FILE = "sources.json";
  private static final String SOURCES_ID_FILE = "sources.id";

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

  public static Long createSource(
      String uri,
      ContentType contentType,
      String contentTag,
      Date publishedDate,
      String title)
      throws IOException, SourceExistsException {
    if (SOURCES.values().stream().anyMatch(s -> s.getUri().equals(uri))) {
      throw new SourceExistsException("Source already exists for uri: " + uri);
    }

    Long id;
    synchronized (SOURCE_ID) {
      id = SOURCE_ID.incrementAndGet();
      Persistence.save(id, SOURCES_ID_FILE);
    }

    Source source = new Source(id, uri, contentType, contentTag, publishedDate, title);
    SOURCES.put(id, source);
    saveSources();

    return id;
  }

  // TODO catch this and if it fails put the Source back in
  public static boolean deleteSource(Long id) throws IOException {
    boolean removed;
    removed = SOURCES.remove(id) != null;
    if (removed) {
      saveSources();
    }

    return removed;
  }

  public static Collection<Source> getSources() {
    return SOURCES.values();
  }

  public static Source getSource(Long id) {
    return SOURCES.getOrDefault(id, Source.NULL_SOURCE);
  }

  private static synchronized void saveSources() throws IOException {
    Persistence.save(SOURCES.values(), SOURCES_FILE);
  }

}
