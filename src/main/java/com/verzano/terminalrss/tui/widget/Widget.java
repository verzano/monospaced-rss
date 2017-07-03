package com.verzano.terminalrss.tui.widget;

import static com.verzano.terminalrss.tui.ansi.AnsiFormat.NORMAL;
import static com.verzano.terminalrss.tui.container.Container.NULL_CONTAINER;

import com.verzano.terminalrss.tui.TerminalUi;
import com.verzano.terminalrss.tui.ansi.AnsiFormat;
import com.verzano.terminalrss.tui.ansi.Attribute;
import com.verzano.terminalrss.tui.ansi.Background;
import com.verzano.terminalrss.tui.ansi.Foreground;
import com.verzano.terminalrss.tui.container.Container;
import com.verzano.terminalrss.tui.metrics.Padding;
import com.verzano.terminalrss.tui.task.key.KeyTask;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// TODO make this thread safe
// TODO padding will drastically effect how something is printed...
// TODO add borders eventually...
// TODO a child widget should also mark its container as focused...
@NoArgsConstructor
public abstract class Widget {

  public static final Widget NULL_WIDGET = new Widget() {
    @Override
    public int getNeededWidth() {
      return 0;
    }

    @Override
    public int getNeededHeight() {
      return 0;
    }

    @Override
    public void printContent() {
    }
  };
  private final Map<String, Set<KeyTask>> keyActionsMap = new HashMap<>();
  @Getter
  @Setter
  private Padding padding = new Padding(0, 0, 0, 0);
  @Getter
  @Setter
  private Container container = NULL_CONTAINER;
  @Getter
  @Setter
  private AnsiFormat focusedFormat = new AnsiFormat(
      Background.NONE,
      Foreground.NONE,
      Attribute.NONE);
  @Getter
  @Setter
  private AnsiFormat unfocusedFormat = new AnsiFormat(
      Background.NONE,
      Foreground.NONE,
      Attribute.NONE);
  private String emptyRow;
  private String emptyContentRow;

  public abstract int getNeededWidth();

  public abstract int getNeededHeight();

  public abstract void printContent();

  public int getWidth() {
    return container.getWidgetWidth(this);
  }

  public int getContentWidth() {
    return getWidth() - padding.getLeft() - padding.getRight();
  }

  public int getHeight() {
    return container.getWidgetHeight(this);
  }

  public int getContentHeight() {
    return getHeight() - padding.getTop() - padding.getBottom();
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

  public void addKeyAction(String key, KeyTask action) {
    Set<KeyTask> keyTasks = keyActionsMap.getOrDefault(key, new HashSet<>());
    keyTasks.add(action);
    keyActionsMap.put(key, keyTasks);
  }

  public void clearKeyActions(String key) {
    keyActionsMap.remove(key);
  }

  public void fireKeyActions(String key) {
    keyActionsMap.getOrDefault(key, Collections.emptySet()).forEach(KeyTask::fire);
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

    printContent();
  }

  public void reprint() {
    TerminalUi.schedulePrintTask(this::print);
  }
}
