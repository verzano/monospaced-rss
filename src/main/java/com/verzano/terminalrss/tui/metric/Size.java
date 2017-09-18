package com.verzano.terminalrss.tui.metric;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Size {
  public static final int FILL_CONTAINER = -1;
  public static final int FILL_NEEDED = -2;

  private int width;
  private int height;

  public Size() {
    this(0, 0);
  }
}
