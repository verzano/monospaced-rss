package com.verzano.terminalrss.ui.widget.scrollable;

import com.verzano.terminalrss.ui.TerminalUI;
import com.verzano.terminalrss.ui.widget.TerminalWidget;
import com.verzano.terminalrss.ui.widget.constants.Direction;
import lombok.Setter;

import static com.verzano.terminalrss.ui.widget.constants.Ansi.RESET;
import static com.verzano.terminalrss.ui.widget.constants.Ansi.REVERSE;

// TODO allow this to have vertical and horizontal bars
public abstract class ScrollableWidget extends TerminalWidget {
  private double internalHeight = 1;

  @Setter
  private double viewTop = 0;

  private int barLength;

  public abstract void scroll(Direction direction, int distance);

  public void setInternalHeight(double internalHeight) {
    this.internalHeight = internalHeight;
    barLength = (int)Math.ceil(getHeight()*(double)getHeight()/internalHeight);
  }

  @Override
  public void print() {
    int barStart = (int)Math.floor(getHeight()*viewTop/internalHeight);
    int barEnd = barStart + barLength;

    int x = getX() + getWidth();
    for (int row = 0; row <= getHeight(); row++) {
      TerminalUI.move(x, getY() + row);
      if (row >= barStart && row <= barEnd) {
        TerminalUI.print(REVERSE + " " + RESET);
      } else {
        TerminalUI.print(" ");
      }
    }
  }
}
