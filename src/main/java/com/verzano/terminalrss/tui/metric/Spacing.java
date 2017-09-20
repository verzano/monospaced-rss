package com.verzano.terminalrss.tui.metric;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Spacing {
  private int left;
  private int top;
  private int right;
  private int bottom;

  public Spacing() {
    this(0);
  }

  public Spacing(int margin) {
    this(margin, margin);
  }

  public Spacing(int leftRight, int topBottom) {
    this(leftRight, topBottom, leftRight, topBottom);
  }
}
