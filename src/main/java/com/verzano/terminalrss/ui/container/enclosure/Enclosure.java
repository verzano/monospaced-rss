package com.verzano.terminalrss.ui.container.enclosure;

import com.verzano.terminalrss.ui.constants.CardinalDirection;
import com.verzano.terminalrss.ui.container.Container;
import com.verzano.terminalrss.ui.widget.Widget;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.verzano.terminalrss.ui.constants.CardinalDirection.CENTER;
import static com.verzano.terminalrss.ui.constants.CardinalDirection.EAST;
import static com.verzano.terminalrss.ui.constants.CardinalDirection.NORTH;
import static com.verzano.terminalrss.ui.constants.CardinalDirection.SOUTH;
import static com.verzano.terminalrss.ui.constants.CardinalDirection.WEST;

public class Enclosure extends Container<EnclosureOptions> {
  // TODO a bidi map would be noice
  private Map<Widget, EnclosureOptions> optionsMap = new HashMap<>();
  private Map<CardinalDirection, Widget> widgetMap = new HashMap<>();

  public Enclosure() {
    Arrays.stream(CardinalDirection.values()).forEach(cd -> widgetMap.put(cd, NULL_WIDGET));
  }

  @Override
  public void arrange() {
    Widget north = widgetMap.get(NORTH);
    if (north != NULL_WIDGET) {
      setWidgetData(north);
    }

    Widget west = widgetMap.get(WEST);
    if (west != NULL_WIDGET) {
      setWidgetData(west);
    }

    Widget center = widgetMap.get(CENTER);
    if (center != NULL_WIDGET) {
      setWidgetData(center);
    }

    Widget east = widgetMap.get(EAST);
    if (east != NULL_WIDGET) {
      setWidgetData(east);
    }

    Widget south = widgetMap.get(SOUTH);
    if (south != NULL_WIDGET) {
      setWidgetData(south);
    }

    size();
  }

  private void setWidgetData(Widget widget) {
    setWidgetWidth(widget);
    setWidgetHeight(widget);
    setWidgetX(widget);
    setWidgetY(widget);
  }

  @Override
  public Collection<Widget> getContainedWidgets() {
    return widgetMap.values().stream()
        .filter(w -> w != NULL_WIDGET)
        .collect(Collectors.toList());
  }

  @Override
  public int calculateWidgetWidth(Widget widget) {
    int width = 0;
    switch (optionsMap.get(widget).getPosition()) {
      case NORTH:
      case SOUTH:
        width = getWidth();
        break;
      case EAST:
      case WEST:
        // TODO maybe make this fill if there aren't others...
        width = getNeededWidth();
        break;
      case CENTER:
        width = getWidth() - getWidgetWidth(widgetMap.get(EAST)) - getWidgetWidth(widgetMap.get(WEST));
        break;
    }
    return width;
  }

  @Override
  public int calculateWidgetHeight(Widget widget) {
    int height = 0;
    switch (optionsMap.get(widget).getPosition()) {
      case NORTH:
      case SOUTH:
        // TODO maybe make this fill if there aren't others...
        height = getNeededHeight();
        break;
      case EAST:
      case WEST:
      case CENTER:
        height = getHeight() - getWidgetHeight(widgetMap.get(EAST)) - getWidgetHeight(widgetMap.get(WEST));
        break;
    }
    return height;
  }

  @Override
  public int calculateWidgetX(Widget widget) {
    int x = 0;
    switch (optionsMap.get(widget).getPosition()) {
      case NORTH:
      case SOUTH:
      case WEST:
        break;
      case EAST:
        x += getWidgetWidth(widgetMap.get(CENTER));
      case CENTER:
        x += getWidgetWidth(widgetMap.get(WEST));
        break;
    }
    return x;
  }

  @Override
  public int calculateWidgetY(Widget widget) {
    int y = 0;
    switch (optionsMap.get(widget).getPosition()) {
      case NORTH:
        break;
      case EAST:
      case WEST:
      case CENTER:
        y += getWidgetHeight(widgetMap.get(NORTH));
        break;
      case SOUTH:
        y += getWidgetHeight(widgetMap.get(CENTER));
        break;
    }
    return y;
  }

  @Override
  public void addWidgetAux(Widget widget, EnclosureOptions options) {
    Widget oldWidget = widgetMap.get(options.getPosition());
    if (oldWidget != NULL_WIDGET) {
      removeWidget(oldWidget);
    }
    optionsMap.put(widget, options);
    widgetMap.put(options.getPosition(), widget);
  }

  @Override
  public void removeWidgetAux(Widget widget) {
    EnclosureOptions options = optionsMap.remove(widget);
    widgetMap.remove(options.getPosition());
  }

  @Override
  public void removeWidgetsAux() {
    optionsMap.clear();
    Arrays.stream(CardinalDirection.values()).forEach(cd -> widgetMap.put(cd, NULL_WIDGET));
  }

  // TODO this could be better I think
  @Override
  public int getNeededWidth() {
    int northWidth = widgetMap.getOrDefault(NORTH, NULL_WIDGET).getNeededWidth();
    int southWidth = widgetMap.getOrDefault(SOUTH, NULL_WIDGET).getNeededWidth();
    int middleWidth = widgetMap.getOrDefault(WEST, NULL_WIDGET).getNeededWidth()
        + widgetMap.getOrDefault(CENTER, NULL_WIDGET).getNeededWidth()
        + widgetMap.getOrDefault(EAST, NULL_WIDGET).getNeededWidth();
    return Math.max(northWidth, Math.max(southWidth, middleWidth));
  }

  @Override
  public int getNeededHeight() {
    int northHeight = widgetMap.getOrDefault(NORTH, NULL_WIDGET).getNeededHeight();
    int southHeight = widgetMap.getOrDefault(SOUTH, NULL_WIDGET).getNeededHeight();

    int leftHeight = northHeight + widgetMap.getOrDefault(WEST, NULL_WIDGET).getNeededHeight() + southHeight;
    int middleHeight = northHeight + widgetMap.getOrDefault(CENTER, NULL_WIDGET).getNeededHeight() + southHeight;
    int rightHeight = northHeight + widgetMap.getOrDefault(EAST, NULL_WIDGET).getNeededHeight() + southHeight;
    return Math.max(leftHeight, Math.max(middleHeight, rightHeight));
  }
}
