package com.verzano.terminalrss.tui.container.floor;

import static com.verzano.terminalrss.tui.metric.Size.FILL_CONTAINER;
import static com.verzano.terminalrss.tui.metric.Size.FILL_NEEDED;

import com.verzano.terminalrss.tui.TerminalUi;
import com.verzano.terminalrss.tui.container.Container;
import com.verzano.terminalrss.tui.widget.Widget;
import java.util.Collection;
import java.util.Collections;

public class Floor extends Container<FloorOptions> {
  private FloorOptions options = null;
  private Widget widget = NULL_WIDGET;

  @Override
  public int getHeight() {
    return TerminalUi.getHeight();
  }

  @Override
  public int getWidth() {
    return TerminalUi.getWidth();
  }

  @Override
  public void addWidgetInternal(Widget widget, FloorOptions options) {
    if (this.widget != NULL_WIDGET) {
      removeWidget(this.widget);
    }
    this.widget = widget;
    this.options = options;
  }

  @Override
  public void removeWidgetInternal(Widget widget) {
    this.widget = NULL_WIDGET;
    options = null;
  }

  @Override
  public void removeWidgetsInternal() {
    widget = NULL_WIDGET;
    options = null;
  }

  @Override
  public Collection<Widget> getContainedWidgets() {
    return Collections.singleton(widget);
  }

  @Override
  public int calculateWidgetWidth(Widget widget) {
    switch (options.getSize().getWidth()) {
      case FILL_CONTAINER:
        return TerminalUi.getWidth();
      case FILL_NEEDED:
        return widget.getNeededContentWidth();
      default:
        return options.getSize().getWidth();
    }
  }

  @Override
  public int calculateWidgetHeight(Widget widget) {
    switch (options.getSize().getHeight()) {
      case FILL_CONTAINER:
        return TerminalUi.getHeight();
      case FILL_NEEDED:
        return widget.getNeededHeight();
      default:
        return options.getSize().getHeight();
    }
  }

  @Override
  public int calculateWidgetX(Widget widget) {
    return options.getLocation().getX();
  }

  @Override
  public int calculateWidgetY(Widget widget) {
    return options.getLocation().getY();
  }

  @Override
  public int getNeededContentWidth() {
    return TerminalUi.getWidth();
  }

  @Override
  public int getNeededContentHeight() {
    return TerminalUi.getHeight();
  }
}
