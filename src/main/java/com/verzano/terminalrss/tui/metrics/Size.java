package com.verzano.terminalrss.tui.metrics;

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
}
