package com.verzano.terminalrss.ui.widget.container.box;

import com.verzano.terminalrss.ui.metrics.Point;
import com.verzano.terminalrss.ui.metrics.Size;
import com.verzano.terminalrss.ui.widget.Widget;
import com.verzano.terminalrss.ui.widget.constants.Orientation;
import com.verzano.terminalrss.ui.widget.container.Container;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class BoxContainer extends Container {
  private List<Widget> widgetStack = new LinkedList<>();
  private Orientation orientation;

  @Getter @Setter
  private int spacing;

  public BoxContainer(Orientation orientation, int spacing, Size size) {
    super(size);
    this.orientation = orientation;
    this.spacing = spacing;
  }

  @Override
  public void addWidget(Widget widget) {
    super.addWidget(widget);
    switch (orientation) {
      case HORIZONTAL:
        widget.setLocation(new Point(
            getX() + widgetStack.stream().mapToInt(w -> w.getWidth() + spacing).sum(),
            getY()));
        break;
      case VERTICAL:
        widget.setLocation(new Point(
            getX(),
            getY() + widgetStack.stream().mapToInt(w -> w.getHeight() + spacing).sum()));
        break;
    }

    widgetStack.add(widget);
  }

  @Override
  public void removeWidgets() {
    super.removeWidgets();
    widgetStack.clear();
  }

  @Override
  public Collection<Widget> getContainedWidgets() {
    return widgetStack;
  }

  @Override
  public int getNeededWidth() {
    int width = 0;
    switch (orientation) {
      case HORIZONTAL:
        width = (widgetStack.size() - 1) * spacing
            + widgetStack.stream()
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
    switch (orientation) {
      case HORIZONTAL:
        height = widgetStack.stream()
            .mapToInt(Widget::getHeight)
            .max()
            .orElseGet(() -> 0);
        break;
      case VERTICAL:
        height = (widgetStack.size() - 1) * spacing
            + widgetStack.stream()
            .mapToInt(Widget::getHeight)
            .sum();
        break;
    }
    return height;
  }
}
