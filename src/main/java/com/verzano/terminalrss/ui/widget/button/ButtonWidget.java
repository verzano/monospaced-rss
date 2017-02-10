package com.verzano.terminalrss.ui.widget.button;

import com.verzano.terminalrss.ui.metrics.Size;
import com.verzano.terminalrss.ui.task.key.KeyTask;
import com.verzano.terminalrss.ui.widget.bar.TextWidget;
import com.verzano.terminalrss.ui.widget.constants.Orientation;
import com.verzano.terminalrss.ui.widget.constants.Position;

import static com.verzano.terminalrss.ui.widget.constants.Ansi.NORMAL;
import static com.verzano.terminalrss.ui.widget.constants.Ansi.REVERSE;
import static com.verzano.terminalrss.ui.widget.constants.Key.ENTER;

// TODO make this more efficient
public class ButtonWidget extends TextWidget {
  public ButtonWidget(KeyTask onPress, String text, Position textPosition, Size size) {
    super(text, Orientation.HORIZONTAL, textPosition, size);
    addKeyAction(ENTER, onPress);
    setFocusedFormat(REVERSE);
    setNotFocusedFormat(NORMAL);
  }
}
