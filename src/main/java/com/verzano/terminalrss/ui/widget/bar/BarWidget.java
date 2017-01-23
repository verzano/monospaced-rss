package com.verzano.terminalrss.ui.widget.bar;

import com.verzano.terminalrss.ui.TerminalUI;
import com.verzano.terminalrss.ui.widget.TerminalWidget;
import com.verzano.terminalrss.ui.widget.constants.Direction;
import lombok.Getter;
import lombok.Setter;

import static com.verzano.terminalrss.ui.widget.constants.Ansi.RESET;
import static com.verzano.terminalrss.ui.widget.constants.Ansi.REVERSE;
import static com.verzano.terminalrss.ui.widget.constants.Direction.HORIZONTAL;

public class BarWidget extends TerminalWidget {
  @Getter @Setter
  private String label;

  private Direction direction;

  public BarWidget(String label) {
    this(label, HORIZONTAL);
  }

  public BarWidget(String label, Direction direction) {
    this.label = label;
    this.direction = direction;

    switch (direction) {
      case UP:
      case DOWN:
      case VERTICAL:
        setHeight(TerminalUI.getHeight());
        setWidth(1);
        break;
      case LEFT:
      case RIGHT:
      case HORIZONTAL:
        setHeight(1);
        setWidth(TerminalUI.getWidth());
        break;
    }
  }

  // TODO allow a 2 thick vertical bar and stuff
  @Override
  public void print() {
    // TODO this might be better off in the TerminalUI
    TerminalUI.move(getX(), getY());
    String toPrint = label;

    switch (direction) {
      case UP:
      case DOWN:
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
      case LEFT:
      case RIGHT:
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
}
