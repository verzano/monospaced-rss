package com.verzano.terminalrss.ui.widget.constants;

import com.verzano.terminalrss.ui.widget.ansi.Ansi;

public class Key {
  private Key() { }

  public static final String ESCAPED_PREFIX = Ansi.ESC + "[";

  // Escaped keys
  public static final String UP_ARROW = ESCAPED_PREFIX + "A";
  public static final String DOWN_ARROW = ESCAPED_PREFIX + "B";
  public static final String RIGHT_ARROW = ESCAPED_PREFIX + "C";
  public static final String LEFT_ARROW = ESCAPED_PREFIX + "D";

  public static final String BACKSPACE = (char)8 + "";
  public static final String TAB = (char)9 + "";
  public static final String ENTER = (char)13 + "";
  public static final String DELETE = (char)127 + "";
}
