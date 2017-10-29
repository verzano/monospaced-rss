package com.verzano.terminalui.container.shelf;

import com.verzano.terminalui.container.ContainerOptions;
import com.verzano.terminalui.metric.Size;

public class ShelfOptions extends ContainerOptions {
  private Size size;

  public ShelfOptions(Size size) {
    this.size = size;
  }

  public Size getSize() {
    return size;
  }

  public void setSize(Size size) {
    this.size = size;
  }
}
