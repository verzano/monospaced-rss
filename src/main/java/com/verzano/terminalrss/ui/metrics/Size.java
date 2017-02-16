package com.verzano.terminalrss.ui.metrics;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Size {
  public static final int FILL_PARENT = -1;
  public static final int FILL_NEEDED = -2;

  private volatile int width;
  private volatile int height;
}
