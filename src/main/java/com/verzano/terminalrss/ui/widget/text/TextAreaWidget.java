package com.verzano.terminalrss.ui.widget.text;

import com.verzano.terminalrss.ui.TerminalUI;
import com.verzano.terminalrss.ui.widget.TerminalWidget;
import com.verzano.terminalrss.ui.widget.constants.Direction;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

import static com.verzano.terminalrss.ui.widget.action.Key.DOWN_ARROW;
import static com.verzano.terminalrss.ui.widget.action.Key.UP_ARROW;
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

    addEscapedKeyAction(UP_ARROW, () -> scroll(UP, 1));
    addEscapedKeyAction(DOWN_ARROW, () -> scroll(DOWN, 1));
  }

  public void setText(String text) {
    this.text = text;
    calculateLines();
  }

  private void calculateLines() {
    lines = new LinkedList<>();
    topLine = 0;

    int begin = 0;
    int end = getWidth();

    while(end < text.length()) {
      int lastSpace = text.substring(begin, end).lastIndexOf(' ') + begin + 1;
      lines.add(text.substring(begin, lastSpace));

      begin = lastSpace;
      end = begin + getWidth();
    }

    lines.add(text.substring(begin));
  }

  private void scroll(Direction direction, int distance) {
    switch (direction) {
      case UP:
        topLine = Math.max(0, topLine - 1);
        TerminalUI.reprint();
        break;
      case DOWN:
        if (topLine + getHeight() < lines.size()) {
          topLine++;
        }
        TerminalUI.reprint();
        break;
    }
  }

  @Override
  public void print() {
    for (int row = 0; row < getHeight(); row++) {
      TerminalUI.move(getX(), getY() + row);
      int selectedLine = row + topLine;
      if (selectedLine < lines.size()) {
        TerminalUI.print(lines.get(selectedLine));
      } else {
        TerminalUI.printn(" ", getWidth());
      }
    }
  }
}
