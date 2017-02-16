package com.verzano.terminalrss.ui.widget;

import com.verzano.terminalrss.ui.TerminalUI;
import com.verzano.terminalrss.ui.metrics.Location;
import com.verzano.terminalrss.ui.metrics.Padding;
import com.verzano.terminalrss.ui.metrics.Size;
import com.verzano.terminalrss.ui.task.key.KeyTask;
import com.verzano.terminalrss.ui.widget.ansi.AnsiTextFormat;
import com.verzano.terminalrss.ui.widget.ansi.Attribute;
import com.verzano.terminalrss.ui.widget.ansi.Background;
import com.verzano.terminalrss.ui.widget.ansi.Foreground;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.verzano.terminalrss.ui.metrics.Size.FILL_PARENT;

// TODO make this thread safe
// TODO padding will drastically effect how something is printed...
// TODO add borders eventually...
// TODO a child widget should also mark its parent as focused...
// TODO really need to consider sizing and when to do it...
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
  };

  @Getter @Setter
  private Size size;

  @Getter @Setter
  private Location location = new Location(1, 1);

  @Getter @Setter
  private Padding padding = new Padding(0, 0, 0, 0);

  @Getter @Setter
  private Widget parent = NULL_WIDGET;

  @Getter @Setter
  private Attribute[] focusedAttributes = new Attribute[]{Attribute.NONE};

  @Getter @Setter
  private Attribute[] unfocusedAttributes = new Attribute[]{Attribute.NONE};

  @Getter @Setter
  private Foreground focusedForeground = Foreground.NONE;

  @Getter @Setter
  private Foreground unfocusedForeground = Foreground.NONE;

  @Getter @Setter
  private Background focusedBackground = Background.NONE;

  @Getter @Setter
  private Background unfocusedBackground = Background.NONE;

  private final Map<String, Set<KeyTask>> keyActionsMap = new HashMap<>();

  private String emptyRow;

  // TODO no, this is wrong
  public static String NORMAL = AnsiTextFormat.build(Attribute.NORMAL);

  // TODO maybe add getParentXXX()???
  public abstract int getNeededWidth();
  public abstract int getNeededHeight();

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
        Widget ancestor = parent;
        boolean cont = true;
        while (cont) {
          // TODO this part defends against a poorly formed widget tree, but should it?
          if (ancestor == NULL_WIDGET || ancestor == null) {
            cont = false;
            width = TerminalUI.getWidth();
          } else if (ancestor.getSize().getWidth() >= 0) {
            cont = false;
            width = ancestor.getWidth();
          }
          ancestor = parent.getParent();
        }
        break;
      case Size.FILL_NEEDED:
        width = getNeededWidth();
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
        Widget ancestor = parent;
        boolean cont = true;
        while (cont) {
          // TODO this part defends against a poorly formed widget tree, but should it?
          if (ancestor == NULL_WIDGET || ancestor == null) {
            cont = false;
            height = TerminalUI.getHeight();
          } else if (ancestor.getSize().getHeight() >= 0) {
            cont = false;
            height = ancestor.getHeight();
          }
          ancestor = parent.getParent();
        }
        break;
      case Size.FILL_NEEDED:
        height = getNeededHeight();
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

  public void setFocusedAttribute(Attribute attribute) {
    setFocusedAttributes(new Attribute[]{attribute});
  }

  public void setUnfocusedAttribute(Attribute attribute) {
    setUnfocusedAttributes(new Attribute[]{attribute});
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

  public String getTextFormattingPrefix() {
    String prefix;
    if (isFocused()) {
      prefix = AnsiTextFormat.build(getFocusedForeground(), getFocusedBackground(), getFocusedAttributes());
    } else {
      prefix = AnsiTextFormat.build(getUnfocusedForeground(), getUnfocusedBackground(), getUnfocusedAttributes());
    }

    return prefix;
  }

  public String getEmptyRow() {
    return getTextFormattingPrefix() + emptyRow + NORMAL;
  }

  public void print() {
    for (int row = 0; row < getHeight(); row++) {
      TerminalUI.move(getX(), getY() + row);
      TerminalUI.print(getEmptyRow());
    }
  }

  public void reprint() {
    TerminalUI.schedulePrintTask(this::print);
  }

  public void size() {
    emptyRow = new String(new char[getWidth()]).replace('\0', ' ');
  }
}
