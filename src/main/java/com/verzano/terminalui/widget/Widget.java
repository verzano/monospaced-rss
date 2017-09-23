package com.verzano.terminalui.widget;

import com.verzano.terminalui.TerminalUi;
import com.verzano.terminalui.ansi.AnsiFormat;
import com.verzano.terminalui.ansi.Attribute;
import com.verzano.terminalui.ansi.Background;
import com.verzano.terminalui.ansi.Foreground;
import com.verzano.terminalui.constant.Orientation;
import com.verzano.terminalui.constant.Position;
import com.verzano.terminalui.container.Container;
import com.verzano.terminalui.metric.Spacing;
import com.verzano.terminalui.task.Task;
import com.verzano.terminalui.util.PrintUtils;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// TODO make this thread safe
// padding is the space between the content and the border/label
// TODO add label
// TODO add border
// TODO add margin (space between border/label and edge of widget)
// TODO a child widget should also mark its container as focused...
@NoArgsConstructor
public abstract class Widget {
  public static final Widget NULL_WIDGET = new Widget() {
    @Override
    public int getNeededContentHeight() {
      return 0;
    }

    @Override
    public int getNeededContentWidth() {
      return 0;
    }

    @Override
    public void printContent() {}
  };
  private final Map<String, Set<Task>> keyActionsMap = new HashMap<>();
  @Getter
  @Setter
  private String label = "";
  @Getter
  @Setter
  private boolean showLabel = false;
  @Getter
  @Setter
  private Position labelPosition = Position.LEFT;
  @Getter
  @Setter
  private Orientation labelOrientation = Orientation.HORIZONTAL;
  @Getter
  @Setter
  private AnsiFormat labelFormat = new AnsiFormat(Background.NONE, Foreground.NONE);
  @Getter
  @Setter
  private Spacing padding = new Spacing();
  @Getter
  @Setter
  private Container container = Container.NULL_CONTAINER;
  @Getter
  @Setter
  private AnsiFormat focusedFormat = new AnsiFormat(Background.NONE, Foreground.NONE, Attribute.NONE);
  @Getter
  @Setter
  private AnsiFormat unfocusedFormat = new AnsiFormat(Background.NONE, Foreground.NONE, Attribute.NONE);
  private String emptyRow;
  private String emptyContentRow;

  public abstract int getNeededContentHeight();
  public abstract int getNeededContentWidth();
  public abstract void printContent();

  public void addKeyAction(String key, Task action) {
    Set<Task> tasks = keyActionsMap.getOrDefault(key, new HashSet<>());
    tasks.add(action);
    keyActionsMap.put(key, tasks);
  }

  public void clearKeyActions(String key) {
    keyActionsMap.remove(key);
  }

  public void fireKeyActions(String key) {
    keyActionsMap.getOrDefault(key, Collections.emptySet()).forEach(Task::fire);
  }

  public String getAnsiFormatPrefix() {
    return isFocused() ? focusedFormat.getFormatString() : unfocusedFormat.getFormatString();
  }

  public int getContentHeight() {
    int contentHeight = getHeight() - padding.getTop() - padding.getBottom();
    if(showLabel) {
      switch(labelPosition) {
        case TOP:
        case TOP_LEFT:
        case TOP_RIGHT:
        case BOTTOM:
        case BOTTOM_LEFT:
        case BOTTOM_RIGHT:
          contentHeight -= getLabelHeight();
      }
    }
    return Math.max(contentHeight, 0);
  }

  public int getContentWidth() {
    int contentWidth = getWidth() - padding.getLeft() - padding.getRight();
    if(showLabel) {
      switch(labelPosition) {
        case LEFT:
        case RIGHT:
          contentWidth -= getLabelWidth();
      }
    }
    return Math.max(contentWidth, 0);
  }

  public int getContentX() {
    int contentX = getX() + padding.getLeft();
    if(showLabel) {
      switch(labelPosition) {
        case LEFT:
          contentX += getLabelWidth();
      }
    }
    return contentX;
  }

  public int getContentY() {
    int contentY = getY() + padding.getTop();
    if(showLabel) {
      switch(labelPosition) {
        case TOP:
        case TOP_LEFT:
        case TOP_RIGHT:
          contentY += getLabelHeight();
      }
    }
    return contentY;
  }

  public String getEmptyContentRow() {
    return getAnsiFormatPrefix() + emptyContentRow + AnsiFormat.NORMAL.getFormatString();
  }

  private String getEmptyRow() {
    return getAnsiFormatPrefix() + emptyRow + AnsiFormat.NORMAL.getFormatString();
  }

  public int getHeight() {
    return container.getWidgetHeight(this);
  }

  private int getLabelHeight() {
    switch(labelOrientation) {
      case HORIZONTAL:
        return 1;
      case VERTICAL:
        return label.length();
      default:
        return 0;
    }
  }

  private int getLabelWidth() {
    switch(labelOrientation) {
      case HORIZONTAL:
        return label.length();
      case VERTICAL:
        return 1;
      default:
        return 0;
    }
  }

  public int getNeededHeight() {
    int neededHeight = getNeededContentHeight();
    if(showLabel) {
      switch(labelPosition) {
        case TOP:
        case TOP_LEFT:
        case TOP_RIGHT:
        case BOTTOM:
        case BOTTOM_LEFT:
        case BOTTOM_RIGHT:
          neededHeight += getLabelHeight();
          break;
        case LEFT:
        case RIGHT:
          neededHeight = Math.max(getLabelHeight(), neededHeight);
          break;
      }
    }
    return neededHeight;
  }

  public int getNeededWidth() {
    int neededWidth = getNeededContentWidth();
    if(showLabel) {
      switch(labelPosition) {
        case TOP:
        case BOTTOM:
          neededWidth = Math.max(getLabelWidth(), neededWidth);
          break;
        case LEFT:
        case TOP_LEFT:
        case BOTTOM_LEFT:
        case RIGHT:
        case TOP_RIGHT:
        case BOTTOM_RIGHT:
          neededWidth += getLabelWidth();
          break;
      }
    }
    return neededWidth;
  }

  public int getWidth() {
    return container.getWidgetWidth(this);
  }

  public int getX() {
    return container.getWidgetX(this);
  }

  public int getY() {
    return container.getWidgetY(this);
  }

  public boolean isFocused() {
    return TerminalUi.getFocusedWidget() == this;
  }

  public final void print() {
    for(int row = 0; row < getHeight(); row++) {
      TerminalUi.move(getX(), getY() + row);
      TerminalUi.print(getEmptyRow());
    }

    printGuts();
  }

  // TODO this method is a hack for the flaoter, need to figure out why it won't print the correct background color
  public final void printGuts() {
    if(showLabel) {
      switch(labelOrientation) {
        case VERTICAL:
//          printLabelVertical();
          break;
        case HORIZONTAL:
          printLabelHorizontal();
          break;
        default:
          break;
      }
    }

    printContent();
  }

  private void printLabelHorizontal() {
    switch(labelPosition) {
      case TOP:
      case TOP_LEFT:
      case TOP_RIGHT:
        TerminalUi.move(getX(), getY());
        break;
      case BOTTOM:
      case BOTTOM_LEFT:
      case BOTTOM_RIGHT:
        TerminalUi.move(getX(), getY() + getHeight());
        break;
      case LEFT:
      case RIGHT:
        TerminalUi.move(getX(), getY()/2);
        break;
    }

    TerminalUi.print(PrintUtils.getRowForText(label, labelPosition, labelFormat.getFormatString(), getWidth()));
  }

  public void reprint() {
    TerminalUi.schedulePrintTask(this::print);
  }

  public void setFocused() {
    TerminalUi.setFocusedWidget(this);
  }

  public void size() {
    emptyRow = new String(new char[getWidth()]).replace('\0', ' ');
    emptyContentRow = new String(new char[getContentWidth()]).replace('\0', ' ');
  }
}
