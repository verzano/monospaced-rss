package com.verzano.terminalrss.content;

import com.verzano.terminalrss.tui.TUIStringable;

public enum ContentType implements TUIStringable {
  NULL_TYPE {
    @Override
    public String toTUIString() {
      return toString();
    }
  }, CLASS_CONTENT {
    @Override
    public String toTUIString() {
      return toString();
    }
  }, ID_CONTENT {
    @Override
    public String toTUIString() {
      return toString();
    }
  }
}
