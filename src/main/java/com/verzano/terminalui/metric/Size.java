package com.verzano.terminalui.metric;

public class Size {
  public static final int FILL_CONTAINER = -1;
  public static final int FILL_NEEDED = -2;
  private int width;
  private int height;

  public Size() {
    this(0, 0);
  }

  public Size(int width, int height) {
    this.width = width;
    this.height = height;
  }

  public int getHeight() {
    return height;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public int getWidth() {
    return width;
  }

  public void setWidth(int width) {
    this.width = width;
  }
}
