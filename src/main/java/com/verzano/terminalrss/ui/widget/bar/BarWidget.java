package com.verzano.terminalrss.ui.widget.bar;

import com.verzano.terminalrss.ui.TerminalUI;
import com.verzano.terminalrss.ui.metrics.Size;
import com.verzano.terminalrss.ui.widget.Widget;
import com.verzano.terminalrss.ui.widget.constants.Direction;
import lombok.Getter;
import lombok.Setter;

import static com.verzano.terminalrss.ui.metrics.Size.FILL_NEEDED;
import static com.verzano.terminalrss.ui.metrics.Size.FILL_PARENT;
import static com.verzano.terminalrss.ui.widget.constants.Ansi.RESET;
import static com.verzano.terminalrss.ui.widget.constants.Ansi.REVERSE;

// TODO allow an N wide vertical bar or M high horizontal bar
// TODO       OR
// TODO lock width and height based on the size of the terminal
// TODO allow positioning of text left/right or top/bottom
public class BarWidget extends Widget {
  @Getter @Setter
  private String text;

  private Direction direction;

  public BarWidget(Direction direction) {
    this("", direction);
  }

  public BarWidget(String text, Direction direction) {
    super(new Size(FILL_PARENT, FILL_PARENT));
    this.text = text;
    setDirection(direction);
  }

  public void setDirection(Direction direction) {
    this.direction = direction;

    switch (direction) {
      case VERTICAL:
        setHeight(FILL_PARENT);
        setWidth(FILL_NEEDED);
        break;
      case HORIZONTAL:
        setHeight(FILL_NEEDED);
        setWidth(FILL_PARENT);
        break;
      default:
        throw new RuntimeException("Direction: " + direction + " not permitted for a " + BarWidget.class.getSimpleName());
    }
  }

  @Override
  public int getNeededWidth() {
    int width = 0;
    switch (direction) {
      case VERTICAL:
        width = 1;
        break;
      case HORIZONTAL:
        width = text.length();
        break;
    }
    return width;
  }

  @Override
  public int getNeededHeight() {
    int height = 0;
    switch (direction) {
      case VERTICAL:
        height = text.length();
        break;
      case HORIZONTAL:
        height = 1;
        break;
    }
    return height;
  }

  @Override
  public void print() {
    TerminalUI.move(getX(), getY());
    String toPrint = text;

    switch (direction) {
      case VERTICAL:
        for (int row = 0; row < getHeight(); row++) {
          TerminalUI.move(getX(), getY() + row);
          if (row < toPrint.length()) {
            TerminalUI.print(REVERSE + toPrint.charAt(row) + RESET);
          } else {
            TerminalUI.print(REVERSE + " " + RESET);
          }
        }
        break;
      case HORIZONTAL:
        if (toPrint.length() > getWidth()) {
          toPrint = toPrint.substring(0, getWidth());
        } else if(toPrint.length() < getWidth()) {
          toPrint = toPrint + new String(new char[getWidth() - toPrint.length()]).replace("\0", " ");
        }
        TerminalUI.print(REVERSE + toPrint + RESET);
        break;
    }
  }

  @Override
  public void size() {

  }
}
