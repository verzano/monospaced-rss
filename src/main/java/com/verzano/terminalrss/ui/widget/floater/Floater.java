package com.verzano.terminalrss.ui.widget.floater;

import com.verzano.terminalrss.ui.TerminalUI;
import com.verzano.terminalrss.ui.metrics.Location;
import com.verzano.terminalrss.ui.widget.Widget;
import lombok.NoArgsConstructor;

// TODO force the floaters to the center of the screen always
// TODO need to disable setX setY stuff so that its position can't be fucked with
@NoArgsConstructor
public abstract class Floater {
  public static final Floater NULL_FLOATER = new Floater() {
    @Override
    public Widget getBaseWidget() {
      return Widget.NULL_WIDGET;
    }
  };

  public abstract Widget getBaseWidget();

  public void centerInTerminal() {
    int x = TerminalUI.getWidth()/2 - getBaseWidget().getWidth()/2;
    int y = TerminalUI.getHeight()/2 - getBaseWidget().getHeight()/2;
    getBaseWidget().setLocation(new Location(x, y));
  }
}
