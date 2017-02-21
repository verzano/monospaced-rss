package com.verzano.terminalrss.ui.floater;

import com.verzano.terminalrss.ui.TerminalUI;
import com.verzano.terminalrss.ui.container.enclosure.Enclosure;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class Floater extends Enclosure {
  public static final Floater NULL_FLOATER = new Floater() { };

  @Override
  public int getWidth() {
    return super.getNeededWidth();
  }

  @Override
  public int getHeight() {
    return super.getNeededHeight();
  }

  @Override
  public final int getX() {
    return TerminalUI.getWidth()/2 - getWidth()/2;
  }

  @Override
  public final int getY() {
    return TerminalUI.getHeight()/2 - getHeight()/2;
  }
}
