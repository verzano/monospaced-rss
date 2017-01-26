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
import com.verzano.terminalrss.ui.widget.TerminalWidget;
import com.verzano.terminalrss.ui.widget.bar.BarWidget;
import com.verzano.terminalrss.ui.widget.popup.AddSourcePopup;
import com.verzano.terminalrss.ui.widget.scrollable.list.ListWidget;
import com.verzano.terminalrss.ui.widget.scrollable.text.TextAreaWidget;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.verzano.terminalrss.content.ContentType.CLASS_CONTENT;
import static com.verzano.terminalrss.content.ContentType.ID_CONTENT;
import static com.verzano.terminalrss.content.ContentType.NULL_TYPE;
import static com.verzano.terminalrss.ui.widget.constants.Direction.HORIZONTAL;
import static com.verzano.terminalrss.ui.widget.constants.Key.DELETE;
import static com.verzano.terminalrss.ui.widget.constants.Key.ENTER;

// TODO handle bad urls
// TODO generify source a bit and make article part of some abstract class so that podcasts can be handled eventually
// TODO do infinite scrolling for sources
public class TerminalRSS {
  private static ListWidget<Source> sourcesListWidget;
  private static ListWidget<Article> articlesListWidget;

  private static BarWidget sourceBarWidget;
  private static BarWidget articleBarWidget;

  private static TextAreaWidget contentTextAreaWidget;

  private static BarWidget notificationBarWidget;

  private static AddSourcePopup addSourcePopup;

  private static final Executor sourceExecutor = Executors.newFixedThreadPool(3);
  private static final Executor articleExecutor = Executors.newFixedThreadPool(6);

  private static final Source ADD_SOURCE = new Source(-1, "", NULL_TYPE, "", null, "+ Add Source");
  // TODO maybe make this 'load newer'/'load older'
  private static final Article REFRESH_SOURCE = new Article(-1, "", -1, null, "\u21BB Refresh Source", "", null);

  public static void main(String[] args) throws IOException, FeedException {
    buildSourceWidgets();
    buildArticleWidgets();
    buildContentTextAreaWidget();
    buildNotificationBar();

    TerminalUI.addWidget(sourceBarWidget);
    TerminalUI.addWidget(sourcesListWidget);
    TerminalUI.addWidget(notificationBarWidget);
    sourcesListWidget.setFocused();
    TerminalUI.reprint();

    addSource("http://www.theverge.com/rss/index.xml", CLASS_CONTENT, "c-entry-content");
    addSource("http://feeds.reuters.com/reuters/technologyNews", ID_CONTENT, "article-text");
    addSource("https://techcrunch.com/feed/", CLASS_CONTENT, "article-entry");
    addSource("http://motherboard.vice.com/rss", CLASS_CONTENT, "article-content");
    addSource("https://news.vice.com/feed", CLASS_CONTENT, "content");
    addSource("http://feeds.gawker.com/kotaku/full", CLASS_CONTENT, "entry-content");
  }

  private static void buildSourceWidgets() {
    sourceBarWidget = new BarWidget("Sources:", HORIZONTAL, 1, 1);

    sourcesListWidget = new ListWidget<>(
        new LinkedList<>(SourceManager.getSources()),
        TerminalWidget.MATCH_TERMINAL,
        TerminalUI.getHeight() - 2,
        1,
        2);
    sourcesListWidget.addRow(ADD_SOURCE);

    addSourcePopup = new AddSourcePopup();

    sourcesListWidget.addKeyAction(ENTER, () -> {
      Source source = sourcesListWidget.getSelectedRow();
      if (source == ADD_SOURCE) {
        TerminalUI.addWidget(addSourcePopup);
        addSourcePopup.setFocused();
      } else {
        sourceBarWidget.setLabel("Source: " + source.getTitle());

        TerminalUI.removeWidget(sourcesListWidget);
        TerminalUI.addWidget(articleBarWidget);

        articlesListWidget.setRows(new LinkedList<>(ArticleManager.getArticles(source.getId())));
        articlesListWidget.addRow(REFRESH_SOURCE);
        TerminalUI.addWidget(articlesListWidget);
        articlesListWidget.setFocused();
      }
      TerminalUI.reprint();
    });
    sourcesListWidget.addKeyAction(DELETE, TerminalUI::shutdown);
  }

  private static void buildArticleWidgets() {
    articleBarWidget = new BarWidget("Articles:", HORIZONTAL,1 ,2);

    articlesListWidget = new ListWidget<>(
        Collections.emptyList(),
        TerminalWidget.MATCH_TERMINAL,
        TerminalUI.getHeight() - 3,
        1,
        3);

    articlesListWidget.addKeyAction(ENTER, () -> {
      Article article = articlesListWidget.getSelectedRow();

      if (article == REFRESH_SOURCE) {
        // TODO update the source
      } else {
        TerminalUI.removeWidget(articlesListWidget);

        articleBarWidget.setLabel("Article: " + article.getTitle());
        TerminalUI.addWidget(articleBarWidget);

        contentTextAreaWidget.setText(article.getContent());
        TerminalUI.addWidget(contentTextAreaWidget);
        contentTextAreaWidget.setFocused();
        TerminalUI.reprint();
      }
    });

    articlesListWidget.addKeyAction(DELETE, () -> {
      TerminalUI.removeWidget(articlesListWidget);

      TerminalUI.removeWidget(articleBarWidget);
      sourceBarWidget.setLabel("Sources:");

      TerminalUI.addWidget(sourcesListWidget);
      sourcesListWidget.setFocused();
      TerminalUI.reprint();
    });
  }

  private static void buildContentTextAreaWidget() {
    contentTextAreaWidget = new TextAreaWidget(
        "",
        TerminalWidget.MATCH_TERMINAL,
        TerminalUI.getHeight() - 4,
        1,
        3);

    contentTextAreaWidget.addKeyAction(DELETE, () -> {
      TerminalUI.removeWidget(contentTextAreaWidget);

      articleBarWidget.setLabel("Articles:");

      TerminalUI.addWidget(articlesListWidget);
      articlesListWidget.setFocused();
      TerminalUI.reprint();
    });
  }

  private static void buildNotificationBar() {
    notificationBarWidget = new BarWidget("", HORIZONTAL, 1, TerminalUI.getHeight());
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
      } catch (SourceExistsException | FeedException | IOException e) {
        // TODO logging
        notificationBarWidget.setLabel(e.getMessage());
        notificationBarWidget.reprint();
      }

      sourcesListWidget.reprint();
    });
  }

  private static void updateSource(Long sourceId) {
    Source source = SourceManager.getSource(sourceId);
    if (source == Source.NULL_SOURCE) {
      notificationBarWidget.setLabel("No source for sourceId: " + sourceId);
      notificationBarWidget.reprint();
    } else {
      sourceExecutor.execute(() -> {
        try {
          SyndFeed feed = new SyndFeedInput().build(new XmlReader(new URL(source.getUri())));
          ((List<SyndEntryImpl>) feed.getEntries()).forEach(entry -> {
            articleExecutor.execute(() -> {

              try {
                ArticleManager.createArticle(
                    sourceId,
                    source.getContentType(),
                    source.getContentTag(),
                    entry.getUri(),
                    entry.getPublishedDate(),
                    entry.getTitle(),
                    entry.getUpdatedDate());
              } catch (IOException | ArticleExistsException e) {
                // TODO logging
                notificationBarWidget.setLabel(e.getMessage());
                notificationBarWidget.reprint();
              }
            });
          });
        } catch(FeedException | IOException e) {
          // TODO logging
          notificationBarWidget.setLabel(e.getMessage());
          notificationBarWidget.reprint();
        }
      });
    }
  }
}
