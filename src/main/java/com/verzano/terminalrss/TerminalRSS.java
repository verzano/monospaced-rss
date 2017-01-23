package com.verzano.terminalrss;

import com.sun.syndication.io.FeedException;
import com.verzano.terminalrss.article.Article;
import com.verzano.terminalrss.article.ArticleManager;
import com.verzano.terminalrss.source.Source;
import com.verzano.terminalrss.source.SourceManager;
import com.verzano.terminalrss.ui.TerminalUI;
import com.verzano.terminalrss.ui.widget.bar.BarWidget;
import com.verzano.terminalrss.ui.widget.constants.Direction;
import com.verzano.terminalrss.ui.widget.list.ListWidget;
import com.verzano.terminalrss.ui.widget.text.TextAreaWidget;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;

import static com.verzano.terminalrss.ui.widget.action.Key.DELETE;
import static com.verzano.terminalrss.ui.widget.action.Key.ENTER;

// TODO handle bad urls
// TODO generify source a bit and make article part of some abstract class so that podcasts can be handled eventually
// TODO do infinite scrolling for sources
// TODO add some persistence class for saving loading with json
// TODO recover from exceptions and stuff
// TODO make all of the printing happen on the printing thread
public class TerminalRSS {
  private static ListWidget<Source> sourcesListWidget;
  private static ListWidget<Article> articlesListWidget;

  private static BarWidget sourceBarWidget;
  private static BarWidget articleBarWidget;

  private static TextAreaWidget contentTextAreaWidget;

  public static void main(String[] args) throws IOException, FeedException {
    buildSourceWidgets();
    buildArticleWidgets();
    buildContentTextAreaWidget();

    TerminalUI.addWidget(sourcesListWidget);
    sourcesListWidget.setFocused();
    TerminalUI.reprint();
  }

  private static void buildSourceWidgets() {
    sourcesListWidget = new ListWidget<>(new LinkedList<>(SourceManager.getSources()));

    sourcesListWidget.addKeyAction(ENTER, () -> {
      TerminalUI.removeWidget(sourcesListWidget);

      Source source = sourcesListWidget.getSelectedRow();
      sourceBarWidget.setLabel("Source - " + source.getTitle());
      TerminalUI.addWidget(sourceBarWidget);

      articlesListWidget.setRows(new LinkedList<>(ArticleManager.getArticles(source.getId())));
      TerminalUI.addWidget(articlesListWidget);
      articlesListWidget.setFocused();
      TerminalUI.reprint();
    });
    sourcesListWidget.addKeyAction(DELETE, TerminalUI::shutdown);

    sourceBarWidget = new BarWidget("", Direction.HORIZONTAL);
  }

  private static void buildArticleWidgets() {
    articlesListWidget = new ListWidget<>(Collections.emptyList());
    articlesListWidget.setY(2);
    articlesListWidget.setHeight(articlesListWidget.getHeight() - 1);

    articlesListWidget.addKeyAction(ENTER, () -> {
      TerminalUI.removeWidget(articlesListWidget);

      Article article = articlesListWidget.getSelectedRow();
      articleBarWidget.setLabel("Article - " + article.getTitle());
      TerminalUI.addWidget(articleBarWidget);

      contentTextAreaWidget.setText(article.getContent());
      TerminalUI.addWidget(contentTextAreaWidget);
      contentTextAreaWidget.setFocused();
      TerminalUI.reprint();
    });

    articlesListWidget.addKeyAction(DELETE, () -> {
      TerminalUI.removeWidget(articlesListWidget);

      TerminalUI.removeWidget(sourceBarWidget);

      TerminalUI.addWidget(sourcesListWidget);
      sourcesListWidget.setFocused();
      TerminalUI.reprint();
    });

    articleBarWidget = new BarWidget("", Direction.HORIZONTAL);
    articleBarWidget.setY(2);
  }

  private static void buildContentTextAreaWidget() {
    contentTextAreaWidget = new TextAreaWidget("");
    contentTextAreaWidget.setY(3);
    contentTextAreaWidget.setHeight(contentTextAreaWidget.getHeight() - 2);

    contentTextAreaWidget.addKeyAction(DELETE, () -> {
      TerminalUI.removeWidget(contentTextAreaWidget);

      TerminalUI.removeWidget(articleBarWidget);

      TerminalUI.addWidget(articlesListWidget);
      articlesListWidget.setFocused();
      TerminalUI.reprint();
    });
  }
}
