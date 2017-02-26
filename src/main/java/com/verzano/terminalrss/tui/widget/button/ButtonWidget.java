package com.verzano.terminalrss.tui.widget.button;

import com.verzano.terminalrss.tui.ansi.Attribute;
import com.verzano.terminalrss.tui.constants.Orientation;
import com.verzano.terminalrss.tui.constants.Position;
import com.verzano.terminalrss.tui.metrics.Size;
import com.verzano.terminalrss.tui.task.key.KeyTask;
import com.verzano.terminalrss.tui.widget.text.TextWidget;

import static com.verzano.terminalrss.tui.constants.Key.ENTER;

public class ButtonWidget extends TextWidget {
  public ButtonWidget(KeyTask onPress, String text, Position textPosition, Size size) {
    super(text, Orientation.HORIZONTAL, textPosition);
    addKeyAction(ENTER, onPress);
    getFocusedFormat().setAttributes(Attribute.INVERSE_ON);
    getUnfocusedFormat().setAttributes(Attribute.NORMAL);
  }
}
