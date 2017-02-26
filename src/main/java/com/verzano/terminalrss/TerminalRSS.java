package com.verzano.terminalrss;

import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import com.verzano.terminalrss.article.Article;
import com.verzano.terminalrss.article.ArticleManager;
import com.verzano.terminalrss.content.ContentType;
import com.verzano.terminalrss.exception.ArticleExistsException;
import com.verzano.terminalrss.exception.SourceExistsException;
import com.verzano.terminalrss.source.Source;
import com.verzano.terminalrss.source.manager.SourceManager;
import com.verzano.terminalrss.source.tui.SourceFloater;
import com.verzano.terminalrss.tui.TerminalUI;
import com.verzano.terminalrss.tui.container.shelf.Shelf;
import com.verzano.terminalrss.tui.container.shelf.ShelfOptions;
import com.verzano.terminalrss.tui.metrics.Size;
import com.verzano.terminalrss.tui.widget.scrollable.list.ListWidget;
import com.verzano.terminalrss.tui.widget.scrollable.list.model.SortedListModel;
import com.verzano.terminalrss.tui.widget.scrollable.text.TextAreaWidget;
import com.verzano.terminalrss.tui.widget.text.TextWidget;
import lombok.extern.java.Log;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import static com.verzano.terminalrss.content.ContentType.NULL_TYPE;
import static com.verzano.terminalrss.tui.constants.Key.DELETE;
import static com.verzano.terminalrss.tui.constants.Key.ENTER;
import static com.verzano.terminalrss.tui.constants.Orientation.HORIZONTAL;
import static com.verzano.terminalrss.tui.constants.Orientation.VERTICAL;
import static com.verzano.terminalrss.tui.constants.Position.CENTER_LEFT;
import static com.verzano.terminalrss.tui.metrics.Size.FILL_CONTAINER;

/*
    addSource("https://techcrunch.com/feed/", CLASS_CONTENT, "article-entry");
    addSource("https://news.vice.com/feed", CLASS_CONTENT, "content");
    addSource("http://www.theverge.com/rss/index.xml", CLASS_CONTENT, "c-entry-content");
    addSource("http://feeds.reuters.com/reuters/technologyNews", ID_CONTENT, "article-text");
    addSource("http://motherboard.vice.com/rss", CLASS_CONTENT, "article-content");
    addSource("http://feeds.gawker.com/kotaku/full", CLASS_CONTENT, "entry-content");
 */
// TODO add in some sort of monitoring to see progress of adding sources and updating articles
// TODO use futures for that ^
// TODO editing of sources and deleting of sources/articles
// TODO handle the exceptions thrown by the Persistence class
// TODO generify source a bit and make article part of some abstract class so that podcasts can be handled eventually
@Log
public class TerminalRSS {
  private static Shelf baseContainer;

  private static ListWidget<Source> sourcesListWidget;
  private static ListWidget<Article> articlesListWidget;

  private static TextWidget sourceTextWidget;
  private static TextWidget articleTextWidget;

  private static TextAreaWidget contentTextAreaWidget;

  private static SourceFloater sourceFloater;

  private static final ExecutorService sourceExecutor = Executors.newFixedThreadPool(3);
  private static final ExecutorService articleExecutor = Executors.newFixedThreadPool(6);

  private static Source selectedSource = Source.NULL_SOURCE;

  private static final Source ADD_SOURCE = new Source(-1, "", NULL_TYPE, "", null, "+ Add Source");
  private static final Article REFRESH_SOURCE = new Article(-1, "", -1, null, "\u21BB Refresh Source", "", null);

  public static void main(String[] args) throws IOException, FeedException {
    buildSourceWidgets();
    buildArticleWidgets();
    buildContentTextAreaWidget();

    baseContainer = new Shelf(VERTICAL, 0);
    showToSourcesList();

    TerminalUI.setBaseWidget(baseContainer);
    sourcesListWidget.setFocused();
    TerminalUI.reprint();
  }

  private static void buildSourceWidgets() {
    sourceTextWidget = new TextWidget("Sources:", HORIZONTAL, CENTER_LEFT);

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
      if (source == ADD_SOURCE) {
        sourceFloater.clearSource();
        sourceFloater.setMode(false);
        sourceFloater.showFloater();
      } else {
        showArticlesList();

        selectedSource = source;

        sourceTextWidget.setText("Source: " + source.getTitle());

        articlesListWidget.setItems(ArticleManager.getArticles(source));
        articlesListWidget.setFocused();
        TerminalUI.reprint();
      }
    });

    sourcesListWidget.addKeyAction("e", () -> {
      Source source = sourcesListWidget.getSelectedItem();
      if (source != ADD_SOURCE) {
        sourceFloater.setSource(source);
        sourceFloater.setMode(true);
        sourceFloater.showFloater();
      }
    });

    sourcesListWidget.addKeyAction("d", () -> {
      Source source = sourcesListWidget.getSelectedItem();
      if (source != ADD_SOURCE) {
        if (SourceManager.deleteSource(source.getId())) {
          sourcesListWidget.removeItem(source);
          sourcesListWidget.reprint();
        }
      }
    });

    sourcesListWidget.addKeyAction(DELETE, () -> {
      TerminalUI.shutdown();
      articleExecutor.shutdownNow();
      sourceExecutor.shutdownNow();
    });

    sourceFloater = new SourceFloater(
        () -> {
          // TODO these two lines should be part of the TerminalUI so that I don't have to put them everywhere
          TerminalUI.removeFloater();
          sourcesListWidget.setFocused();
          TerminalUI.reprint();
          addSource(sourceFloater.getUri(), sourceFloater.getContentType(), sourceFloater.getContentTag());
        },
        () -> {
          TerminalUI.removeFloater();
          sourcesListWidget.setFocused();
          TerminalUI.reprint();
          modifySource(
              sourceFloater.getSourceId(),
              sourceFloater.getContentType(),
              sourceFloater.getContentTag());
        },
        () -> {
          TerminalUI.removeFloater();
          sourcesListWidget.setFocused();
          TerminalUI.reprint();
        });
  }

  private static void buildArticleWidgets() {
    articleTextWidget = new TextWidget("Articles:", HORIZONTAL, CENTER_LEFT);

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

      if (article == REFRESH_SOURCE) {
        refreshSource(selectedSource.getId());
      } else {
        showArticle();
        articleTextWidget.setText("Article: " + article.getTitle());

        contentTextAreaWidget.setText(article.getContent());
        contentTextAreaWidget.setFocused();
        TerminalUI.reprint();
      }
    });

    articlesListWidget.addKeyAction(DELETE, () -> {
      showToSourcesList();
      selectedSource = Source.NULL_SOURCE;

      sourceTextWidget.setText("Sources:");

      sourcesListWidget.setFocused();
      TerminalUI.reprint();
    });
  }

  private static void buildContentTextAreaWidget() {
    contentTextAreaWidget = new TextAreaWidget();

    contentTextAreaWidget.addKeyAction(DELETE, () -> {
      showArticlesList();
      articleTextWidget.setText("Articles:");

      articlesListWidget.setFocused();
      TerminalUI.reprint();
    });
  }

  private static void showToSourcesList() {
    baseContainer.removeWidgets();
    baseContainer.addWidget(sourceTextWidget, new ShelfOptions(new Size(FILL_CONTAINER, 1)));
    baseContainer.addWidget(sourcesListWidget, new ShelfOptions(new Size(FILL_CONTAINER, TerminalUI.getHeight() - 1)));
  }

  private static void showArticlesList() {
    baseContainer.removeWidgets();
    baseContainer.addWidget(sourceTextWidget, new ShelfOptions(new Size(FILL_CONTAINER, 1)));
    baseContainer.addWidget(articleTextWidget, new ShelfOptions(new Size(FILL_CONTAINER, 1)));
    baseContainer.addWidget(articlesListWidget, new ShelfOptions(new Size(FILL_CONTAINER, TerminalUI.getHeight() - 2)));
  }

  private static void showArticle() {
    baseContainer.removeWidgets();
    baseContainer.addWidget(sourceTextWidget, new ShelfOptions(new Size(FILL_CONTAINER, 1)));
    baseContainer.addWidget(articleTextWidget, new ShelfOptions(new Size(FILL_CONTAINER, 1)));
    baseContainer.addWidget(contentTextAreaWidget, new ShelfOptions(new Size(FILL_CONTAINER, TerminalUI.getHeight() - 3)));
  }

  private static void addSource(String uri, ContentType contentType, String contentTag) {
    sourceExecutor.execute(() -> {
      try {
        SyndFeed feed = new SyndFeedInput().build(new XmlReader(new URL(uri)));
        Source source = SourceManager.createSource(
            uri,
            contentType,
            contentTag,
            feed.getPublishedDate(),
            feed.getTitle());

        if (source == Source.NULL_SOURCE) {
          log.warning("Failed to create source for uri: " + uri
              + ", contentType: " + contentType
              + ", contentTag: " + contentTag);
        } else {
          sourcesListWidget.addItem(source);
        }
        sourcesListWidget.reprint();
      } catch (SourceExistsException e) {
        log.warning(e.getMessage());
      } catch (FeedException | IOException e) {
        log.log(Level.SEVERE, e.getMessage(), e);
      }
    });
  }

  private static void modifySource(Long sourceId, ContentType contentType, String contentTag) {
    if (!SourceManager.updateSource(sourceId, contentType, contentTag)) {
      log.warning("Failed to update source for sourceId: " + sourceId);
    }

    sourcesListWidget.reprint();
  }

  private static void refreshSource(Long sourceId) {
    Source source = SourceManager.readSource(sourceId);
    if (source == Source.NULL_SOURCE) {
      log.warning("No source exists for sourceId: " + sourceId);
    } else {
      sourceExecutor.execute(() -> {
        try {
          SyndFeed feed = new SyndFeedInput().build(new XmlReader(new URL(source.getUri())));
          ((List<SyndEntryImpl>) feed.getEntries()).forEach(entry -> articleExecutor.execute(() -> {
            try {
              Article article = ArticleManager.createArticle(
                  source,
                  entry.getUri(),
                  entry.getPublishedDate(),
                  entry.getTitle(),
                  entry.getUpdatedDate());
              articlesListWidget.addItem(article);
              articlesListWidget.reprint();
            } catch (ArticleExistsException e) {
              log.warning(e.getMessage());
            } catch (IOException e) {
              log.log(Level.SEVERE, e.getMessage(), e);
            }
          }));
        } catch(FeedException | IOException e) {
          log.log(Level.SEVERE, e.getMessage(), e);
        }
      });
    }
  }
}
