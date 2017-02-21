package com.verzano.terminalrss.ui.widget.scrollable.list;

import com.verzano.terminalrss.ui.TerminalUI;
import com.verzano.terminalrss.ui.widget.ansi.AnsiFormat;
import com.verzano.terminalrss.ui.widget.ansi.Attribute;
import com.verzano.terminalrss.ui.widget.ansi.Background;
import com.verzano.terminalrss.ui.widget.ansi.Foreground;
import com.verzano.terminalrss.ui.widget.constants.Direction;
import com.verzano.terminalrss.ui.widget.scrollable.ScrollableWidget;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

import static com.verzano.terminalrss.ui.widget.ansi.AnsiFormat.NORMAL;
import static com.verzano.terminalrss.ui.widget.constants.Key.DOWN_ARROW;
import static com.verzano.terminalrss.ui.widget.constants.Key.UP_ARROW;

// TODO rework this as a vertical Shelf of TextWidgets...
public class ListWidget<T> extends ScrollableWidget {
  @Getter
  private List<T> rows;

  private int selectedLine;

  private int topLine;

  @Getter @Setter
  private AnsiFormat selectedRowFormat = new AnsiFormat(Background.NONE, Foreground.NONE, Attribute.INVERSE_ON);

  public ListWidget() {
    this(new LinkedList<>());
  }

  public ListWidget(List<T> rows) {
    setRows(rows);

    addKeyAction(UP_ARROW, () -> {
      scroll(Direction.UP, 1);
      reprint();
    });
    addKeyAction(DOWN_ARROW, () -> {
      scroll(Direction.DOWN, 1);
      reprint();
    });
  }

  public void addRow(T row) {
    rows.add(row);
    setInternalHeight(rows.size());
  }

  public void setRows(List<T> rows) {
    this.rows = rows;
    selectedLine = 0;
    setTopLine(0);
    setInternalHeight(this.rows.size());
  }

  private void setTopLine(int topLine) {
    this.topLine = topLine;
    setViewTop(topLine);
  }

  public T getSelectedRow() {
    return rows.get(selectedLine);
  }

  @Override
  public void scroll(Direction dir, int distance) {
    switch (dir) {
      case UP:
        if (topLine == selectedLine) {
          setTopLine(Math.max(0, topLine - 1));
        }
        selectedLine = Math.max(0, selectedLine - distance);
        break;
      case DOWN:
        selectedLine = Math.min(rows.size() - 1, selectedLine + distance);
        if (selectedLine == topLine + getHeight()) {
          setTopLine(Math.min(topLine + 1, rows.size() - getHeight()));
        }
        break;
    }
  }

  @Override
  public int getNeededWidth() {
    return rows.stream()
        .mapToInt(row -> row.toString().length())
        .max()
        .orElseGet(() -> 0) + 1;
  }

  @Override
  public int getNeededHeight() {
    return rows.size();
  }

  @Override
  public void printContent() {
    super.printContent();

    int width = getContentWidth() - 1;

    for (int row = 0; row < getContentHeight(); row++) {
      TerminalUI.move(getContentX(), getContentY() + row);
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
          toPrint = selectedRowFormat.getFormatString() + toPrint + NORMAL.getFormatString();
        }

        TerminalUI.print(toPrint);
      }
    }
  }
}
