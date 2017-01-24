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
  @Getter
  private List<T> rows;

  @Getter @Setter
  private int selectedLine;

  private int topLine;

  public ListWidget(List<T> rows) {
    setRows(rows);

    addEscapedKeyAction(UP_ARROW, () -> {
      scroll(Direction.UP, 1);
      reprint();
    });
    addEscapedKeyAction(DOWN_ARROW, () -> {
      scroll(Direction.DOWN, 1);
      reprint();
    });
  }

  public void setRows(List<T> rows) {
    this.rows = rows;
    selectedLine = 0;
    topLine = 0;
  }

  public T getRow(int row) {
    // TODO this will throw if out of bounds
    return rows.get(row);
  }

  public T getSelectedRow() {
    return rows.get(selectedLine);
  }

  private void scroll(Direction dir, int distance) {
    switch (dir) {
      case UP:
        if (topLine == selectedLine) {
          topLine = Math.max(0, topLine - 1);
        }
        selectedLine = Math.max(0, selectedLine - distance);
        break;
      case DOWN:
        selectedLine = Math.min(rows.size() - 1, selectedLine + distance);
        if (selectedLine == topLine + getHeight()) {
          topLine = Math.min(topLine + 1, rows.size() - getHeight());
        }
        break;
    }
  }

  private void printRows() {
    int width = getWidth() - 1;

    for (int row = 0; row < getHeight(); row++) {
      TerminalUI.move(getX(), getY() + row);
      int index = row + topLine;

      if (index >= rows.size()) {
        TerminalUI.printn(" ", width);
      } else {
        String toPrint = rows.get(index).toString();
        if (toPrint.length() > width) {
          toPrint = toPrint.substring(0, width);
        } else if(toPrint.length() < width) {
          toPrint = toPrint + new String(new char[width - toPrint.length()]).replace('\0', ' ');
        }

        if (index == selectedLine) {
          toPrint = REVERSE + toPrint + RESET;
        }

        TerminalUI.print(toPrint);
      }
    }
  }

  // TODO need to do this better so that it always shows..
  private void printScrollbar() {
    double thumbTop = getHeight()*(double)topLine/rows.size();
    double thumbBottom = thumbTop + getHeight()*(double)getHeight()/rows.size();

    int x = getX() + getWidth();
    for (int row = 0; row <= getHeight(); row++) {
      TerminalUI.move(x, getY() + row);
      if (row >= thumbTop && row <= thumbBottom) {
        TerminalUI.print(REVERSE + " " + RESET);
      } else {
        TerminalUI.print(" ");
      }
    }
  }

  @Override
  public void print() {
    printRows();
    printScrollbar();
  }
}
