package com.verzano.terminalrss.ui.widget.scrollable;

import com.verzano.terminalrss.ui.TerminalUI;
import com.verzano.terminalrss.ui.ansi.AnsiFormat;
import com.verzano.terminalrss.ui.ansi.Attribute;
import com.verzano.terminalrss.ui.ansi.Background;
import com.verzano.terminalrss.ui.ansi.Foreground;
import com.verzano.terminalrss.ui.constants.Direction;
import com.verzano.terminalrss.ui.widget.Widget;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.verzano.terminalrss.ui.ansi.AnsiFormat.NORMAL;

// TODO allow this to have vertical and horizontal bars
// TODO make this a container
// TODO make the scrollbar its own widget
@NoArgsConstructor
public abstract class ScrollableWidget extends Widget {
  private double internalHeight = 1;

  @Getter @Setter
  private AnsiFormat scrollbarFormat = new AnsiFormat(Background.NONE, Foreground.NONE, Attribute.INVERSE_ON);

  @Getter @Setter
  private int viewTop = 0;

  private int barLength;

  public abstract void scroll(Direction direction, int distance);

  public void setInternalHeight(double internalHeight) {
    this.internalHeight = internalHeight;
    barLength = (int)Math.ceil(getHeight()*(double)getHeight()/internalHeight);
  }

  @Override
  public void printContent() {
    int barStart = (int)Math.floor(getContentHeight()*(double)viewTop/internalHeight);
    int barEnd = barStart + barLength;

    int x = getContentX() + getContentWidth();
    for (int row = 0; row <= getContentHeight(); row++) {
      TerminalUI.move(x, getContentY() + row);
      if (row >= barStart && row <= barEnd) {
        TerminalUI.print(scrollbarFormat.getFormatString() + " " + NORMAL.getFormatString());
      } else {
        TerminalUI.print(" ");
      }
    }
  }
}
