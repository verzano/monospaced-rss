package com.verzano.terminalrss.ui.widget.scrollable;

import com.verzano.terminalrss.ui.TerminalUI;
import com.verzano.terminalrss.ui.metrics.Size;
import com.verzano.terminalrss.ui.widget.Widget;
import com.verzano.terminalrss.ui.widget.ansi.AnsiTextFormat;
import com.verzano.terminalrss.ui.widget.ansi.Attribute;
import com.verzano.terminalrss.ui.widget.constants.Direction;
import lombok.Setter;

// TODO allow this to have vertical and horizontal bars
public abstract class ScrollableWidget extends Widget {
  private double internalHeight = 1;

  @Setter
  private double viewTop = 0;

  private int barLength;

  public abstract void scroll(Direction direction, int distance);

  private static final String SCROLLBAR_PIXEL = AnsiTextFormat.build(Attribute.INVERSE_ON) + " " + AnsiTextFormat.build(Attribute.INVERSE_OFF);

  public ScrollableWidget(Size size) {
    super(size);
  }

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
        TerminalUI.print(SCROLLBAR_PIXEL);
      } else {
        TerminalUI.print(" ");
      }
    }
  }

  @Override
  public void size() {

  }
}
