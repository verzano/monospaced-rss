package com.verzano.terminalrss.tui.container.shelf;

import com.verzano.terminalrss.tui.constant.Orientation;
import com.verzano.terminalrss.tui.container.Container;
import com.verzano.terminalrss.tui.metric.Size;
import com.verzano.terminalrss.tui.widget.Widget;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

public class Shelf extends Container<ShelfOptions> {
  private List<Widget> widgetStack = new LinkedList<>();
  private Map<Widget, ShelfOptions> optionsMap = new HashMap<>();

  private Orientation orientation;

  @Getter
  @Setter
  private int spacing;

  public Shelf(Orientation orientation, int spacing) {
    this.orientation = orientation;
    this.spacing = spacing;
  }

  @Override
  public void addWidgetInternal(Widget widget, ShelfOptions options) {
    optionsMap.put(widget, options);
    widgetStack.add(widget);
  }

  @Override
  public void removeWidgetInternal(Widget widget) {
    optionsMap.remove(widget);
    widgetStack.remove(widget);
  }

  @Override
  public void removeWidgetsInternal() {
    optionsMap.clear();
    widgetStack.clear();
  }

  @Override
  public Collection<Widget> getContainedWidgets() {
    return widgetStack;
  }

  // TODO NPE if widget not in map
  @Override
  public int calculateWidgetWidth(Widget widget) {
    int width = optionsMap.get(widget).getSize().getWidth();

    switch (width) {
      case Size.FILL_CONTAINER:
        return getWidth();
      case Size.FILL_NEEDED:
        return widget.getNeededContentWidth();
      default:
        return width;
    }
  }

  // TODO NPE if widget not in map
  @Override
  public int calculateWidgetHeight(Widget widget) {
    int height = optionsMap.get(widget).getSize().getHeight();

    switch (height) {
      case Size.FILL_CONTAINER:
        return getHeight();
      case Size.FILL_NEEDED:
        return widget.getNeededHeight();
      default:
        return height;
    }
  }

  // TODO NPE if widget not in map
  @Override
  public int calculateWidgetX(Widget widget) {
    int x = getX();
    if (orientation == Orientation.HORIZONTAL) {
      for (Widget w : widgetStack) {
        if (widget == w) {
          break;
        }

        x += getWidgetWidth(w) + spacing;
      }
    }
    return x + this.getPadding().getLeft();
  }

  // TODO NPE if widget not in map
  @Override
  public int calculateWidgetY(Widget widget) {
    int y = getY();
    if (orientation == Orientation.VERTICAL) {
      for (Widget w : widgetStack) {
        if (widget == w) {
          break;
        }

        y += getWidgetHeight(w) + spacing;
      }
    }
    return y + this.getPadding().getTop();
  }

  @Override
  public int getNeededContentWidth() {
    int width = 0;
    if (!widgetStack.isEmpty()) {
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
              .orElse(0);
          break;
      }
    }
    return width + this.getPadding().getLeft() + this.getPadding().getRight();
  }

  @Override
  public int getNeededContentHeight() {
    int height = 0;
    if (!widgetStack.isEmpty()) {
      switch (orientation) {
        case HORIZONTAL:
          height = widgetStack.stream()
              .mapToInt(Widget::getHeight)
              .max()
              .orElse(0);
          break;
        case VERTICAL:
          height = (widgetStack.size() - 1) * spacing
              + widgetStack.stream()
              .mapToInt(Widget::getHeight)
              .sum();
          break;
      }
    }

    return height + this.getPadding().getTop() + this.getPadding().getBottom();
  }
}
