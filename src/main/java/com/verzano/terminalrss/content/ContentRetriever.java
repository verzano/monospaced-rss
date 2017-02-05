package com.verzano.terminalrss.content;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class ContentRetriever {
  private ContentRetriever() { }

  public static String getContent(
      String uri,
      ContentType contentType,
      String contentTag)
      throws IOException {
    Document doc = Jsoup.connect(uri).get();

    StringBuilder builder = new StringBuilder();
    switch (contentType) {
      case CLASS_CONTENT:
        doc.getElementsByClass(contentTag).get(0).getElementsByTag("p")
            .stream()
            .map(Element::text)
            .forEach(t -> builder.append(t).append("\n"));
        break;
      case ID_CONTENT:
        doc.getElementById(contentTag).getElementsByTag("p")
            .stream()
            .map(Element::text)
            .forEach(t -> builder.append(t).append("\n"));
        break;
    }

    return builder.toString();
  }
}
