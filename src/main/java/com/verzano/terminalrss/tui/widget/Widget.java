package com.verzano.terminalrss.tui.widget;

import static com.verzano.terminalrss.tui.ansi.AnsiFormat.NORMAL;
import static com.verzano.terminalrss.tui.container.Container.NULL_CONTAINER;

import com.verzano.terminalrss.tui.TerminalUi;
import com.verzano.terminalrss.tui.ansi.AnsiFormat;
import com.verzano.terminalrss.tui.ansi.Attribute;
import com.verzano.terminalrss.tui.ansi.Background;
import com.verzano.terminalrss.tui.ansi.Foreground;
import com.verzano.terminalrss.tui.constant.Orientation;
import com.verzano.terminalrss.tui.constant.Position;
import com.verzano.terminalrss.tui.container.Container;
import com.verzano.terminalrss.tui.metric.Spacing;
import com.verzano.terminalrss.tui.task.Task;
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
    public int getNeededContentWidth() {
      return 0;
    }

    @Override
    public int getNeededContentHeight() {
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
  private AnsiFormat labelFormat = new AnsiFormat(Background.DEFAULT, Foreground.DEFAULT);

  @Getter
  private long altitude;
  @Getter
  @Setter
  private Spacing padding = new Spacing();
  @Getter
  @Setter
  private Container container = NULL_CONTAINER;
  @Getter
  @Setter
  private AnsiFormat focusedFormat = new AnsiFormat(Background.NONE, Foreground.NONE, Attribute.NONE);
  @Getter
  @Setter
  private AnsiFormat unfocusedFormat = new AnsiFormat(Background.NONE, Foreground.NONE, Attribute.NONE);
  private String emptyRow;
  private String emptyContentRow;

  public abstract int getNeededContentWidth();

  public abstract int getNeededContentHeight();

  public abstract void printContent();

  public int getWidth() {
    return container.getWidgetWidth(this);
  }

  public int getNeededWidth() {
    int neededWidth = getNeededContentWidth();
    if (showLabel) {
      switch (labelPosition) {
        case TOP:
        case BOTTOM:
          neededWidth = Math.max(getLabelWidth(), neededWidth);
          break;
        case LEFT:
        case RIGHT:
          neededWidth += getLabelWidth();
          break;
      }
    }
    return neededWidth;
  }

  public int getNeededHeight() {
    int neededHeight = getNeededContentHeight();
    if (showLabel) {
      switch (labelPosition) {
        case TOP:
        case BOTTOM:
          neededHeight += getLabelHeight();
          break;
        case LEFT:
        case RIGHT:
          neededHeight = Math.max(getNeededHeight(), neededHeight);
          break;
      }
    }
    return neededHeight;
  }

  public int getContentWidth() {
    return Math.max(getWidth() - padding.getLeft() - padding.getRight(), 0);
  }

  public int getHeight() {
    return container.getWidgetHeight(this);
  }

  public int getContentHeight() {
    return Math.max(getHeight() - padding.getTop() - padding.getBottom(), 0);
  }

  public int getX() {
    return container.getWidgetX(this);
  }

  public int getContentX() {
    return getX() + padding.getLeft();
  }

  public int getY() {
    return container.getWidgetY(this);
  }

  public int getContentY() {
    return getY() + padding.getTop();
  }

  public void setAltitude(long altitude) {
    if (altitude < 0) {
      throw new IllegalArgumentException(
          "A widget cannot have a negative altitude."
              + "  Supplied altitude is " + altitude);
    } else if (altitude <= container.getAltitude()) {
      throw new IllegalArgumentException(
          "A widget's altitude must be greater than it's container's."
              + "  Supplied altitude is " + altitude
              + " container's altitude is " + container.getAltitude());
    }

    this.altitude = altitude;
  }

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

  public boolean isFocused() {
    return TerminalUi.getFocusedWidget() == this;
  }

  public void setFocused() {
    TerminalUi.setFocusedWidget(this);
  }

  public void size() {
    emptyRow = new String(new char[getWidth()]).replace('\0', ' ');
    emptyContentRow = new String(new char[getContentWidth()]).replace('\0', ' ');
  }

  public String getAnsiFormatPrefix() {
    return isFocused() ? focusedFormat.getFormatString() : unfocusedFormat.getFormatString();
  }

  private String getEmptyRow() {
    return getAnsiFormatPrefix() + emptyRow + NORMAL.getFormatString();
  }

  public String getEmptyContentRow() {
    return getAnsiFormatPrefix() + emptyContentRow + NORMAL.getFormatString();
  }

  public final void print() {
    for (int row = 0; row < getHeight(); row++) {
      TerminalUi.move(getX(), getY() + row);
      TerminalUi.print(getEmptyRow());
    }

    // TODO this chunk is basically identical to what is in TextWidget
    if (showLabel) {
      switch (labelOrientation) {
        case VERTICAL:
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

  public void reprint() {
    TerminalUi.schedulePrintTask(this::print);
  }

  // TODO this chunk is basically identical to what is in TextWidget
  private void printLabelHorizontal() {
    switch (labelPosition) {
      case TOP:
        TerminalUi.move(getX(), getY());
        TerminalUi.print(getRowForLabel());
        for (int i = 1; i < getContentHeight(); i++) {
          TerminalUi.move(getX(), getY() + i);
          TerminalUi.print(getEmptyContentRow());
        }
        break;
//      case LEFT:
//        break;
//      case RIGHT:
//        int middleRow = getHeight() / 2;
//        for (int i = 0; i < getHeight(); i++) {
//          TerminalUi.move(getContentX(), getContentY() + i);
//          if (i == middleRow) {
//            TerminalUi.print(getRowForLabel());
//          } else {
//            TerminalUi.print(getEmptyContentRow());
//          }
//        }
//        break;
//      case BOTTOM:
//        TerminalUi.move(getContentX(), getContentY());
//        for (int i = 1; i < getContentHeight(); i++) {
//          TerminalUi.print(getEmptyContentRow());
//          TerminalUi.move(getContentX(), getContentY() + i);
//        }
//        TerminalUi.print(getRowForLabel());
//        break;
//      default:
//        break;
    }
  }

  // TODO this chunk is basically identical to what is in TextWidget
  private String getRowForLabel() {
    String ret = label;
    if (label.length() != getWidth()) {
      switch (labelPosition) {
//        case LEFT:
//          if (text.length() > getWidth()) {
//            text = text.substring(0, getWidth());
//          } else {
//            text += new String(new char[getWidth() - text.length()]).replace('\0', ' ');
//          }
//          break;
        case TOP:
//        case BOTTOM:
          if (label.length() > getWidth()) {
            double halfExtra = (label.length() - getWidth()) / 2D;

            ret = label.substring((int) halfExtra, label.length() - (int) Math.ceil(halfExtra));
          } else {
            double halfRemaining = (getWidth() - label.length()) / 2D;
            ret = new String(new char[(int) Math.ceil(halfRemaining)]).replace('\0', ' ')
                + label
                + new String(new char[(int) halfRemaining]).replace('\0', ' ');
          }
          break;
//        case RIGHT:
//          if (text.length() > getWidth()) {
//            text = text.substring(text.length() - getWidth(), text.length());
//          } else {
//            text = new String(new char[getWidth() - text.length()]).replace('\0', ' ') + text;
//          }
//          break;
//        default:
//          break;
      }
    }

    return labelFormat.getFormatString() + ret + AnsiFormat.NORMAL.getFormatString();
  }

  private int getLabelWidth() {
    switch (labelOrientation) {
      case HORIZONTAL:
        return label.length();
      case VERTICAL:
        return 1;
      default:
        return 0;
    }
  }

  private int getLabelHeight() {
    switch (labelOrientation) {
      case HORIZONTAL:
        return 1;
      case VERTICAL:
        return label.length();
      default:
        return 0;
    }
  }
}
