package com.verzano.terminalrss.ui.widget.popup;

import com.verzano.terminalrss.ui.TerminalUI;
import com.verzano.terminalrss.ui.metrics.Location;
import com.verzano.terminalrss.ui.metrics.Size;
import com.verzano.terminalrss.ui.widget.TerminalWidget;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import static com.verzano.terminalrss.ui.widget.constants.Ansi.RESET;
import static com.verzano.terminalrss.ui.widget.constants.Ansi.REVERSE;
import static com.verzano.terminalrss.ui.widget.constants.Key.DOWN_ARROW;
import static com.verzano.terminalrss.ui.widget.constants.Key.UP_ARROW;

// TODO both this and the list widget should be backed by some kind of list model
public class RolodexWidget<T> extends TerminalWidget {
  private List<T> items;

  @Getter @Setter
  private volatile int selectedIndex = 0;

  private String emptyBar = REVERSE + new String(new char[getWidth()]).replace('\0', ' ') + RESET;

  private static final String EMPTY_COL = REVERSE + " " + RESET;

  public RolodexWidget(List<T> items, Size size, Location location) {
    super(size, location);
    this.items = items;

    addEscapedKeyAction(UP_ARROW, () -> {
      selectedIndex = getPreviousIndex();
      reprint();
    });

    addEscapedKeyAction(DOWN_ARROW, () -> {
      selectedIndex = getNextIndex();
      reprint();
    });
  }

  public T getSelectedItem() {
    return items.get(selectedIndex);
  }

  private int getPreviousIndex() {
    return selectedIndex == 0 ? items.size() - 1 : selectedIndex - 1;
  }

  private int getNextIndex() {
    return selectedIndex == items.size() - 1 ? 0 : selectedIndex + 1;
  }

  private String cropOrPad(String s) {
    int maxWidth = getWidth() - 2;

    if (s.length() < maxWidth) {
      s += new String(new char[maxWidth - s.length()]).replace('\0', ' ');
    } else if (s.length() > maxWidth) {
      s = s.substring(0, maxWidth);
    }

    return s;
  }

  @Override
  public void print() {
    if (isFocused()) {
      TerminalUI.move(getX(), getY());
      TerminalUI.print(' ' + cropOrPad(items.get(getPreviousIndex()).toString()) + ' ');

      TerminalUI.move(getX(), getY() + 1);
      TerminalUI.print(EMPTY_COL + REVERSE + cropOrPad(items.get(selectedIndex).toString()) + RESET + EMPTY_COL);

      TerminalUI.move(getX(), getY() + 2);
      TerminalUI.print(' ' + cropOrPad(items.get(getNextIndex()).toString()) + ' ');
    } else {
      TerminalUI.move(getX(), getY());
      TerminalUI.print(emptyBar);

      TerminalUI.move(getX(), getY() + 1);
      TerminalUI.print(EMPTY_COL + cropOrPad(items.get(selectedIndex).toString()) + EMPTY_COL);

      TerminalUI.move(getX(), getY() + 2);
      TerminalUI.print(emptyBar);
    }
  }

  @Override
  public void size() {

  }
}
