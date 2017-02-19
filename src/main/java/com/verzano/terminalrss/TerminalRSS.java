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
import com.verzano.terminalrss.source.SourceManager;
import com.verzano.terminalrss.ui.TerminalUI;
import com.verzano.terminalrss.ui.metrics.Size;
import com.verzano.terminalrss.ui.widget.AddSourceFloater;
import com.verzano.terminalrss.ui.widget.container.Container;
import com.verzano.terminalrss.ui.widget.container.shelf.ShelfContainer;
import com.verzano.terminalrss.ui.widget.scrollable.list.ListWidget;
import com.verzano.terminalrss.ui.widget.scrollable.text.TextAreaWidget;
import com.verzano.terminalrss.ui.widget.text.TextWidget;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.verzano.terminalrss.content.ContentType.NULL_TYPE;
import static com.verzano.terminalrss.ui.metrics.Size.FILL_PARENT;
import static com.verzano.terminalrss.ui.widget.constants.Key.DELETE;
import static com.verzano.terminalrss.ui.widget.constants.Key.ENTER;
import static com.verzano.terminalrss.ui.widget.constants.Orientation.HORIZONTAL;
import static com.verzano.terminalrss.ui.widget.constants.Orientation.VERTICAL;
import static com.verzano.terminalrss.ui.widget.constants.Position.CENTER_LEFT;

// TODO handle bad urls
// TODO generify source a bit and make article part of some abstract class so that podcasts can be handled eventually
/*
    addSource("https://techcrunch.com/feed/", CLASS_CONTENT, "article-entry");
    addSource("https://news.vice.com/feed", CLASS_CONTENT, "content");
    addSource("http://www.theverge.com/rss/index.xml", CLASS_CONTENT, "c-entry-content");
    addSource("http://feeds.reuters.com/reuters/technologyNews", ID_CONTENT, "article-text");
    addSource("http://motherboard.vice.com/rss", CLASS_CONTENT, "article-content");
    addSource("http://feeds.gawker.com/kotaku/full", CLASS_CONTENT, "entry-content");
 */
// TODO sort Sources by name
// TODO sort Articles by date
public class TerminalRSS {
  private static Container sourcesScreen;
  private static Container articlesScreen;
  private static Container contentScreen;

  private static ListWidget<Source> sourcesListWidget;
  private static ListWidget<Article> articlesListWidget;

  private static TextWidget sourceTextWidget;
  private static TextWidget articleTextWidget;

  private static TextAreaWidget contentTextAreaWidget;

  private static AddSourceFloater addSourceFloater;

  private static final ExecutorService sourceExecutor = Executors.newFixedThreadPool(3);
  private static final ExecutorService articleExecutor = Executors.newFixedThreadPool(6);

  private static Source selectedSource = Source.NULL_SOURCE;

  private static final Source ADD_SOURCE = new Source(-1, "", NULL_TYPE, "", null, "+ Add Source");
  // TODO maybe make this 'load newer'/'load older'
  private static final Article REFRESH_SOURCE = new Article(-1, "", -1, null, "\u21BB Refresh Source", "", null);

  public static void main(String[] args) throws IOException, FeedException {
    buildSourceWidgets();
    buildArticleWidgets();
    buildContentTextAreaWidget();

    sourcesScreen = new ShelfContainer(VERTICAL, 0, new Size(FILL_PARENT, FILL_PARENT));
    sourcesScreen.addWidget(sourceTextWidget);
    sourcesScreen.addWidget(sourcesListWidget);

    articlesScreen = new ShelfContainer(VERTICAL, 0, new Size(FILL_PARENT, FILL_PARENT));
    articlesScreen.addWidget(sourceTextWidget);
    articlesScreen.addWidget(articleTextWidget);
    articlesScreen.addWidget(articlesListWidget);

    contentScreen = new ShelfContainer(VERTICAL, 0, new Size(FILL_PARENT, FILL_PARENT));
    contentScreen.addWidget(sourceTextWidget);
    contentScreen.addWidget(articleTextWidget);
    contentScreen.addWidget(contentTextAreaWidget);

    TerminalUI.setBaseWidget(sourcesScreen);
    sourcesListWidget.setFocused();
    TerminalUI.reprint();
  }

  private static void buildSourceWidgets() {
    sourceTextWidget = new TextWidget("Sources:", HORIZONTAL, CENTER_LEFT, new Size(FILL_PARENT, 1));

    sourcesListWidget = new ListWidget<>(
        new LinkedList<>(SourceManager.getSources()),
        new Size(FILL_PARENT, TerminalUI.getHeight() - 2));
    sourcesListWidget.addRow(ADD_SOURCE);

    sourcesListWidget.addKeyAction(ENTER, () -> {
      Source source = sourcesListWidget.getSelectedRow();
      if (source == ADD_SOURCE) {
        addSourceFloater.clear();
        TerminalUI.setFloater(addSourceFloater);
      } else {
        TerminalUI.setBaseWidget(articlesScreen);
        selectedSource = source;

        sourceTextWidget.setText("Source: " + source.getTitle());

        articlesListWidget.setRows(new LinkedList<>(ArticleManager.getArticles(source.getId())));
        articlesListWidget.addRow(REFRESH_SOURCE);
        articlesListWidget.setFocused();
      }
      TerminalUI.reprint();
    });

    sourcesListWidget.addKeyAction(DELETE, () -> {
      TerminalUI.shutdown();
      articleExecutor.shutdownNow();
      sourceExecutor.shutdownNow();
    });

    addSourceFloater = new AddSourceFloater(
        () -> {
          TerminalUI.removeFloater();
          sourcesListWidget.setFocused();
          TerminalUI.reprint();
          addSource(addSourceFloater.getUri(), addSourceFloater.getContentType(), addSourceFloater.getContentTag());
        },
        () -> {
          TerminalUI.removeFloater();
          sourcesListWidget.setFocused();
          TerminalUI.reprint();
        });
  }

  private static void buildArticleWidgets() {
    articleTextWidget = new TextWidget("Articles:", HORIZONTAL, CENTER_LEFT, new Size(FILL_PARENT, 1));

    articlesListWidget = new ListWidget<>(new Size(FILL_PARENT, TerminalUI.getHeight() - 3));

    articlesListWidget.addKeyAction(ENTER, () -> {
      Article article = articlesListWidget.getSelectedRow();

      if (article == REFRESH_SOURCE) {
        updateSource(selectedSource.getId());
      } else {
        TerminalUI.setBaseWidget(contentScreen);
        articleTextWidget.setText("Article: " + article.getTitle());

        contentTextAreaWidget.setText(article.getContent());
        contentTextAreaWidget.setFocused();
        TerminalUI.reprint();
      }
    });

    articlesListWidget.addKeyAction(DELETE, () -> {
      TerminalUI.setBaseWidget(sourcesScreen);
      selectedSource = Source.NULL_SOURCE;

      sourceTextWidget.setText("Sources:");

      sourcesListWidget.setFocused();
      TerminalUI.reprint();
    });
  }

  private static void buildContentTextAreaWidget() {
    contentTextAreaWidget = new TextAreaWidget(new Size(FILL_PARENT, TerminalUI.getHeight() - 4));

    contentTextAreaWidget.addKeyAction(DELETE, () -> {
      TerminalUI.setBaseWidget(articlesScreen);
      articleTextWidget.setText("Articles:");

      articlesListWidget.setFocused();
      TerminalUI.reprint();
    });
  }

  private static void addSource(String uri, ContentType contentType, String contentTag) {
    sourceExecutor.execute(() -> {
      try {
        SyndFeed feed = new SyndFeedInput().build(new XmlReader(new URL(uri)));
        SourceManager.createSource(
            uri,
            contentType,
            contentTag,
            feed.getPublishedDate(),
            feed.getTitle());
        sourcesListWidget.setRows(new LinkedList<>(SourceManager.getSources()));
        sourcesListWidget.addRow(ADD_SOURCE);
        sourcesListWidget.reprint();
      } catch (SourceExistsException | FeedException | IOException e) {
        // TODO logging
      }

      sourcesListWidget.reprint();
    });
  }

  private static void updateSource(Long sourceId) {
    Source source = SourceManager.getSource(sourceId);
    if (source == Source.NULL_SOURCE) {
      // TODO logggggg
    } else {
      sourceExecutor.execute(() -> {
        try {
          SyndFeed feed = new SyndFeedInput().build(new XmlReader(new URL(source.getUri())));
          ((List<SyndEntryImpl>) feed.getEntries()).forEach(entry -> articleExecutor.execute(() -> {

            try {
              ArticleManager.createArticle(
                  sourceId,
                  source.getContentType(),
                  source.getContentTag(),
                  entry.getUri(),
                  entry.getPublishedDate(),
                  entry.getTitle(),
                  entry.getUpdatedDate());
              // TODO this is inefficient and migt happen out of order
              articlesListWidget.setRows(new LinkedList<>(ArticleManager.getArticles(source.getId())));
              articlesListWidget.addRow(REFRESH_SOURCE);
              articlesListWidget.reprint();
            } catch (IOException | ArticleExistsException e) {
              // TODO logging
            }
          }));
        } catch(FeedException | IOException e) {
          // TODO logging
        }
      });
    }
  }
}
