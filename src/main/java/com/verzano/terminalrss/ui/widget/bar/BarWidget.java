package com.verzano.terminalrss.ui.widget.bar;

import com.verzano.terminalrss.ui.TerminalUI;
import com.verzano.terminalrss.ui.metrics.Location;
import com.verzano.terminalrss.ui.metrics.Size;
import com.verzano.terminalrss.ui.widget.TerminalWidget;
import com.verzano.terminalrss.ui.widget.constants.Direction;
import lombok.Getter;
import lombok.Setter;

import static com.verzano.terminalrss.ui.metrics.Size.MATCH_TERMINAL;
import static com.verzano.terminalrss.ui.widget.constants.Ansi.RESET;
import static com.verzano.terminalrss.ui.widget.constants.Ansi.REVERSE;

// TODO allow an N wide vertical bar or M high horizontal bar
// TODO       OR
// TODO lock width and height based on the size of the terminal
// TODO allow positioning of text left/right or top/bottom
public class BarWidget extends TerminalWidget {
  @Getter @Setter
  private String label;

  private Direction direction;

  public BarWidget(Direction direction, Location location) {
    this("", direction, location);
  }

  public BarWidget(String label, Direction direction, Location location) {
    super(new Size(MATCH_TERMINAL, MATCH_TERMINAL), location);
    this.label = label;
    setDirection(direction);
  }

  public void setDirection(Direction direction) {
    this.direction = direction;

    switch (direction) {
      case VERTICAL:
        setHeight(MATCH_TERMINAL);
        setWidth(1);
        break;
      case HORIZONTAL:
        setHeight(1);
        setWidth(MATCH_TERMINAL);
        break;
      default:
        throw new RuntimeException("Direction: " + direction + " not permitted for a " + BarWidget.class.getSimpleName());
    }
  }

  @Override
  public void print() {
    TerminalUI.move(getX(), getY());
    String toPrint = label;

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
