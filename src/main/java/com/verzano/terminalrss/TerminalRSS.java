package com.verzano.terminalrss;

import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import com.verzano.terminalrss.article.Article;
import com.verzano.terminalrss.article.ArticleManager;
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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static com.verzano.terminalrss.ui.widget.action.Key.DELETE;
import static com.verzano.terminalrss.ui.widget.action.Key.ENTER;

// TODO handle bad urls
// TODO generify source a bit and make article part of some abstract class so that podcasts can be handled eventually
// TODO do infinite scrolling for sources
// TODO add some persistence class for saving loading with json
// TODO recover from exceptions and stuff
public class TerminalRSS {
  public static void main(String[] args) throws IOException, FeedException {
    List<Source> sources = new LinkedList<>(SourceManager.getSources());
    if (sources.isEmpty()) {
      addSource("http://www.theverge.com/rss/index.xml", "c-entry-content");
      sources = new LinkedList<>(SourceManager.getSources());
    }

    ListWidget<Source> sourcesList = new ListWidget<>(sources);
    sourcesList.setZ(0);

    BarWidget sourceBar = new BarWidget("", Direction.HORIZONTAL);
    sourceBar.setZ(1);

    ListWidget<Article> articlesList = new ListWidget<>(Collections.emptyList());
    articlesList.setY(2);
    articlesList.setHeight(articlesList.getHeight() - 1);
    articlesList.setZ(2);

    sourcesList.addKeyAction(ENTER, () -> {
      TerminalUI.removeWidget(sourcesList);

      Source source = sourcesList.getSelectedRow();
      sourceBar.setLabel(source.getTitle());
      TerminalUI.addWidget(sourceBar);

      articlesList.setRows(new LinkedList<>(ArticleManager.getArticles(source.getId())));
      TerminalUI.addWidget(articlesList);
      articlesList.setFocused();
      TerminalUI.repaint();
    });

    BarWidget articleBar = new BarWidget("", Direction.HORIZONTAL);
    articleBar.setY(2);
    articleBar.setZ(3);

    TextAreaWidget articleText = new TextAreaWidget("");
    articleText.setY(3);
    articleText.setHeight(articleText.getHeight() - 2);
    articleText.setZ(4);

    articlesList.addKeyAction(ENTER, () -> {
      TerminalUI.removeWidget(articlesList);

      Article article = articlesList.getSelectedRow();
      articleBar.setLabel(article.getTitle());
      TerminalUI.addWidget(articleBar);

      articleText.setText(article.getContent());
      TerminalUI.addWidget(articleText);
      articleText.setFocused();
      TerminalUI.repaint();
    });

    articlesList.addKeyAction(DELETE, () -> {
      TerminalUI.removeWidget(articlesList);

      TerminalUI.removeWidget(sourceBar);

      TerminalUI.addWidget(sourcesList);
      sourcesList.setFocused();
      TerminalUI.repaint();
    });

    articleText.addKeyAction(DELETE, () -> {
      TerminalUI.removeWidget(articleText);

      TerminalUI.removeWidget(articleBar);

      TerminalUI.addWidget(articlesList);
      articlesList.setFocused();
      TerminalUI.repaint();
    });

    TerminalUI.addWidget(sourcesList);
    sourcesList.setFocused();
//    boolean quit = false;
//    printHelp();
//
//    Scanner scanner = new Scanner(System.in);
//    while (!quit) {
//      System.out.print(">>> ");
//
//      String command = scanner.nextLine();
//
//      String[] parts = command.split("\"?( |$)(?=(([^\"]*\"){2})*[^\"]*$)\"?");
//      if (parts.length > 0)
//        switch (parts[0]) {
//          case "as":
//            if (parts.length != 3) {
//              System.err.println("Usage:\n\tas <uri> <content_tag>");
//            } else {
//              addSource(parts[1], parts[2]);
//            }
//            break;
//          case "ds":
//            if (parts.length != 2) {
//              System.err.println("Usage:\n\tds <source_id>");
//            } else {
//              Long sourceId = null;
//              try {
//                sourceId = Long.parseLong(parts[1]);
//              } catch (NumberFormatException e) {
//                System.err.println("source_id should be a number");
//              }
//
//              if (sourceId != null) {
//                deleteSource(sourceId);
//              }
//            }
//            break;
//          case "us":
//            if (parts.length != 2) {
//              System.err.println("Usage:\n\tus <source_id>");
//            } else {
//              Long sourceId = null;
//              try {
//                sourceId = Long.parseLong(parts[1]);
//              } catch (NumberFormatException e) {
//                System.err.println("source_id should be a number");
//              }
//
//              if (sourceId != null) {
//                updateSource(sourceId);
//              }
//            }
//            break;
//          case "ls":
//            listSources();
//            break;
//          case "la":
//            if (parts.length != 2) {
//              System.err.println("Usage:\n\tla <source_id>");
//            } else {
//              Long sourceId = null;
//              try {
//                sourceId = Long.parseLong(parts[1]);
//              } catch (NumberFormatException e) {
//                System.err.println("source_id should be a number");
//              }
//
//              if (sourceId != null) {
//                listArticles(sourceId);
//              }
//            }
//            break;
//          case "ra":
//            if (parts.length != 3) {
//              System.err.println("Usage:\n\tra <source_id> <article_id>");
//            } else {
//              Long sourceId = null;
//              try {
//                sourceId = Long.parseLong(parts[1]);
//              } catch (NumberFormatException e) {
//                System.err.println("source_id should be a number");
//              }
//
//              Long articleId = null;
//              try {
//                articleId = Long.parseLong(parts[2]);
//              } catch (NumberFormatException e) {
//                System.err.println("article_id should be a number");
//              }
//
//              if (sourceId != null && articleId != null) {
//                readArticle(sourceId, articleId);
//              }
//            }
//            break;
//          case "q":
//            quit = true;
//            break;
//          case "h":
//          default:
//            printHelp();
//        }
//    }
//
//    System.out.println("Until next time...");
  }

  private static void addSource(String uri, String contentTag) {
    try {
      SyndFeed feed = new SyndFeedInput().build(new XmlReader(new URL(uri)));
      Long id = SourceManager.createSource(uri, contentTag, feed.getPublishedDate(), feed.getTitle());
      updateSource(id);
    } catch (FeedException | IOException e) {
      System.err.println("Failed to create source");
    }
  }

  private static void deleteSource(Long sourceId) {
    try {
      SourceManager.deleteSource(sourceId);
      ArticleManager.deleteArticlesForSource(sourceId);
    } catch (IOException e) {
      System.err.println("Failed to delete source");
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
            System.out.println("Failed to create article");
            e.printStackTrace();
          }
        });
      } catch(FeedException | IOException e){
        System.err.println("Failed to update source");
      }
    }
  }

  private static void listSources() {
    SourceManager.getSources().forEach(source ->
        System.out.println(source.getId() + " " + source.getTitle()));
  }

  private static void listArticles(Long sourceId) {
    Source source = SourceManager.getSource(sourceId);
    if (source == Source.NULL_SOURCE) {
      System.err.println("No source for source_id: " + sourceId);
    } else {
      System.out.println(source.getTitle());
      ArticleManager.getArticles(sourceId).forEach(article ->
          System.out.println("\t" + article.getId() + " " + article.getTitle()));
    }
  }

  private static void readArticle(Long sourceId, Long articleId) {
    Article article = ArticleManager.getArticle(sourceId, articleId);
    if (article == Article.NULL_ARTICLE) {
      System.err.println("No article for source_id: " + sourceId + " and article_id: " + articleId);
    } else {
      System.out.println(article.getTitle());
      System.out.println(article.getContent());
    }
  }

  private static void printHelp() {
    System.out.println("Usage:\n"
        + "\n"
        + "\t-- Sources -- \n"
        + "\tList Sources: ls\n"
        + "\tAdd Source: as <uri> <content tag>\n"
        + "\tDelete Source: ds <source_id>\n"
        + "\tUpdate SourceL us <source_id\n"
        + "\n"
        + "\t-- Articles --\n"
        + "\tList Articles: la <source_id>\n"
        + "\tRead Article: ra <source_id> <article_id>\n"
        + "\n"
        + "\t-- Misc --\n"
        + "\tHelp: h\n"
        + "\tQuit: q");
  }
}
