package com.verzano.monospaced.rss.content;

import com.verzano.monospaced.gui.widget.scrollable.list.model.Stringable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum ContentType implements Stringable {
  NULL_CONTENT_TYPE {
    @Override
    public String stringify() {
      return "Null";
    }
  }, CLASS_CONTENT {
    @Override
    public String stringify() {
      return "Class";
    }
  }, ID_CONTENT {
    @Override
    public String stringify() {
      return "ID";
    }
  };

  public static List<ContentType> nonNullValues() {
    return Arrays.stream(ContentType.values()).filter(ct -> ct != NULL_CONTENT_TYPE).collect(Collectors.toList());
  }
}
