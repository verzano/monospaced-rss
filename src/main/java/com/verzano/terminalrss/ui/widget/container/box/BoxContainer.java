package com.verzano.terminalrss.ui.widget.container.box;

import com.verzano.terminalrss.ui.metrics.Location;
import com.verzano.terminalrss.ui.metrics.Size;
import com.verzano.terminalrss.ui.widget.Widget;
import com.verzano.terminalrss.ui.widget.constants.Direction;
import com.verzano.terminalrss.ui.widget.container.Container;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class BoxContainer extends Container {
  private List<Widget> widgetStack = new LinkedList<>();
  private Direction direction;

  public BoxContainer(Direction direction, Size size) {
    super(size);
    this.direction = direction;
  }

  @Override
  public void addWidget(Widget widget) {
    super.addWidget(widget);
    switch (direction) {
      case HORIZONTAL:
        widget.setLocation(new Location(
            getX() + widgetStack.stream().mapToInt(Widget::getWidth).sum(),
            getY()));
        break;
      case VERTICAL:
        widget.setLocation(new Location(
            getX(),
            getY() + widgetStack.stream().mapToInt(Widget::getHeight).sum()));
        break;
    }

    widgetStack.add(widget);
  }

  @Override
  public Collection<Widget> getContainedWidgets() {
    return widgetStack;
  }

  @Override
  public int getNeededWidth() {
    int width = 0;
    switch (direction) {
      case HORIZONTAL:
        width = widgetStack.stream()
            .mapToInt(Widget::getWidth)
            .sum();
        break;
      case VERTICAL:
        width = widgetStack.stream()
            .mapToInt(Widget::getWidth)
            .max()
            .orElseGet(() -> 0);
        break;
    }
    return width;
  }

  @Override
  public int getNeededHeight() {
    int height = 0;
    switch (direction) {
      case HORIZONTAL:
        height = widgetStack.stream()
            .mapToInt(Widget::getHeight)
            .max()
            .orElseGet(() -> 0);
        break;
      case VERTICAL:
        height = widgetStack.stream()
            .mapToInt(Widget::getHeight)
            .sum();
        break;
    }
    return height;
  }

  @Override
  public void size() {

  }
}
