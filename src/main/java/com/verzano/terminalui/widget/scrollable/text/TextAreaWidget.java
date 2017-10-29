package com.verzano.terminalui.widget.scrollable.text;

import static com.verzano.terminalui.constant.Key.DOWN_ARROW;
import static com.verzano.terminalui.constant.Key.UP_ARROW;

import com.verzano.terminalui.TerminalUi;
import com.verzano.terminalui.constant.Direction;
import com.verzano.terminalui.widget.scrollable.ScrollableWidget;
import java.util.LinkedList;
import java.util.List;

public class TextAreaWidget extends ScrollableWidget {
  private String text;
  private List<String> lines;
  private volatile int topLine;

  public TextAreaWidget() {
    this("");
  }

  public TextAreaWidget(String text) {
    setText(text);

    addKeyAction(UP_ARROW, () -> {
      scroll(Direction.UP, 1);
      reprint();
    });
    addKeyAction(DOWN_ARROW, () -> {
      scroll(Direction.DOWN, 1);
      reprint();
    });
  }

  public String getText() {
    return text;
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

    for(String chunk : text.split("\n")) {
      if(chunk.isEmpty()) {
        continue;
      }

      int begin = 0;
      int end = width;

      while(end < chunk.length()) {
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
  public int getNeededContentHeight() {
    int width = getWidth() - 1;

    return lines.stream().mapToInt(row -> (int)Math.ceil(row.length()/width)).sum();
  }

  @Override
  public int getNeededContentWidth() {
    return lines.stream().mapToInt(String::length).max().orElse(0) + 1;
  }

  @Override
  public void size() {
    super.size();
    calculateLines();
  }

  @Override
  public void scroll(Direction direction, int distance) {
    switch(direction) {
      case UP:
        setTopLine(Math.max(0, topLine - distance));
        break;
      case DOWN:
        if(topLine + getHeight() < lines.size()) {
          setTopLine(topLine + distance);
        }
        break;
    }
  }

  @Override
  public void printContent() {
    super.printContent();

    int width = getContentWidth() - 1;

    for(int row = 0; row <= getContentHeight(); row++) {
      TerminalUi.move(getContentX(), getContentY() + row);
      int line = row + topLine;
      if(line < lines.size()) {
        TerminalUi.print(lines.get(line));
      } else {
        TerminalUi.printn(" ", width);
      }
    }
  }
}
