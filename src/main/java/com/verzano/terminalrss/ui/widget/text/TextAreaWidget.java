package com.verzano.terminalrss.ui.widget.text;

import com.verzano.terminalrss.ui.TerminalUI;
import com.verzano.terminalrss.ui.widget.TerminalWidget;
import com.verzano.terminalrss.ui.widget.constants.Direction;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

import static com.verzano.terminalrss.ui.widget.action.Key.DOWN_ARROW;
import static com.verzano.terminalrss.ui.widget.action.Key.UP_ARROW;
import static com.verzano.terminalrss.ui.widget.constants.Ansi.RESET;
import static com.verzano.terminalrss.ui.widget.constants.Ansi.REVERSE;
import static com.verzano.terminalrss.ui.widget.constants.Direction.DOWN;
import static com.verzano.terminalrss.ui.widget.constants.Direction.UP;

public class TextAreaWidget extends TerminalWidget {
  @Getter
  private String text;

  private List<String> lines;

  private volatile int topLine = 0;

  public TextAreaWidget(String text) {
    this.text = text;
    calculateLines();

    addEscapedKeyAction(UP_ARROW, () -> {
      scroll(UP, 1);
      reprint();
    });
    addEscapedKeyAction(DOWN_ARROW, () -> {
      scroll(DOWN, 1);
      reprint();
    });
  }

  public void setText(String text) {
    this.text = text;
    calculateLines();
  }

  private void calculateLines() {
    lines = new LinkedList<>();
    topLine = 0;

    int width = getWidth() - 1;

    for (String chunk : text.split("\n")) {
      if (chunk.isEmpty()) {
        continue;
      }

      int begin = 0;
      int end = width;

      while (end < chunk.length()) {
        int lastSpace = chunk.substring(begin, end).lastIndexOf(' ') + begin + 1;
        String line = chunk.substring(begin, lastSpace);
        lines.add(line + new String(new char[width - line.length()]).replace('\0', ' '));

        begin = lastSpace;
        end = begin + width;
      }

      String line = chunk.substring(begin);
      lines.add(line + new String(new char[width - line.length()]).replace('\0', ' '));
      lines.add(new String(new char[width]).replace('\0', ' '));
    }
  }

  private void scroll(Direction direction, int distance) {
    switch (direction) {
      case UP:
        topLine = Math.max(0, topLine - distance);
        break;
      case DOWN:
        if (topLine + getHeight() < lines.size()) {
          topLine = topLine + distance;
        }
        break;
    }
  }

  @Override
  public void print() {
    double thumbTop = getHeight()*(double)topLine/lines.size();
    double thumbBottom = thumbTop + getHeight()*(double)getHeight()/lines.size();

    for (int row = 0; row < getHeight(); row++) {
      TerminalUI.move(getX(), getY() + row);
      int selectedLine = row + topLine;
      if (selectedLine < lines.size()) {
        TerminalUI.print(lines.get(selectedLine));
      } else {
        TerminalUI.printn(" ", getWidth() - 1);
      }

      if (row >= thumbTop && row <= thumbBottom) {
        TerminalUI.print(REVERSE + " " + RESET);
      } else {
        TerminalUI.print(" ");
      }
    }
  }
}
