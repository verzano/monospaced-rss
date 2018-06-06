package com.verzano.monospaced.gui.floater;

import com.verzano.monospaced.gui.MonospacedGui;
import com.verzano.monospaced.gui.container.enclosure.Enclosure;
import com.verzano.monospaced.gui.task.Task;

import static com.verzano.monospaced.gui.task.Task.NULL_TASK;

public abstract class Floater extends Enclosure {
  public static final Floater NULL_FLOATER = new Floater() {};
  private Task disposeTask = NULL_TASK;

  public Floater() {}

  public void setDisposeTask(Task disposeTask) {
    this.disposeTask = disposeTask;
  }

  public void display() {
    MonospacedGui.setFloater(this);
    reprint();
  }

  public void dispose() {
    MonospacedGui.removeFloater();
    disposeTask.fire();
    MonospacedGui.reprint();
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
    return MonospacedGui.getWidth()/2 - getWidth()/2;
  }

  @Override
  public final int getY() {
    return MonospacedGui.getHeight()/2 - getHeight()/2;
  }
}
