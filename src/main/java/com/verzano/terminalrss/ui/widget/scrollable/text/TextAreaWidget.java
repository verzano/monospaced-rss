package com.verzano.terminalrss.ui.widget.scrollable.text;

import com.verzano.terminalrss.ui.TerminalUI;
import com.verzano.terminalrss.ui.metrics.Location;
import com.verzano.terminalrss.ui.metrics.Size;
import com.verzano.terminalrss.ui.widget.constants.Direction;
import com.verzano.terminalrss.ui.widget.scrollable.ScrollableWidget;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

import static com.verzano.terminalrss.ui.widget.constants.Direction.DOWN;
import static com.verzano.terminalrss.ui.widget.constants.Direction.UP;
import static com.verzano.terminalrss.ui.widget.constants.Key.DOWN_ARROW;
import static com.verzano.terminalrss.ui.widget.constants.Key.UP_ARROW;

public class TextAreaWidget extends ScrollableWidget {
  @Getter
  private String text;

  private List<String> lines;

  private volatile int topLine;

  public TextAreaWidget(Size size, Location location) {
    this("", size, location);
  }

  public TextAreaWidget(String text, Size size, Location location) {
    super(size, location);
    setText(text);

    addKeyAction(UP_ARROW, () -> {
      scroll(UP, 1);
      reprint();
    });
    addKeyAction(DOWN_ARROW, () -> {
      scroll(DOWN, 1);
      reprint();
    });
  }

  public void setText(String text) {
    this.text = text;
    calculateLines();
    setTopLine(0);
  }

  private void setTopLine(int topLine) {
    this.topLine = topLine;
    setViewTop(topLine);
  }

  private void calculateLines() {
    lines = new LinkedList<>();

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

    setInternalHeight(lines.size());
  }

  @Override
  public void scroll(Direction direction, int distance) {
    switch (direction) {
      case UP:
        setTopLine(Math.max(0, topLine - distance));
        break;
      case DOWN:
        if (topLine + getHeight() < lines.size()) {
          setTopLine(topLine + distance);
        }
        break;
    }
  }

  @Override
  public void print() {
    super.print();

    int width = getWidth() - 1;

    for (int row = 0; row <= getHeight(); row++) {
      TerminalUI.move(getX(), getY() + row);
      int line = row + topLine;
      if (line < lines.size()) {
        TerminalUI.print(lines.get(line));
      } else {
        TerminalUI.printn(" ", width);
      }
    }
  }

  @Override
  public void size() {
    super.size();
    calculateLines();
  }
}
