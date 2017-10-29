package com.verzano.terminalrss.article.manager;

import com.google.gson.reflect.TypeToken;
import com.verzano.terminalrss.article.Article;
import com.verzano.terminalrss.content.ContentRetriever;
import com.verzano.terminalrss.exception.ArticleExistsException;
import com.verzano.terminalrss.persistence.Persistence;
import com.verzano.terminalrss.source.Source;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class ArticleManager {
  public static final Comparator<Article> UPDATED_AT_COMPARATOR = Comparator.comparing(Article::getPublishedAt).reversed();
  private static final int MAX_STORED_ARTICLES = 25;
  private static final Map<Long, Map<Long, Article>> ARTICLES = new ConcurrentHashMap<>();
  private static final AtomicLong ARTICLE_ID = new AtomicLong(0);
  private static final String ARTICLES_FILE = "articles.json";
  private static final String ARTICLES_ID_FILE = "articles.id";
  private static final Type ARTICLES_FILE_TYPE = new TypeToken<List<Article>>() {}.getType();

  static {
    try {
      List<Article> articles = Persistence.load(ARTICLES_FILE, ARTICLES_FILE_TYPE, new LinkedList<>());
      ARTICLES.putAll(articles.stream()
          .collect(Collectors.groupingBy(Article::getSourceId, Collectors.toMap(Article::getId, article -> article))));

      ARTICLE_ID.set(Persistence.load(ARTICLES_ID_FILE, Long.class, 0L));
    } catch(IOException e) {
      throw new RuntimeException(e);
    }
  }

  private ArticleManager() {}

  public static Article createArticle(Source source, String uri, Date publishedDate, String title, Date updatedDate)
      throws IOException, ArticleExistsException {
    if(getArticles(source).stream().anyMatch(a -> a.getUri().equals(uri))) {
      throw new ArticleExistsException("Article already exists for uri: " + uri);
    }

    String content = ContentRetriever.getContent(uri, source.getContentType(), source.getContentTag());

    Long id;
    synchronized(ARTICLE_ID) {
      id = ARTICLE_ID.incrementAndGet();
      Persistence.save(id, ARTICLES_ID_FILE);
    }

    Article article = new Article(id, uri, source.getId(), publishedDate, title, content, updatedDate);

    synchronized(ARTICLES) {
      Map<Long, Article> articles = ARTICLES.getOrDefault(source.getId(), new HashMap<>());
      articles.put(id, article);
      ARTICLES.put(source.getId(), articles);
      saveArticles();
    }

    return article;
  }

  public static boolean deleteArticle(Long sourceId, Long articleId) throws IOException {
    boolean removed;
    synchronized(ARTICLES) {
      removed = ARTICLES.get(sourceId).remove(articleId) != null;
      if(removed) {
        saveArticles();
      }
    }

    return removed;
  }

  public static boolean deleteArticlesForSource(Long sourceId) throws IOException {
    boolean removed;
    synchronized(ARTICLES) {
      removed = ARTICLES.remove(sourceId) != null;
      if(removed) {
        saveArticles();
      }
    }

    return removed;
  }

  public static Article getArticle(Long id) {
    Map<Long, Article> articles = ARTICLES.values().stream()
        .flatMap(map -> map.entrySet().stream())
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    return articles.get(id);
  }

  public static Article getArticle(Long sourceId, Long articleId) {
    return ARTICLES.getOrDefault(sourceId, new HashMap<>()).getOrDefault(articleId, Article.NULL_ARTICLE);
  }

  public static Collection<Article> getArticles(Source source) {
    return ARTICLES.getOrDefault(source.getId(), new HashMap<>()).values();
  }

  private static void saveArticles() throws IOException {
    Persistence.save(ARTICLES.values().stream().flatMap(map -> {
      List<Article> articles = new LinkedList<>(map.values());
      return articles.subList(0, Math.min(map.values().size(), MAX_STORED_ARTICLES)).stream();
    }).sorted(UPDATED_AT_COMPARATOR).collect(Collectors.toList()), ARTICLES_FILE);
  }
}
