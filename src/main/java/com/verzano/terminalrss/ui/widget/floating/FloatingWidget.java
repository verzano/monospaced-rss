package com.verzano.terminalrss.ui.widget.floating;

import com.verzano.terminalrss.ui.TerminalUI;
import com.verzano.terminalrss.ui.metrics.Location;
import com.verzano.terminalrss.ui.metrics.Size;
import com.verzano.terminalrss.ui.widget.Widget;

// TODO force the floaters to the center of the screen always
// TODO need to disable setX setY stuff so that its position can't be fucked with
public abstract class FloatingWidget extends Widget {
  public static final FloatingWidget NULL_FLOATER = new FloatingWidget(new Size(0, 0)) {
    @Override
    public int getNeededWidth() {
      return 0;
    }

    @Override
    public int getNeededHeight() {
      return 0;
    }

    @Override
    public void print() { }

    @Override
    public void size() { }
  };

  public FloatingWidget(Size size) {
    super(size);
  }

  public void centerInTerminal() {
    int x = TerminalUI.getWidth()/2 - getWidth()/2;
    int y = TerminalUI.getHeight()/2 - getHeight()/2;
    setLocation(new Location(x, y));
  }

  @Override
  public void size() {
    centerInTerminal();
  }
}
