package com.verzano.terminalrss.content;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

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

    String content = "";
    switch (contentType) {
      case CLASS_CONTENT:
        // TODO might want to grab all of these and concatenate....
        content = doc.getElementsByClass(contentTag).get(0).text();
        break;
      case ID_CONTENT:
        content = doc.getElementById(contentTag).text();
        break;
    }

    return content;
  }
}
