package com.verzano.terminalui.floater;

import static com.verzano.terminalui.task.Task.NULL_TASK;

import com.verzano.terminalui.TerminalUi;
import com.verzano.terminalui.container.enclosure.Enclosure;
import com.verzano.terminalui.task.Task;

public abstract class Floater extends Enclosure {
  public static final Floater NULL_FLOATER = new Floater() {};
  private Task disposeTask = NULL_TASK;

  public Floater() {}

  public void setDisposeTask(Task disposeTask) {
    this.disposeTask = disposeTask;
  }

  public void display() {
    TerminalUi.setFloater(this);
    reprint();
  }

  public void dispose() {
    TerminalUi.removeFloater();
    disposeTask.fire();
    TerminalUi.reprint();
  }

  @Override
  public int getHeight() {
    return super.getNeededHeight();
  }

  @Override
  public int getWidth() {
    return super.getNeededContentWidth();
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
