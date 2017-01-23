package com.verzano.terminalrss;

import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import com.verzano.terminalrss.article.Article;
import com.verzano.terminalrss.article.ArticleManager;
import com.verzano.terminalrss.exception.ArticleExistsException;
import com.verzano.terminalrss.exception.SourceExistsException;
import com.verzano.terminalrss.source.Source;
import com.verzano.terminalrss.source.SourceManager;
import com.verzano.terminalrss.ui.TerminalUI;
import com.verzano.terminalrss.ui.widget.bar.BarWidget;
import com.verzano.terminalrss.ui.widget.constants.Direction;
import com.verzano.terminalrss.ui.widget.list.ListWidget;
import com.verzano.terminalrss.ui.widget.text.TextAreaWidget;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static com.verzano.terminalrss.ui.widget.action.Key.DELETE;
import static com.verzano.terminalrss.ui.widget.action.Key.ENTER;

// TODO handle bad urls
// TODO generify source a bit and make article part of some abstract class so that podcasts can be handled eventually
// TODO do infinite scrolling for sources
public class TerminalRSS {
  private static ListWidget<Source> sourcesListWidget;
  private static ListWidget<Article> articlesListWidget;

  private static BarWidget sourceBarWidget;
  private static BarWidget articleBarWidget;

  private static TextAreaWidget contentTextAreaWidget;

  public static void main(String[] args) throws IOException, FeedException {
    Collection<Source> sources = SourceManager.getSources();
    if (sources.isEmpty()) {
      addSource("http://www.theverge.com/rss/index.xml", "c-entry-content");
    }

    buildSourceWidgets();
    buildArticleWidgets();
    buildContentTextAreaWidget();

    TerminalUI.addWidget(sourceBarWidget);
    TerminalUI.addWidget(sourcesListWidget);
    sourcesListWidget.setFocused();
    TerminalUI.reprint();
  }

  private static void buildSourceWidgets() {
    sourceBarWidget = new BarWidget("Sources:", Direction.HORIZONTAL);
    sourceBarWidget.setY(1);

    sourcesListWidget = new ListWidget<>(new LinkedList<>(SourceManager.getSources()));
    sourcesListWidget.setY(2);
    sourcesListWidget.setHeight(sourcesListWidget.getHeight() - 1);
    sourcesListWidget.setX(2);
    sourcesListWidget.setWidth(sourcesListWidget.getWidth() - 1);

    sourcesListWidget.addKeyAction(ENTER, () -> {
      Source source = sourcesListWidget.getSelectedRow();
      sourceBarWidget.setLabel("Source: " + source.getTitle());

      TerminalUI.removeWidget(sourcesListWidget);
      TerminalUI.addWidget(articleBarWidget);

      articlesListWidget.setRows(new LinkedList<>(ArticleManager.getArticles(source.getId())));
      TerminalUI.addWidget(articlesListWidget);
      articlesListWidget.setFocused();
      TerminalUI.reprint();
    });
    sourcesListWidget.addKeyAction(DELETE, TerminalUI::shutdown);
  }

  private static void buildArticleWidgets() {
    articleBarWidget = new BarWidget("Articles:", Direction.HORIZONTAL);
    articleBarWidget.setY(2);

    articlesListWidget = new ListWidget<>(Collections.emptyList());
    articlesListWidget.setY(3);
    articlesListWidget.setHeight(articlesListWidget.getHeight() - 2);
    articlesListWidget.setX(2);
    articlesListWidget.setWidth(articlesListWidget.getWidth() - 1);

    articlesListWidget.addKeyAction(ENTER, () -> {
      TerminalUI.removeWidget(articlesListWidget);

      Article article = articlesListWidget.getSelectedRow();
      articleBarWidget.setLabel("Article: " + article.getTitle());
      TerminalUI.addWidget(articleBarWidget);

      contentTextAreaWidget.setText(article.getContent());
      TerminalUI.addWidget(contentTextAreaWidget);
      contentTextAreaWidget.setFocused();
      TerminalUI.reprint();
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
    contentTextAreaWidget = new TextAreaWidget("");
    contentTextAreaWidget.setY(3);
    contentTextAreaWidget.setHeight(contentTextAreaWidget.getHeight() - 3);

    contentTextAreaWidget.addKeyAction(DELETE, () -> {
      TerminalUI.removeWidget(contentTextAreaWidget);

      articleBarWidget.setLabel("Articles:");

      TerminalUI.addWidget(articlesListWidget);
      articlesListWidget.setFocused();
      TerminalUI.reprint();
    });
  }

  private static void addSource(String uri, String contentTag) {
    try {
      SyndFeed feed = new SyndFeedInput().build(new XmlReader(new URL(uri)));
      Long id = SourceManager.createSource(uri, contentTag, feed.getPublishedDate(), feed.getTitle());
      updateSource(id);
    } catch (FeedException | IOException e) {
      // TODO logging
      throw new RuntimeException(e);
    } catch (SourceExistsException ignored) {
      // TODO notify the user
    }
  }

  private static void updateSource(Long sourceId) {
    Source source = SourceManager.getSource(sourceId);
    if (source == Source.NULL_SOURCE) {
      System.err.println("No source for source_id: " + sourceId);
    } else {
      try {
        SyndFeed feed = new SyndFeedInput().build(new XmlReader(new URL(source.getUri())));

        ((List<SyndEntryImpl>) feed.getEntries()).forEach(entry -> {
          try {
            ArticleManager.createArticle(
                sourceId,
                entry.getUri(),
                entry.getPublishedDate(),
                entry.getTitle(),
                Jsoup.connect(entry.getUri()).get().getElementsByClass(source.getContentTag()).get(0).text(),
                entry.getUpdatedDate());
          } catch (IOException e) {
            // TODO logging
            throw new RuntimeException(e);
          } catch (ArticleExistsException ignored) {
            // TODO notify the user
          }
        });
      } catch(FeedException | IOException e){
        System.err.println("Failed to update source");
      }
    }
  }
}
