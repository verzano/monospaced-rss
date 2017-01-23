package com.verzano.terminalrss.ui.widget;

import com.verzano.terminalrss.ui.TerminalUI;
import com.verzano.terminalrss.ui.widget.action.KeyAction;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// TODO make this nice and
// TODO migrate to terminal-printer
// TODO make this thread safe
// TODO maybe preload the keyActionsMap with all of the keys? might be overkill...
// TODO autofocus on a widget when clicked
public abstract class TerminalWidget {
  public static final TerminalWidget NULL_WIDGET = new TerminalWidget(0, 0, 0, 0) {
    @Override
    public void print() { }
  };

  public static final Comparator<TerminalWidget> Z_COMPARTOR = (tw1, tw2) -> {
    int comp = Integer.compare(tw1.z, tw2.z);
    return comp == 0 ? Long.compare(tw1.hashCode(), tw2.hashCode()) : comp;
  };

  // TODO use metrics.Size from terminal-printer
  @Getter @Setter
  private int width;
  @Getter @Setter
  private int height;

  // TODO use metrics.Point from terminal-printer
  // TODO maybe make this a 3D point??? so z is included
  // Position withing terminal, can be offscreen....
  @Getter @Setter
  private int x;
  @Getter @Setter
  private int y;
  @Getter @Setter
  private int z;

  // TODO combine these somehow for simplicity's sake
  private final Map<Integer, Set<KeyAction>> keyActionsMap = new HashMap<>();
  private final Map<Integer, Set<KeyAction>> escapedKeyActionsMap = new HashMap<>();

  public abstract void print();

  public TerminalWidget() {
    this(TerminalUI.getWidth(), TerminalUI.getHeight(), 1, 1);
  }

  public TerminalWidget(int width, int height, int x, int y) {
    this.width = width;
    this.height = height;
    this.x = x;
    this.y = y;
  }

  public void addKeyAction(int key, KeyAction action) {
    Set<KeyAction> keyActions = keyActionsMap.getOrDefault(key, new HashSet<>());
    keyActions.add(action);
    keyActionsMap.put(key, keyActions);
  }

  public void fireKeyActions(int key) {
    keyActionsMap.getOrDefault(key, Collections.emptySet()).forEach(KeyAction::fire);
  }

  public void addEscapedKeyAction(int escapedKey, KeyAction action) {
    Set<KeyAction> keyActions = escapedKeyActionsMap.getOrDefault(escapedKey, new HashSet<>());
    keyActions.add(action);
    escapedKeyActionsMap.put(escapedKey, keyActions);
  }

  public void fireEscapedKeyActions(int escapedKey) {
    escapedKeyActionsMap.getOrDefault(escapedKey, Collections.emptySet()).forEach(KeyAction::fire);
  }

  public void setFocused() {
    TerminalUI.setFocusedWidget(this);
  }
}
