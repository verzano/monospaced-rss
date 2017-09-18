package com.verzano.terminalrss.tui.widget.button;

import com.verzano.terminalrss.tui.ansi.Attribute;
import com.verzano.terminalrss.tui.constant.Orientation;
import com.verzano.terminalrss.tui.constant.Position;
import com.verzano.terminalrss.tui.task.NamedTask;
import com.verzano.terminalrss.tui.widget.text.TextWidget;

import static com.verzano.terminalrss.tui.constant.Key.ENTER;

public class ButtonWidget extends TextWidget {
  private NamedTask onPressTask;

  public ButtonWidget(NamedTask onPressTask, Position textPosition) {
    super(onPressTask.getName(), Orientation.HORIZONTAL, textPosition);
    this.onPressTask = onPressTask;
    addKeyAction(ENTER, () -> this.onPressTask.fire());
    getFocusedFormat().setAttributes(Attribute.INVERSE_ON);
    getUnfocusedFormat().setAttributes(Attribute.NORMAL);
  }
}
