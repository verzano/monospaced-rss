package com.verzano.terminalrss.ui.widget.ansi;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
// TODO work in the 8/256 colors
public enum Background {
  NONE(-1),

  DEFAULT(49),
  
  // 8 colors
  BLACK(40),
  RED(41),
  GREEN(42),
  YELLOW(43),
  BLUE(44),
  MAGENTA(45),
  CYAN(46),
  LIGHT_GRAY(47),
  
  // 16 colors
  DARK_GRAY(100),
  LIGHT_RED(101),
  LIGHT_GREEN(102),
  LIGHT_YELLOW(103),
  LIGHT_BLUE(104),
  LIGHT_MAGENTA(105),
  LIGHT_CYAN(106),
  WHITE(107);

  private final int value;
}