package com.verzano.terminalrss.tui.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

// TODO fill out this class a lot more
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Key {
  public static final String ESCAPED_PREFIX = "\u001b[";
  // Escaped keys
  public static final String UP_ARROW = ESCAPED_PREFIX + "A";
  public static final String DOWN_ARROW = ESCAPED_PREFIX + "B";
  public static final String RIGHT_ARROW = ESCAPED_PREFIX + "C";
  public static final String LEFT_ARROW = ESCAPED_PREFIX + "D";
  public static final String BACKSPACE = (char) 8 + "";
  public static final String TAB = (char) 9 + "";
  public static final String ENTER = (char) 13 + "";
  public static final String DELETE = (char) 127 + "";
}
