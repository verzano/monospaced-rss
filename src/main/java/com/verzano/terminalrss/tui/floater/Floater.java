package com.verzano.terminalrss.tui.floater;

import static com.verzano.terminalrss.tui.task.Task.NULL_TASK;

import com.verzano.terminalrss.tui.TerminalUi;
import com.verzano.terminalrss.tui.container.enclosure.Enclosure;
import com.verzano.terminalrss.tui.task.Task;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public abstract class Floater extends Enclosure {
  public static final Floater NULL_FLOATER = new Floater() {};
  @Setter private Task disposeTask = NULL_TASK;

  public void dispose() {
    TerminalUi.removeFloater();
    disposeTask.fire();
    TerminalUi.reprint();
  }

  public void display() {
    TerminalUi.setFloater(this);
    reprint();
  }

  @Override
  public int getWidth() {
    return super.getNeededContentWidth();
  }

  @Override
  public int getHeight() {
    return super.getNeededHeight();
  }

  @Override
  public final int getX() {
    return TerminalUi.getWidth()/2 - getWidth()/2;
  }

  @Override
  public final int getY() {
    return TerminalUi.getHeight()/2 - getHeight()/2;
  }
}
