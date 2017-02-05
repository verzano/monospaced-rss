package com.verzano.terminalrss.ui.widget;

import com.verzano.terminalrss.ui.TerminalUI;
import com.verzano.terminalrss.ui.metrics.Location;
import com.verzano.terminalrss.ui.metrics.Size;
import com.verzano.terminalrss.ui.task.key.KeyTask;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.verzano.terminalrss.ui.metrics.Size.MATCH_TERMINAL;

// TODO make this thread safe
public abstract class TerminalWidget {
  public static final TerminalWidget NULL_WIDGET = new TerminalWidget() {
    @Override
    public void print() { }

    @Override
    public void size() { }
  };

  public static final Comparator<TerminalWidget> Z_COMPARTOR = (tw1, tw2) -> {
    int comp = Integer.compare(tw1.location.getZ(), tw2.location.getZ());
    return comp == 0 ? Long.compare(tw1.hashCode(), tw2.hashCode()) : comp;
  };

  @Getter @Setter
  private Size size;

  @Getter @Setter
  private Location location;

  // TODO combine these somehow for simplicity's sake
  private final Map<Integer, Set<KeyTask>> keyActionsMap = new HashMap<>();
  private final Map<Integer, Set<KeyTask>> escapedKeyActionsMap = new HashMap<>();

  public abstract void print();
  public abstract void size();

  public TerminalWidget() {
    this(new Size(MATCH_TERMINAL, MATCH_TERMINAL), new Location(1, 1, 1));
  }

  public TerminalWidget(Size size, Location location) {
    this.size = size;
    this.location = location;
  }

  public int getWidth() {
    return size.getWidth() == MATCH_TERMINAL ? TerminalUI.getWidth() : size.getWidth();
  }

  public void setWidth(int width) {
    size.setWidth(width);
  }

  public int getHeight() {
    return size.getHeight() == MATCH_TERMINAL ? TerminalUI.getHeight() : size.getHeight();
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

  public int getZ() {
    return location.getZ();
  }

  public void setZ(int z) {
    location.setZ(z);
  }

  public void addKeyAction(int key, KeyTask action) {
    Set<KeyTask> keyTasks = keyActionsMap.getOrDefault(key, new HashSet<>());
    keyTasks.add(action);
    keyActionsMap.put(key, keyTasks);
  }

  public void fireKeyActions(int key) {
    keyActionsMap.getOrDefault(key, Collections.emptySet()).forEach(KeyTask::fire);
  }

  public void addEscapedKeyAction(int escapedKey, KeyTask action) {
    Set<KeyTask> keyTasks = escapedKeyActionsMap.getOrDefault(escapedKey, new HashSet<>());
    keyTasks.add(action);
    escapedKeyActionsMap.put(escapedKey, keyTasks);
  }

  public void fireEscapedKeyActions(int escapedKey) {
    escapedKeyActionsMap.getOrDefault(escapedKey, Collections.emptySet()).forEach(KeyTask::fire);
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
