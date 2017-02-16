package com.verzano.terminalrss.ui.widget.ansi;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Background {
  NONE(-1),
  BLACK(40),
  RED(41),
  GREEN(42),
  YELLOW(43),
  BLUE(44),
  MAGENTA(45),
  CYAN(46),
  WHITE(47),
  DEFAULT(49);

  private final int value;
}