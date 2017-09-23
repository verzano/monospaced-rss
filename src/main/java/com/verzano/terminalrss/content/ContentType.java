package com.verzano.terminalrss.content;

import com.verzano.terminalrss.tui.TuiStringable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum ContentType implements TuiStringable {
  NULL_CONTENT_TYPE {
    @Override
    public String toTuiString() {
      return "Null";
    }
  }, CLASS_CONTENT {
    @Override
    public String toTuiString() {
      return "Class";
    }
  }, ID_CONTENT {
    @Override
    public String toTuiString() {
      return "ID";
    }
  };

  public static List<ContentType> nonNullValues() {
    return Arrays.stream(ContentType.values()).filter(ct -> ct != NULL_CONTENT_TYPE).collect(Collectors.toList());
  }
}
