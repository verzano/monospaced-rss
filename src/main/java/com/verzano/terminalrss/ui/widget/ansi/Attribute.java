package com.verzano.terminalrss.ui.widget.ansi;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
// TODO look into the reverters of these --> "ESC[2Xm"
public enum Attribute {
  NONE(-1),
  NORMAL(0),
  BOLD_ON(1),
  ITALICS_ON(3),
  UNDERLINE_ON(4),
  BLINK_ON(5),
  INVERSE_ON(7),
  INVISIBLE_ON(8),
  STRIKETHROUGH_ON(9),
  BOLD_OFF(22),
  ITALICS_OFF(23),
  UNDERLINE_OFF(24),
  BLINK_OFF(25),
  INVERSE_OFF(27),
  STRIKETHROUGH_OFF(29);

  private final int value;
}