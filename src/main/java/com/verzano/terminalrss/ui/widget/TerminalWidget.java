package com.verzano.terminalrss.ui.widget;

import com.verzano.terminalrss.ui.TerminalUI;
import com.verzano.terminalrss.ui.task.key.KeyTask;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// TODO migrate to terminal-printer
// TODO make this thread safe
// TODO maybe preload the keyActionsMap with all of the keys? might be overkill...
// TODO autofocus on a widget when clicked
public abstract class TerminalWidget {
  public static final TerminalWidget NULL_WIDGET = new TerminalWidget() {
    @Override
    public void print() { }

    @Override
    public void size() { }
  };

  public static final Comparator<TerminalWidget> Z_COMPARTOR = (tw1, tw2) -> {
    int comp = Integer.compare(tw1.z, tw2.z);
    return comp == 0 ? Long.compare(tw1.hashCode(), tw2.hashCode()) : comp;
  };

  // TODO migrate this to metrics.Size
  public static final int MATCH_TERMINAL = -1;

  // TODO use metrics.Size from terminal-printer
  @Setter
  private int width;
  @Setter
  private int height;

  // TODO use metrics.Point from terminal-printer
  // TODO maybe make this a 3D point??? so z is included
  @Getter @Setter
  private int x;
  @Getter @Setter
  private int y;
  @Getter @Setter
  private int z;

  // TODO combine these somehow for simplicity's sake
  private final Map<Integer, Set<KeyTask>> keyActionsMap = new HashMap<>();
  private final Map<Integer, Set<KeyTask>> escapedKeyActionsMap = new HashMap<>();

  public abstract void print();
  public abstract void size();

  public TerminalWidget() {
    this(-1, -1, 1, 1);
  }

  public TerminalWidget(int width, int height, int x, int y) {
    this.width = width;
    this.height = height;
    this.x = x;
    this.y = y;
  }

  public int getWidth() {
    return width == MATCH_TERMINAL ? TerminalUI.getWidth() : width;
  }

  public int getHeight() {
    return height == MATCH_TERMINAL ? TerminalUI.getHeight() : height;
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
