package com.verzano.terminalrss.ui.widget.list;

import com.verzano.terminalrss.ui.TerminalUI;
import com.verzano.terminalrss.ui.widget.TerminalWidget;
import com.verzano.terminalrss.ui.widget.constants.Direction;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import static com.verzano.terminalrss.ui.widget.action.Key.DOWN_ARROW;
import static com.verzano.terminalrss.ui.widget.action.Key.UP_ARROW;
import static com.verzano.terminalrss.ui.widget.constants.Ansi.RESET;
import static com.verzano.terminalrss.ui.widget.constants.Ansi.REVERSE;

public class ListWidget<T> extends TerminalWidget {
  // TODO thread safe?
  @Getter @Setter
  private List<T> rows;

  @Getter @Setter
  private int selectedIndex = 0;
  // TODO update this when the cursor moves past the bottom of the screen
  // TODO also need to update it and stuff when stuff is resized...
  private int topRow = 0;

  public ListWidget(List<T> rows) {
    this.rows = rows;

    addEscapedKeyAction(UP_ARROW, () -> {
      scroll(Direction.UP, 1);
      reprint();
    });
    addEscapedKeyAction(DOWN_ARROW, () -> {
      scroll(Direction.DOWN, 1);
      reprint();
    });
  }

  @Override
  public void print() {
    for (int row = 0; row < getHeight(); row++) {
      TerminalUI.move(getX(), getY() + row);
      if (row + topRow >= rows.size()) {
        // TODO cut this off it it runs off-screen should prolly be done in the TerminalUI
        TerminalUI.printn(" ", getWidth());
      } else {
        // TODO need to pad or crop this
        String toPrint = rows.get(row + topRow).toString();
        if (toPrint.length() > getWidth()) {
          toPrint = toPrint.substring(0, getWidth());
        } else if(toPrint.length() < getWidth()) {
          toPrint = toPrint + new String(new char[getWidth() - toPrint.length()]).replace("\0", " ");
        }

        if (row == selectedIndex) {
          toPrint = REVERSE + toPrint + RESET;
        }

        TerminalUI.print(toPrint);
      }
    }
  }

  private void scroll(Direction dir, int distance) {
    switch (dir) {
      case UP:
        setSelectedIndex(Math.max(0, selectedIndex - distance));
        break;
      case DOWN:
        setSelectedIndex(Math.min(rows.size() - 1, selectedIndex + distance));
    }
  }

  public T getRow(int row) {
    // TODO this will throw if out of bounds
    return rows.get(row);
  }

  public T getSelectedRow() {
    return rows.get(selectedIndex);
  }
}
