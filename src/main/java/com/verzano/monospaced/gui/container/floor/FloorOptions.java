package com.verzano.monospaced.gui.container.floor;

import com.verzano.monospaced.gui.container.ContainerOptions;
import com.verzano.monospaced.gui.metric.Point;
import com.verzano.monospaced.gui.metric.Size;

public class FloorOptions extends ContainerOptions {
  private Size size;
  private Point location;

  public FloorOptions(Size size, Point location) {
    this.size = size;
    this.location = location;
  }

  public Point getLocation() {
    return location;
  }

  public void setLocation(Point location) {
    this.location = location;
  }

  public Size getSize() {
    return size;
  }

  public void setSize(Size size) {
    this.size = size;
  }
}
