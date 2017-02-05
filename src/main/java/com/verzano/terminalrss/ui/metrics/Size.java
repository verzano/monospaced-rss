package com.verzano.terminalrss.ui.metrics;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Size {
  public static final int MATCH_TERMINAL = -1;

  private int width;
  private int height;
}
