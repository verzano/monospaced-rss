package com.verzano.terminalrss.ui.widget.container.floor;

import com.verzano.terminalrss.ui.TerminalUI;
import com.verzano.terminalrss.ui.widget.Widget;
import com.verzano.terminalrss.ui.widget.container.Container;

import java.util.Collection;
import java.util.Collections;

import static com.verzano.terminalrss.ui.metrics.Size.FILL_CONTAINER;
import static com.verzano.terminalrss.ui.metrics.Size.FILL_NEEDED;

public class Floor extends Container<FloorOptions> {
  private FloorOptions options = null;
  private Widget widget = NULL_WIDGET;

  @Override
  public int getHeight() {
    return TerminalUI.getHeight();
  }

  @Override
  public int getWidth() {
    return TerminalUI.getWidth();
  }

  @Override
  public void addWidgetAux(Widget widget, FloorOptions options) {
    this.widget = widget;
    this.options = options;
  }

  @Override
  public void removeWidgetAux(Widget widget) {
    if (widget == this.widget) {
      this.widget = NULL_WIDGET;
      options = null;
    }
  }

  @Override
  public void removeWidgetsAux() {
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
        return TerminalUI.getWidth();
      case FILL_NEEDED:
        return widget.getNeededWidth();
      default:
        return options.getSize().getWidth();
    }
  }

  @Override
  public int calculateWidgetHeight(Widget widget) {
    switch (options.getSize().getHeight()) {
      case FILL_CONTAINER:
        return TerminalUI.getHeight();
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
  public int getNeededWidth() {
    return TerminalUI.getWidth();
  }

  @Override
  public int getNeededHeight() {
    return TerminalUI.getHeight();
  }
}
