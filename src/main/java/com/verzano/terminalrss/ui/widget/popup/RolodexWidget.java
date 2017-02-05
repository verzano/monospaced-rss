package com.verzano.terminalrss.ui.widget.popup;

import com.verzano.terminalrss.ui.TerminalUI;
import com.verzano.terminalrss.ui.metrics.Location;
import com.verzano.terminalrss.ui.metrics.Size;
import com.verzano.terminalrss.ui.widget.TerminalWidget;

import java.util.List;

import static com.verzano.terminalrss.ui.widget.constants.Ansi.RESET;
import static com.verzano.terminalrss.ui.widget.constants.Ansi.REVERSE;
import static com.verzano.terminalrss.ui.widget.constants.Key.DOWN_ARROW;
import static com.verzano.terminalrss.ui.widget.constants.Key.UP_ARROW;

public class RolodexWidget<T> extends TerminalWidget {
  private List<T> items;
  private int selectedItem = 0;

  private String emptyBar = REVERSE + new String(new char[getWidth()]).replace('\0', ' ') + RESET;

  private static final String EMPTY_COL = REVERSE + " " + RESET;

  public RolodexWidget(List<T> items, Size size, Location location) {
    super(size, location);
    this.items = items;

    addEscapedKeyAction(UP_ARROW, () -> {
      selectedItem = getPreviousIndex();
      reprint();
    });

    addEscapedKeyAction(DOWN_ARROW, () -> {
      selectedItem = getNextIndex();
      reprint();
    });
  }

  private int getPreviousIndex() {
    return selectedItem == 0 ? items.size() : selectedItem--;
  }

  private int getNextIndex() {
    return selectedItem == items.size() - 1 ? 0 : selectedItem++;
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
      TerminalUI.print(cropOrPad(items.get(getPreviousIndex()).toString()));

      TerminalUI.move(getX(), getY() + 1);
      TerminalUI.print(EMPTY_COL + REVERSE + cropOrPad(items.get(selectedItem).toString()) + RESET + EMPTY_COL);

      TerminalUI.move(getX(), getY() + 2);
      TerminalUI.print(cropOrPad(items.get(getNextIndex()).toString()));
    } else {
      TerminalUI.move(getX(), getY());
      TerminalUI.print(emptyBar);

      TerminalUI.move(getX(), getY() + 1);
      TerminalUI.print(EMPTY_COL + cropOrPad(items.get(selectedItem).toString()) + EMPTY_COL);

      TerminalUI.move(getX(), getY() + 2);
      TerminalUI.print(emptyBar);
    }
  }

  @Override
  public void size() {

  }
}
