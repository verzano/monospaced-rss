package com.verzano.terminalrss.tui.metrics;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class Padding {
  private int left;
  private int top;
  private int right;
  private int bottom;

  public Padding(int pad) {
    this(pad, pad, pad, pad);
  }

  public Padding(int leftRight, int topBottom) {
    this(leftRight, topBottom, leftRight, topBottom);
  }
}
