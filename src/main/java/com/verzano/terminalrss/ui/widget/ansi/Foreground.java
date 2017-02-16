package com.verzano.terminalrss.ui.widget.ansi;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Foreground {
  NONE(-1),
  BLACK(30),
  RED(31),
  GREEN(32),
  YELLOW(33),
  BLUE(34),
  MAGENTA(35),
  CYAN(36),
  WHITE(37),
  DEFAULT(39);

  private final int value;
}
