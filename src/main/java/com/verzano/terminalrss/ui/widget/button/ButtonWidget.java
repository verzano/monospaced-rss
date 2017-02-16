package com.verzano.terminalrss.ui.widget.button;

import com.verzano.terminalrss.ui.metrics.Size;
import com.verzano.terminalrss.ui.task.key.KeyTask;
import com.verzano.terminalrss.ui.widget.ansi.Attribute;
import com.verzano.terminalrss.ui.widget.constants.Orientation;
import com.verzano.terminalrss.ui.widget.constants.Position;
import com.verzano.terminalrss.ui.widget.text.TextWidget;

import static com.verzano.terminalrss.ui.widget.constants.Key.ENTER;

public class ButtonWidget extends TextWidget {
  public ButtonWidget(KeyTask onPress, String text, Position textPosition, Size size) {
    super(text, Orientation.HORIZONTAL, textPosition, size);
    addKeyAction(ENTER, onPress);
    setFocusedAttribute(Attribute.INVERSE_ON);
    setUnfocusedAttribute(Attribute.NORMAL);
  }
}
