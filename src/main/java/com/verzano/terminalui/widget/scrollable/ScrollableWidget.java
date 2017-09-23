package com.verzano.terminalui.widget.scrollable;

import static com.verzano.terminalui.ansi.AnsiFormat.NORMAL;

import com.verzano.terminalui.TerminalUi;
import com.verzano.terminalui.ansi.AnsiFormat;
import com.verzano.terminalui.ansi.Attribute;
import com.verzano.terminalui.ansi.Background;
import com.verzano.terminalui.ansi.Foreground;
import com.verzano.terminalui.constant.Direction;
import com.verzano.terminalui.widget.Widget;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// TODO allow this to have vertical and horizontal bars
// TODO make this a container
// TODO make the scrollbar its own widget
// TODO currently scrollbar prints too much when its long
@NoArgsConstructor
public abstract class ScrollableWidget extends Widget {
  private double internalHeight = 1;
  @Getter
  @Setter
  private AnsiFormat scrollbarFormat = new AnsiFormat(Background.NONE, Foreground.NONE, Attribute.INVERSE_ON);
  @Getter
  @Setter
  private int viewTop = 0;
  private int barLength;

  public abstract void scroll(Direction direction, int distance);

  @Override
  public void printContent() {
    int barStart = (int)Math.floor(getContentHeight()*(double)viewTop/internalHeight);
    int barEnd = barStart + barLength;

    int x = getContentX() + getContentWidth();
    for(int row = 0; row <= getContentHeight(); row++) {
      TerminalUi.move(x, getContentY() + row);
      if(row >= barStart && row <= barEnd) {
        TerminalUi.print(scrollbarFormat.getFormatString() + " " + NORMAL.getFormatString());
      } else {
        TerminalUi.print(" ");
      }
    }
  }

  public void setInternalHeight(double internalHeight) {
    this.internalHeight = internalHeight;
    barLength = (int)Math.ceil(getHeight()*(double)getHeight()/internalHeight);
  }
}
