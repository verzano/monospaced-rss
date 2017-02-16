package com.verzano.terminalrss.ui.widget.ansi;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
// TODO work in the 8/256 colors
public enum Foreground {
  NONE(-1),

  DEFAULT(39),

  // 8 Colors
  BLACK(30),
  RED(31),
  GREEN(32),
  YELLOW(33),
  BLUE(34),
  MAGENTA(35),
  CYAN(36),
  LIGHT_GRAY(37),

  // 16 colors
  DARK_GRAY(90),
  LIGHT_RED(91),
  LIGHT_GREEN(92),
  LIGHT_YELLOW(93),
  LIGHT_BLUE(94),
  LIGHT_MAGENTA(95),
  LIGHT_CYAN(96),
  WHITE(97);

  private final int value;
}
