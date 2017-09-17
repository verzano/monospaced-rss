package com.verzano.terminalrss.tui.widget.container;

import com.verzano.terminalrss.tui.container.Container;
import com.verzano.terminalrss.tui.container.ContainerOptions;
import com.verzano.terminalrss.tui.widget.Widget;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.LinkedList;

// TODO for now this should be enough but if tests start needing more complicated containers then...
// TODO this might be good as just an adapter pattern then could actually do a calculation...
@NoArgsConstructor
public class MockContainerBuilder {
  private Collection<Widget> containedWidgets = new LinkedList<>();
  private int calculatedWidgetWidth = 0;
  private int calculatedWidgetHeight = 0;
  private int calculatedWidgetX = 0;
  private int calculatedWidgetY = 0;
  private int neededWidth = 0;
  private int neededHeight = 0;
  private int widgetWidth = 0;
  private int widgetHeight = 0;
  private int widgetX = 0;
  private int widgetY = 0;

  public MockContainerBuilder containedWidgets(Collection<Widget> containedWidgets) {
    this.containedWidgets = containedWidgets;
    return this;
  }

  public MockContainerBuilder calculatedWidgetWidth(int calculatedWidgetWidth) {
    this.calculatedWidgetWidth = calculatedWidgetWidth;
    return this;
  }

  public MockContainerBuilder calculatedWidgetHeight(int calculatedWidgetHeight) {
    this.calculatedWidgetHeight = calculatedWidgetHeight;
    return this;
  }

  public MockContainerBuilder calculatedWidgetX(int calculatedWidgetX) {
    this.calculatedWidgetX = calculatedWidgetX;
    return this;
  }

  public MockContainerBuilder calculatedWidgetY(int calculatedWidgetY) {
    this.calculatedWidgetY = calculatedWidgetY;
    return this;
  }

  public MockContainerBuilder neededWidth(int neededWidth) {
    this.neededWidth = neededWidth;
    return this;
  }

  public MockContainerBuilder neededHeight(int neededHeight) {
    this.neededHeight = neededHeight;
    return this;
  }

  public MockContainerBuilder widgetWidth(int widgetWidth) {
    this.widgetWidth = widgetWidth;
    return this;
  }

  public MockContainerBuilder widgetHeight(int widgetHeight) {
    this.widgetHeight = widgetHeight;
    return this;
  }

  public MockContainerBuilder widgetX(int widgetX) {
    this.widgetX = widgetX;
    return this;
  }

  public MockContainerBuilder widgetY(int widgetY) {
    this.widgetY = widgetY;
    return this;
  }

  public Container build() {
    return new Container() {
      @Override
      public Collection<Widget> getContainedWidgets() {
        return containedWidgets;
      }

      @Override
      public int calculateWidgetWidth(Widget widget) {
        return calculatedWidgetWidth;
      }

      @Override
      public int calculateWidgetHeight(Widget widget) {
        return calculatedWidgetHeight;
      }

      @Override
      public int calculateWidgetX(Widget widget) {
        return calculatedWidgetX;
      }

      @Override
      public int calculateWidgetY(Widget widget) {
        return calculatedWidgetY;
      }

      @Override
      public void addWidgetInternal(Widget widget, ContainerOptions options) {}

      @Override
      public void removeWidgetInternal(Widget widget) {}

      @Override
      public void removeWidgetsInternal() {}

      @Override
      public int getNeededWidth() {
        return neededWidth;
      }

      @Override
      public int getNeededHeight() {
        return neededHeight;
      }

      @Override
      public int getWidgetWidth(Widget widget) {
        return widgetWidth;
      }

      @Override
      public int getWidgetHeight(Widget widget) {
        return widgetHeight;
      }

      @Override
      public int getWidgetX(Widget widget) {
        return widgetX;
      }

      @Override
      public int getWidgetY(Widget widget) {
        return widgetY;
      }
    };
  }
}
