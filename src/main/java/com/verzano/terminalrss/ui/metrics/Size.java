package com.verzano.terminalrss.ui.metrics;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Size {
  // TODO these sizes might not make sense here and might be better closer to Container...
  public static final int FILL_PARENT = -1;
  public static final int FILL_NEEDED = -2;
  public static final int FILL_REMAINING = -3;

  private volatile int width;
  private volatile int height;
}
