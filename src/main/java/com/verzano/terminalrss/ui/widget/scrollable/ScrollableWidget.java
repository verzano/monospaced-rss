package com.verzano.terminalrss.ui.widget.scrollable;

import com.verzano.terminalrss.ui.TerminalUI;
import com.verzano.terminalrss.ui.widget.Widget;
import com.verzano.terminalrss.ui.widget.ansi.AnsiTextFormatBuilder;
import com.verzano.terminalrss.ui.widget.ansi.Attribute;
import com.verzano.terminalrss.ui.widget.constants.Direction;
import lombok.NoArgsConstructor;
import lombok.Setter;

// TODO allow this to have vertical and horizontal bars
// TODO make this a container
// TODO make the scrollbar its own widget
@NoArgsConstructor
public abstract class ScrollableWidget extends Widget {
  private double internalHeight = 1;

  @Setter
  private double viewTop = 0;

  private int barLength;

  public abstract void scroll(Direction direction, int distance);

  private static final String SCROLLBAR_PIXEL = AnsiTextFormatBuilder.build(Attribute.INVERSE_ON) + " " + AnsiTextFormatBuilder.build(Attribute.INVERSE_OFF);

  public void setInternalHeight(double internalHeight) {
    this.internalHeight = internalHeight;
    barLength = (int)Math.ceil(getHeight()*(double)getHeight()/internalHeight);
  }

  @Override
  public void printContent() {
    int barStart = (int)Math.floor(getContentHeight()*viewTop/internalHeight);
    int barEnd = barStart + barLength;

    int x = getContentX() + getContentWidth();
    for (int row = 0; row <= getContentHeight(); row++) {
      TerminalUI.move(x, getContentY() + row);
      if (row >= barStart && row <= barEnd) {
        TerminalUI.print(SCROLLBAR_PIXEL);
      } else {
        TerminalUI.print(" ");
      }
    }
  }
}
