package com.verzano.terminalrss.ui.widget.button;

import com.verzano.terminalrss.ui.ansi.Attribute;
import com.verzano.terminalrss.ui.constants.Orientation;
import com.verzano.terminalrss.ui.constants.Position;
import com.verzano.terminalrss.ui.metrics.Size;
import com.verzano.terminalrss.ui.task.key.KeyTask;
import com.verzano.terminalrss.ui.widget.text.TextWidget;

import static com.verzano.terminalrss.ui.constants.Key.ENTER;

public class ButtonWidget extends TextWidget {
  public ButtonWidget(KeyTask onPress, String text, Position textPosition, Size size) {
    super(text, Orientation.HORIZONTAL, textPosition);
    addKeyAction(ENTER, onPress);
    getFocusedFormat().setAttributes(Attribute.INVERSE_ON);
    getUnfocusedFormat().setAttributes(Attribute.NORMAL);
  }
}
