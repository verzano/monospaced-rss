package com.verzano.monospaced.rss;

import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import com.verzano.logging.LogDealer;
import com.verzano.monospaced.gui.MonospacedGui;
import com.verzano.monospaced.gui.container.shelf.Shelf;
import com.verzano.monospaced.gui.container.shelf.ShelfOptions;
import com.verzano.monospaced.gui.metric.Size;
import com.verzano.monospaced.gui.widget.scrollable.list.ListWidget;
import com.verzano.monospaced.gui.widget.scrollable.list.model.SortedListModel;
import com.verzano.monospaced.gui.widget.scrollable.text.TextAreaWidget;
import com.verzano.monospaced.gui.widget.text.TextWidget;
import com.verzano.monospaced.rss.article.Article;
import com.verzano.monospaced.rss.article.manager.ArticleManager;
import com.verzano.monospaced.rss.content.ContentType;
import com.verzano.monospaced.rss.exception.ArticleExistsException;
import com.verzano.monospaced.rss.exception.SourceExistsException;
import com.verzano.monospaced.rss.persistence.Persistence;
import com.verzano.monospaced.rss.source.Source;
import com.verzano.monospaced.rss.source.gui.EditSourceFloater;
import com.verzano.monospaced.rss.source.manager.SourceManager;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.verzano.monospaced.gui.constant.Key.*;
import static com.verzano.monospaced.gui.constant.Orientation.HORIZONTAL;
import static com.verzano.monospaced.gui.constant.Orientation.VERTICAL;
import static com.verzano.monospaced.gui.constant.Position.LEFT;
import static com.verzano.monospaced.gui.metric.Size.FILL_CONTAINER;

/*
    addSource("https://techcrunch.com/feed/", CLASS_CONTENT, "article-entry");
    addSource("https://news.vice.com/feed", CLASS_CONTENT, "content");
    addSource("http://www.theverge.com/rss/index.xml", CLASS_CONTENT, "c-entry-content");
    addSource("http://feeds.reuters.com/reuters/technologyNews", ID_CONTENT, "article-text");
    addSource("http://motherboard.vice.com/rss", CLASS_CONTENT, "article-content");
    addSource("http://feeds.gawker.com/kotaku/full", CLASS_CONTENT, "entry-content");
 */
// TODO add in some sort of monitoring to see progress of adding sources and updating articles
// TODO use futures for that ^ ?
// TODO generify source a bit and make article part of some abstract class so that podcasts can be handled eventually
public class MonospacedRss {
  private static final Logger log = LogDealer.get(MonospacedRss.class);
  private static final ExecutorService sourceExecutor = Executors.newFixedThreadPool(3);
  private static final ExecutorService articleExecutor = Executors.newFixedThreadPool(6);
  private static final Source ADD_SOURCE = new Source(-1, "", ContentType.NULL_CONTENT_TYPE, "", null, "+ Add Source");
  private static final Article REFRESH_SOURCE = new Article(Article.NULL_ARTICLE_ID, "", -1, null, "\u21BB Refresh Source", "", null);

  private static Shelf baseContainer;
  private static ListWidget<Source> sourcesListWidget;
  private static ListWidget<Article> articlesListWidget;
  private static TextWidget sourceTextWidget;
  private static TextWidget articleTextWidget;
  private static TextAreaWidget contentTextAreaWidget;
  private static Source selectedSource = Source.NULL_SOURCE;

  private static void addSource(String uri, ContentType contentType, String contentTag) {
    sourceExecutor.execute(() -> {
      try {
        SyndFeed feed = new SyndFeedInput().build(new XmlReader(new URL(uri)));
        Source source = SourceManager.createSource(uri, contentType, contentTag, feed.getPublishedDate(), feed.getTitle());

        if(source == Source.NULL_SOURCE) {
          log.warning("Failed to create source for uri: " + uri
                  + ", contentType: " + contentType
                  + ", contentTag: " + contentTag);
        } else {
          sourcesListWidget.addItem(source);
        }
        sourcesListWidget.reprint();
        // TODO handle unknown host exceptions
      } catch(SourceExistsException e) {
        log.warning(e.getMessage());
      } catch(MalformedURLException e) {
        // TODO log this and throw own exception to allow re-attempt at url entry
      } catch(FeedException | IOException e) {
        log.log(Level.SEVERE, e.getMessage(), e);
      }
    });
  }

  private static void buildArticleWidgets() {
    articleTextWidget = new TextWidget("Articles:", HORIZONTAL, LEFT);

    articlesListWidget = new ListWidget<>(new SortedListModel<Article>(ArticleManager.UPDATED_AT_COMPARATOR) {
      @Override
      public Article getItemAt(int index) {
        return index == super.getItemCount() ? REFRESH_SOURCE : super.getItemAt(index);
      }

      @Override
      public int getItemCount() {
        return super.getItemCount() + 1;
      }
    });

    articlesListWidget.addKeyAction(ENTER, () -> {
      Article article = articlesListWidget.getSelectedItem();

      if(article == REFRESH_SOURCE) {
        refreshSource(selectedSource.getId());
      } else {
        showArticle();
        articleTextWidget.setText("Article: " + article.getTitle());

        contentTextAreaWidget.setText(article.getContent());
        contentTextAreaWidget.setFocused();
        MonospacedGui.reprint();
      }
    });

    articlesListWidget.addKeyAction(DELETE, () -> {
      showToSourcesList();
      selectedSource = Source.NULL_SOURCE;

      sourceTextWidget.setText("Sources:");

      sourcesListWidget.setFocused();
      MonospacedGui.reprint();
    });
  }

  private static void buildContentTextAreaWidget() {
    contentTextAreaWidget = new TextAreaWidget();

    contentTextAreaWidget.addKeyAction(DELETE, () -> {
      showArticlesList();
      articleTextWidget.setText("Articles:");

      articlesListWidget.setFocused();
      MonospacedGui.reprint();
    });
  }

  private static void buildSourceWidgets() {
    sourceTextWidget = new TextWidget("Sources:", HORIZONTAL, LEFT);

    sourcesListWidget = new ListWidget<>(new SortedListModel<Source>(SourceManager.TITLE_COMPARATOR) {
      @Override
      public Source getItemAt(int index) {
        return index == super.getItemCount() ? ADD_SOURCE : super.getItemAt(index);
      }

      @Override
      public int getItemCount() {
        return super.getItemCount() + 1;
      }
    });
    sourcesListWidget.setItems(SourceManager.readSources());

    sourcesListWidget.addKeyAction(ENTER, () -> {
      Source source = sourcesListWidget.getSelectedItem();
      if(source == ADD_SOURCE) {
        final EditSourceFloater editSourceFloater = new EditSourceFloater();
        editSourceFloater.setDisposeTask(() -> {
          if(editSourceFloater.isPositiveSelected()) {
            addSource(editSourceFloater.getUri(), editSourceFloater.getContentType(), editSourceFloater.getContentTag());
          }
          // TODO make this part of TerminalUI
          sourcesListWidget.setFocused();
        });
        editSourceFloater.display();
      } else {
        showArticlesList();

        selectedSource = source;

        sourceTextWidget.setText("Source: " + source.getTitle());

        articlesListWidget.setItems(ArticleManager.getArticles(source));
        articlesListWidget.setFocused();
        MonospacedGui.reprint();
      }
    });

    sourcesListWidget.addKeyAction(E_LOWER, () -> {
      Source source = sourcesListWidget.getSelectedItem();
      if(source != ADD_SOURCE) {
        final EditSourceFloater editSourceFloater = new EditSourceFloater(source);
        editSourceFloater.setDisposeTask(() -> {
          if(editSourceFloater.isPositiveSelected()) {
            modifySource(editSourceFloater.getSourceId(), editSourceFloater.getContentType(), editSourceFloater.getContentTag());
          }
          // TODO make this part of TerminalUI
          sourcesListWidget.setFocused();
        });
        editSourceFloater.display();
      }
    });

    sourcesListWidget.addKeyAction(D_LOWER, () -> {
      Source source = sourcesListWidget.getSelectedItem();
      if(source != ADD_SOURCE) {
        if(SourceManager.deleteSource(source.getId())) {
          sourcesListWidget.removeItem(source);
          sourcesListWidget.reprint();
        }
      }
    });

    sourcesListWidget.addKeyAction(DELETE, () -> {
      MonospacedGui.shutdown();
      articleExecutor.shutdownNow();
      sourceExecutor.shutdownNow();
    });
  }

  private static void modifySource(Long sourceId, ContentType contentType, String contentTag) {
    if(!SourceManager.updateSource(sourceId, contentType, contentTag)) {
      log.warning("Failed to update source for sourceId: " + sourceId);
    }

    sourcesListWidget.reprint();
  }

  private static void refreshSource(Long sourceId) {
    Source source = SourceManager.readSource(sourceId);
    if(source == Source.NULL_SOURCE) {
      log.warning("No source exists for sourceId: " + sourceId);
    } else {
      sourceExecutor.execute(() -> {
        try {
          SyndFeed feed = new SyndFeedInput().build(new XmlReader(new URL(source.getUri())));
          ((List<SyndEntryImpl>)feed.getEntries()).forEach(entry -> articleExecutor.execute(() -> {
            try {
              Article article = ArticleManager.createArticle(source,
                  entry.getUri(),
                  entry.getPublishedDate(),
                  entry.getTitle(),
                  entry.getUpdatedDate());
              articlesListWidget.addItem(article);
              articlesListWidget.reprint();
            } catch(ArticleExistsException e) {
              log.warning(e.getMessage());
            } catch(IOException e) {
              log.log(Level.SEVERE, e.getMessage(), e);
            }
          }));
        } catch(FeedException | IOException e) {
          log.log(Level.SEVERE, e.getMessage(), e);
        }
      });
    }
  }

  private static void setProgramSettings(String[] args) {
    String persistenceDir = System.getProperty("java.io.tmpdir") + File.separator + "data" + File.separator;

    if(args.length > 0) {
      persistenceDir = args[0];
    }

    System.setProperty(Persistence.PERSISTENCE_DIR_KEY, persistenceDir + File.separator);
  }

  private static void showArticle() {
    baseContainer.removeWidgets();
    baseContainer.addWidget(sourceTextWidget, new ShelfOptions(new Size(FILL_CONTAINER, 1)));
    baseContainer.addWidget(articleTextWidget, new ShelfOptions(new Size(FILL_CONTAINER, 1)));
    baseContainer.addWidget(contentTextAreaWidget, new ShelfOptions(new Size(FILL_CONTAINER, MonospacedGui.getHeight() - 3)));
  }

  private static void showArticlesList() {
    baseContainer.removeWidgets();
    baseContainer.addWidget(sourceTextWidget, new ShelfOptions(new Size(FILL_CONTAINER, 1)));
    baseContainer.addWidget(articleTextWidget, new ShelfOptions(new Size(FILL_CONTAINER, 1)));
    baseContainer.addWidget(articlesListWidget, new ShelfOptions(new Size(FILL_CONTAINER, MonospacedGui.getHeight() - 2)));
  }

  private static void showToSourcesList() {
    baseContainer.removeWidgets();
    baseContainer.addWidget(sourceTextWidget, new ShelfOptions(new Size(FILL_CONTAINER, 1)));
    baseContainer.addWidget(sourcesListWidget, new ShelfOptions(new Size(FILL_CONTAINER, MonospacedGui.getHeight() - 1)));
  }

  public static void main(String[] args) {
    log.info("Starting up...");
    setProgramSettings(args);

    buildSourceWidgets();
    buildArticleWidgets();
    buildContentTextAreaWidget();

    baseContainer = new Shelf(VERTICAL, 0);
    showToSourcesList();

    MonospacedGui.setBaseWidget(baseContainer);
    sourcesListWidget.setFocused();
    MonospacedGui.reprint();
  }
}
