package com.verzano.terminalrss.ui.widget;

import com.verzano.terminalrss.ui.TerminalUI;
import com.verzano.terminalrss.ui.ansi.AnsiFormat;
import com.verzano.terminalrss.ui.ansi.Attribute;
import com.verzano.terminalrss.ui.ansi.Background;
import com.verzano.terminalrss.ui.ansi.Foreground;
import com.verzano.terminalrss.ui.container.Container;
import com.verzano.terminalrss.ui.metrics.Padding;
import com.verzano.terminalrss.ui.task.key.KeyTask;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.verzano.terminalrss.ui.ansi.AnsiFormat.NORMAL;
import static com.verzano.terminalrss.ui.container.Container.NULL_CONTAINER;

// TODO make this thread safe
// TODO padding will drastically effect how something is printed...
// TODO add borders eventually...
// TODO a child widget should also mark its container as focused...
// TODO really need to consider sizing and when to do it...
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
    public void printContent() { }
  };

  @Getter @Setter
  private Padding padding = new Padding(0, 0, 0, 0);

  @Getter @Setter
  private Container container = NULL_CONTAINER;

  @Getter @Setter
  private AnsiFormat focusedFormat = new AnsiFormat(Background.NONE, Foreground.NONE, Attribute.NONE);

  @Getter @Setter
  private AnsiFormat unfocusedFormat = new AnsiFormat(Background.NONE, Foreground.NONE, Attribute.NONE);

  private final Map<String, Set<KeyTask>> keyActionsMap = new HashMap<>();

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

  public void fireKeyActions(String key) {
    keyActionsMap.getOrDefault(key, Collections.emptySet()).forEach(KeyTask::fire);
  }

  public boolean isFocused() {
    return TerminalUI.getFocusedWidget() == this;
  }

  public void setFocused() {
    TerminalUI.setFocusedWidget(this);
  }

  public void size() {
    emptyRow = new String(new char[getWidth()]).replace('\0', ' ');
    emptyContentRow = new String(new char[getContentWidth()]).replace('\0', ' ');
  }

  public String getAnsiFormatPrefix() {
    String prefix;
    if (isFocused()) {
      prefix = focusedFormat.getFormatString();
    } else {
      prefix = unfocusedFormat.getFormatString();
    }

    return prefix;
  }

  private String getEmptyRow() {
    return getAnsiFormatPrefix() + emptyRow + NORMAL.getFormatString();
  }

  public String getEmptyContentRow() {
    return getAnsiFormatPrefix() + emptyContentRow + NORMAL.getFormatString();
  }

  public final void print() {
    for (int row = 0; row < getHeight(); row++) {
      TerminalUI.move(getX(), getY() + row);
      TerminalUI.print(getEmptyRow());
    }

    printContent();
  }

  public void reprint() {
    TerminalUI.schedulePrintTask(this::print);
  }
}
