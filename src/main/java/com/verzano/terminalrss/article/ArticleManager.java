package com.verzano.terminalrss.article;

import com.google.gson.reflect.TypeToken;
import com.verzano.terminalrss.content.ContentRetriever;
import com.verzano.terminalrss.content.ContentType;
import com.verzano.terminalrss.exception.ArticleExistsException;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

// TODO allow only N of the newest articles to be stored but allow N + M in memory
public class ArticleManager {
  private ArticleManager() { }

  private static final Map<Long, Map<Long, Article>> ARTICLES = new ConcurrentHashMap<>();
  private static final AtomicLong ARTICLE_ID = new AtomicLong(0);

  private static final String TEMP_DIR = System.getProperty("java.io.tmpdir");
  private static final String ARTICLES_FILE = TEMP_DIR + File.separator + "articles.json";
  private static final String ARTICLES_ID_FILE = TEMP_DIR + File.separator + "articles.id";

  private static final Type ARTICLES_FILE_TYPE = new TypeToken<List<Article>>(){}.getType();

  static {
    try {
      List<Article> articles = Persistence.load(ARTICLES_FILE, ARTICLES_FILE_TYPE, new LinkedList<>());
      ARTICLES.putAll(articles.stream().collect(Collectors.groupingBy(
          Article::getSourceId,
          Collectors.toMap(Article::getId, article -> article))));

      ARTICLE_ID.set(Persistence.load(ARTICLES_ID_FILE, Long.class, 0L));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  // TODO maybe just have this take a Source...
  public static Long createArticle(
      Long sourceId,
      ContentType contentType,
      String contentTag,
      String uri,
      Date publishedDate,
      String title,
      Date updatedDate)
      throws IOException, ArticleExistsException {
    if (getArticles(sourceId).stream().anyMatch(a -> a.getUri().equals(uri))) {
      throw new ArticleExistsException();
    }

    String content = ContentRetriever.getContent(uri, contentType, contentTag);

    Long id;
    synchronized (ARTICLE_ID) {
      id = ARTICLE_ID.incrementAndGet();
      Persistence.save(id, ARTICLES_ID_FILE);
    }

    Article article = new Article(
        id,
        uri,
        sourceId,
        publishedDate,
        title,
        content,
        updatedDate);

    synchronized (ARTICLES) {
      Map<Long, Article> articles = ARTICLES.getOrDefault(sourceId, new HashMap<>());
      articles.put(id, article);
      ARTICLES.put(sourceId, articles);
      saveArticles();
    }

    return id;
  }

  public static boolean deleteArticle(Long sourceId, Long articleId) throws IOException {
    boolean removed;
    synchronized (ARTICLES) {
      removed = ARTICLES.get(sourceId).remove(articleId) != null;
      if (removed) {
        saveArticles();
      }
    }

    return removed;
  }

  public static boolean deleteArticlesForSource(Long sourceId) throws IOException {
    boolean removed;
    synchronized (ARTICLES) {
      removed = ARTICLES.remove(sourceId) != null;
      if (removed) {
        saveArticles();
      }
    }

    return removed;
  }

  public static Collection<Article> getArticles(Long sourceId) {
    return ARTICLES.getOrDefault(sourceId, new HashMap<>()).values();
  }

  public static Article getArticle(Long id) {
    Map<Long, Article> articles = ARTICLES.values().stream()
        .flatMap(map -> map.entrySet().stream())
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    return articles.get(id);
  }

  public static Article getArticle(Long sourceId, Long articleId) {
    return ARTICLES.getOrDefault(sourceId, new HashMap<>())
        .getOrDefault(articleId, Article.NULL_ARTICLE);
  }

  private static void saveArticles() throws IOException {
    List<Article> articles = ARTICLES.values().stream()
        .flatMap(map -> map.values().stream())
        .collect(Collectors.toList());
    Persistence.save(articles, ARTICLES_FILE);
  }
}
