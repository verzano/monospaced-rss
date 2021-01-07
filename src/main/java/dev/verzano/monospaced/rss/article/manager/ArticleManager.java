package dev.verzano.monospaced.rss.article.manager;

import com.google.gson.reflect.TypeToken;
import dev.verzano.monospaced.rss.article.Article;
import dev.verzano.monospaced.rss.content.ContentRetriever;
import dev.verzano.monospaced.rss.exception.ArticleExistsException;
import dev.verzano.monospaced.rss.persistence.Persistence;
import dev.verzano.monospaced.rss.source.Source;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
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
    private static final Type ARTICLES_FILE_TYPE = new TypeToken<List<Article>>() {
    }.getType();

    static {
        try {
            List<Article> articles = Persistence.load(ARTICLES_FILE, ARTICLES_FILE_TYPE, new LinkedList<>());
            ARTICLES.putAll(articles.stream()
                    .collect(Collectors.groupingBy(Article::getSourceId, Collectors.toMap(Article::getId, article -> article))));

            ARTICLE_ID.set(Persistence.load(ARTICLES_ID_FILE, Long.class, 0L));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ArticleManager() {
    }

    public static Article createArticle(Source source, String uri, Date publishedDate, String title, Date updatedDate)
            throws IOException, ArticleExistsException {
        if (getArticles(source).stream().anyMatch(a -> a.getUri().equals(uri))) {
            throw new ArticleExistsException("Article already exists for uri: " + uri);
        }

        var content = ContentRetriever.getContent(uri, source.getContentType(), source.getContentTag());

        long id;
        synchronized (ARTICLE_ID) {
            id = ARTICLE_ID.incrementAndGet();
            Persistence.save(id, ARTICLES_ID_FILE);
        }

        var article = new Article(id, uri, source.getId(), publishedDate, title, content, updatedDate);
        synchronized (ARTICLES) {
            var articles = ARTICLES.getOrDefault(source.getId(), new HashMap<>());
            articles.put(id, article);
            ARTICLES.put(source.getId(), articles);
            saveArticles();
        }

        return article;
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

    public static Article getArticle(Long id) {
        var articles = ARTICLES.values().stream()
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
            var articles = new LinkedList<>(map.values());
            return articles.subList(0, Math.min(map.values().size(), MAX_STORED_ARTICLES)).stream();
        }).sorted(UPDATED_AT_COMPARATOR).collect(Collectors.toList()), ARTICLES_FILE);
    }
}
