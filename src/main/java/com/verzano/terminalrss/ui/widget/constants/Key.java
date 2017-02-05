package com.verzano.terminalrss.ui.widget.constants;

public class Key {
  private Key() { }

  // Escaped keys
  public static final String UP_ARROW = Ansi.ESC + "[A";
  public static final String DOWN_ARROW = Ansi.ESC + "[B";
  public static final String RIGHT_ARROW = Ansi.ESC + "[C";
  public static final String LEFT_ARROW = Ansi.ESC + "[D";

  public static final String BACKSPACE = (char)8 + "";
  public static final String TAB = (char)9 + "";
  public static final String ENTER = (char)13 + "";
  public static final String DELETE = (char)127 + "";
}
