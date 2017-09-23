package com.verzano.terminalui.widget.button;

import static com.verzano.terminalui.constant.Key.ENTER;

import com.verzano.terminalui.ansi.Attribute;
import com.verzano.terminalui.constant.Orientation;
import com.verzano.terminalui.constant.Position;
import com.verzano.terminalui.task.NamedTask;
import com.verzano.terminalui.widget.text.TextWidget;

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
