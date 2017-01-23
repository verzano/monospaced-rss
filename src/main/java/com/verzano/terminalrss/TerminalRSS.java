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
}
