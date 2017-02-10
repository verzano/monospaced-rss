package com.verzano.terminalrss.ui.widget;

import com.verzano.terminalrss.ui.TerminalUI;
import com.verzano.terminalrss.ui.metrics.Location;
import com.verzano.terminalrss.ui.metrics.Size;
import com.verzano.terminalrss.ui.task.key.KeyTask;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.verzano.terminalrss.ui.metrics.Size.FILL_PARENT;

// TODO make this thread safe
// TODO add a 'parent' widget
// TODO rework the 'resize' thing thang
// TODO add padding around components
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
    public void print() { }

    @Override
    public void size() { }
  };

  @Getter @Setter
  private Size size;

  @Getter @Setter
  private Location location = new Location(1, 1);
  
  @Getter @Setter
  private Widget parent = NULL_WIDGET;

  private final Map<String, Set<KeyTask>> keyActionsMap = new HashMap<>();

  public abstract int getNeededWidth();
  public abstract int getNeededHeight();
  public abstract void print();
  public abstract void size();

  public Widget() {
    this(new Size(FILL_PARENT, FILL_PARENT));
  }

  public Widget(Size size) {
    this.size = size;
  }

  public int getWidth() {
    int width = size.getWidth();
    switch (width) {
      case Size.FILL_PARENT:
        if (parent == NULL_WIDGET) {
          width = TerminalUI.getWidth();
        } else {
          width = parent.getWidth();
        }
        break;
      case Size.FILL_NEEDED:
        width = getNeededWidth();
        break;
      case Size.FILL_REMAINING:
        // TODO figure this out...
        break;
    }
    return width;
  }

  public void setWidth(int width) {
    size.setWidth(width);
  }

  public int getHeight() {
    int height = size.getHeight();
    switch (height) {
      case Size.FILL_PARENT:
        if (parent == NULL_WIDGET) {
          height = TerminalUI.getHeight();
        } else {
          height = parent.getHeight();
        }
        break;
      case Size.FILL_NEEDED:
        height = getNeededHeight();
        break;
      case Size.FILL_REMAINING:
        // TODO figure this out...
        break;
    }
    return height;
  }

  public void setHeight(int height) {
    size.setHeight(height);
  }

  public int getX() {
    return location.getX();
  }

  public void setX(int x) {
    location.setX(x);
  }

  public int getY() {
    return location.getY();
  }

  public void setY(int y) {
    location.setY(y);
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

  public void reprint() {
    TerminalUI.schedulePrintTask(this::print);
  }

  public void resize() {
    TerminalUI.schedulePrintTask(this::size);
  }
}
