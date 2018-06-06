package com.verzano.monospaced.gui.widget.button;

import com.verzano.monospaced.gui.ansi.Attribute;
import com.verzano.monospaced.gui.constant.Orientation;
import com.verzano.monospaced.gui.constant.Position;
import com.verzano.monospaced.gui.task.NamedTask;
import com.verzano.monospaced.gui.widget.text.TextWidget;

import static com.verzano.monospaced.gui.constant.Key.ENTER;

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
