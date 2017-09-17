package com.verzano.terminalrss.content;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.MalformedURLException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ContentRetriever {
  public static String getContent(String uri, ContentType contentType, String contentTag) throws IOException {
    Document doc;
    try {
      doc = Jsoup.connect(uri).get();
    } catch (IllegalArgumentException e) {
      throw new MalformedURLException(e.getMessage());
    }

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
