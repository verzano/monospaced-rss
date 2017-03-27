package com.verzano.terminalrss.content;

import com.verzano.terminalrss.tui.TuiStringable;

public enum ContentType implements TuiStringable {
  NULL_TYPE {
    @Override
    public String toTuiString() {
      return toString();
    }
  }, CLASS_CONTENT {
    @Override
    public String toTuiString() {
      return toString();
    }
  }, ID_CONTENT {
    @Override
    public String toTuiString() {
      return toString();
    }
  }
}
