package com.verzano.terminalrss.content;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class ContentRetriever {
  private ContentRetriever() { }

  // TODO maybe throw if it's an unsupported type???
  public static String getContent(
      String uri,
      ContentType contentType,
      String contentTag)
      throws IOException {
    Document doc = Jsoup.connect(uri).get();

    StringBuilder builder = new StringBuilder();
    // TODO actually parse through and separate into paragraphs
    switch (contentType) {
      case CLASS_CONTENT:
        // TODO might want to grab all of these and concatenate....
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
