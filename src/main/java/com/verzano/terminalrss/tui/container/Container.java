package com.verzano.terminalrss.tui.container;

import com.verzano.terminalrss.tui.metrics.Point;
import com.verzano.terminalrss.tui.metrics.Size;
import com.verzano.terminalrss.tui.widget.Widget;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class Container<T extends ContainerOptions> extends Widget {

  public static final Container<ContainerOptions> NULL_CONTAINER = new Container<ContainerOptions>() {
    @Override
    public Collection<Widget> getContainedWidgets() {
      return Collections.emptyList();
    }

    @Override
    public int calculateWidgetWidth(Widget widget) {
      return 0;
    }

    @Override
    public int calculateWidgetHeight(Widget widget) {
      return 0;
    }

    @Override
    public int calculateWidgetX(Widget widget) {
      return 0;
    }

    @Override
    public int calculateWidgetY(Widget widget) {
      return 0;
    }

    @Override
    public void addWidgetAux(Widget widget, ContainerOptions options) {}

    @Override
    public void removeWidgetAux(Widget widget) {}

    @Override
    public void removeWidgetsAux() {}

    @Override
    public int getNeededWidth() {
      return 0;
    }

    @Override
    public int getNeededHeight() {
      return 0;
    }
  };
  private static final Size NO_SIZE = new Size(0, 0);
  private static final Point NO_LOCATION = new Point(0, 0);
  private final Map<Widget, Size> widgetSizes = new HashMap<>();
  private final Map<Widget, Point> widgetLocations = new HashMap<>();

  public abstract Collection<Widget> getContainedWidgets();

  public abstract int calculateWidgetWidth(Widget widget);

  public abstract int calculateWidgetHeight(Widget widget);

  public abstract int calculateWidgetX(Widget widget);

  public abstract int calculateWidgetY(Widget widget);

  // TODO need better names
  public abstract void addWidgetAux(Widget widget, T options);

  public abstract void removeWidgetAux(Widget widget);

  public abstract void removeWidgetsAux();

  public int getWidgetWidth(Widget widget) {
    return widgetSizes.getOrDefault(widget, NO_SIZE).getWidth();
  }

  public void setWidgetWidth(Widget widget) {
    widgetSizes.get(widget).setWidth(calculateWidgetWidth(widget));
  }

  public int getWidgetHeight(Widget widget) {
    return widgetSizes.getOrDefault(widget, NO_SIZE).getHeight();
  }

  public void setWidgetHeight(Widget widget) {
    widgetSizes.get(widget).setHeight(calculateWidgetHeight(widget));
  }

  public int getWidgetX(Widget widget) {
    return widgetLocations.getOrDefault(widget, NO_LOCATION).getX();
  }

  public void setWidgetX(Widget widget) {
    widgetLocations.get(widget).setX(calculateWidgetX(widget));
  }

  public int getWidgetY(Widget widget) {
    return widgetLocations.getOrDefault(widget, NO_LOCATION).getY();
  }

  public void setWidgetY(Widget widget) {
    widgetLocations.get(widget).setY(calculateWidgetY(widget));
  }

  public void addWidget(Widget widget, T options) {
    widgetSizes.put(widget, new Size(0, 0));
    widgetLocations.put(widget, new Point(0, 0));
    widget.setContainer(this);

    addWidgetAux(widget, options);
    arrange();
  }

  public void removeWidget(Widget widget) {
    widgetSizes.remove(widget);
    widgetLocations.remove(widget);
    widget.setContainer(NULL_CONTAINER);

    removeWidgetAux(widget);
    arrange();
  }

  public void removeWidgets() {
    widgetSizes.clear();
    widgetLocations.clear();
    getContainedWidgets().forEach(w -> w.setContainer(NULL_CONTAINER));

    removeWidgetsAux();
    arrange();
  }

  public void arrange() {
    getContainedWidgets().forEach(widget -> {
      setWidgetWidth(widget);
      setWidgetHeight(widget);
      setWidgetX(widget);
      setWidgetY(widget);

      if (widget instanceof Container) {
        ((Container) widget).arrange();
      }
    });

    size();
  }

  @Override
  public void printContent() {
    getContainedWidgets().forEach(Widget::printContent);
  }

  @Override
  public void setFocused() {
    Collection<Widget> widgets = getContainedWidgets();
    if (widgets.size() > 0) {
      widgets.iterator().next().setFocused();
    } else {
      super.setFocused();
    }
  }

  @Override
  public void size() {
    super.size();
    getContainedWidgets().forEach(Widget::size);
  }
}
