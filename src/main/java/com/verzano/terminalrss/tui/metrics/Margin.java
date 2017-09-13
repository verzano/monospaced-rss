package com.verzano.terminalrss.tui.metrics;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Margin {
  private int left;
  private int top;
  private int right;
  private int bottom;

  public Margin() {
    this(0);
  }

  public Margin(int pad) {
    this(pad, pad);
  }

  public Margin(int leftRight, int topBottom) {
    this(leftRight, topBottom, leftRight, topBottom);
  }
}
