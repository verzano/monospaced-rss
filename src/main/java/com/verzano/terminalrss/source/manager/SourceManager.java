package com.verzano.terminalrss.source.manager;

import static com.verzano.terminalrss.source.Source.NULL_SOURCE;

import com.google.gson.reflect.TypeToken;
import com.verzano.terminalrss.content.ContentType;
import com.verzano.terminalrss.exception.SourceExistsException;
import com.verzano.terminalrss.persistence.Persistence;
import com.verzano.terminalrss.source.Source;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class SourceManager {
  public static final Comparator<Source> TITLE_COMPARATOR = Comparator.comparing(Source::getTitle).reversed();

  private static final Map<Long, Source> SOURCES = new ConcurrentHashMap<>();
  private static final AtomicLong SOURCE_ID = new AtomicLong(0);
  private static final String SOURCES_FILE = "sources.json";
  private static final String SOURCES_ID_FILE = "sources.id";
  private static final Type SOURCES_FILE_TYPE = new TypeToken<LinkedList<Source>>() {}.getType();

  static {
    try {
      List<Source> sources = Persistence.load(SOURCES_FILE, SOURCES_FILE_TYPE, new LinkedList<Source>());
      SOURCES.putAll(sources.stream().collect(Collectors.toMap(Source::getId, source -> source)));

      SOURCE_ID.set(Persistence.load(SOURCES_ID_FILE, Long.class, 0L));
    } catch(IOException e) {
      throw new RuntimeException(e);
    }
  }

  private SourceManager() {}

  public static Source createSource(
      String uri, ContentType contentType, String contentTag, Date publishedDate, String title) throws SourceExistsException {
    if(SOURCES.values().stream().anyMatch(s -> s.getUri().equals(uri))) {
      throw new SourceExistsException("Source already exists for uri: " + uri);
    }

    Long id;
    synchronized(SOURCE_ID) {
      id = SOURCE_ID.incrementAndGet();
      try {
        Persistence.save(id, SOURCES_ID_FILE);
      } catch(IOException e) {
        return NULL_SOURCE;
      }
    }

    Source source = new Source(id, uri, contentType, contentTag, publishedDate, title);
    SOURCES.put(id, source);
    try {
      saveSources();
    } catch(IOException e) {
      SOURCES.remove(id);
      source = NULL_SOURCE;
    }

    return source;
  }

  public static boolean deleteSource(Long id) {
    Source source = SOURCES.remove(id);
    boolean removed = source != null;
    if(removed) {
      try {
        saveSources();
      } catch(IOException e) {
        SOURCES.put(id, source);
        removed = false;
      }
    }

    return removed;
  }

  public static Source readSource(Long id) {
    return SOURCES.getOrDefault(id, NULL_SOURCE);
  }

  public static Collection<Source> readSources() {
    return SOURCES.values();
  }

  private static synchronized void saveSources() throws IOException {
    Persistence.save(SOURCES.values(), SOURCES_FILE);
  }

  // TODO allow editing of title?
  public static boolean updateSource(Long id, ContentType contentType, String contentTag) {
    Source source = SOURCES.get(id);
    boolean updated = source != null;
    if(updated) {
      String oldContentTag = source.getContentTag();
      source.setContentTag(contentTag);

      ContentType oldContentType = source.getContentType();
      source.setContentType(contentType);

      try {
        saveSources();
      } catch(IOException e) {
        updated = false;
        source.setContentTag(oldContentTag);
        source.setContentType(oldContentType);
      }
    }

    return updated;
  }
}
