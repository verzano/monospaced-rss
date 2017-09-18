package com.verzano.terminalrss.tui.metric;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Margins {
  private int left;
  private int top;
  private int right;
  private int bottom;

  public Margins() {
    this(0);
  }

  public Margins(int margin) {
    this(margin, margin);
  }

  public Margins(int leftRight, int topBottom) {
    this(leftRight, topBottom, leftRight, topBottom);
  }
}
