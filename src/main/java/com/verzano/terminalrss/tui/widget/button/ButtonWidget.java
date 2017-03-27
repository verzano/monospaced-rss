package com.verzano.terminalrss.tui.widget.button;

import static com.verzano.terminalrss.tui.constants.Key.ENTER;

import com.verzano.terminalrss.tui.ansi.Attribute;
import com.verzano.terminalrss.tui.constants.Orientation;
import com.verzano.terminalrss.tui.constants.Position;
import com.verzano.terminalrss.tui.task.key.KeyTask;
import com.verzano.terminalrss.tui.widget.text.TextWidget;
import lombok.Setter;

public class ButtonWidget extends TextWidget {

  @Setter
  private KeyTask onPress;

  public ButtonWidget(KeyTask onPress, String text, Position textPosition) {
    super(text, Orientation.HORIZONTAL, textPosition);
    this.onPress = onPress;
    addKeyAction(ENTER, () -> this.onPress.fire());
    getFocusedFormat().setAttributes(Attribute.INVERSE_ON);
    getUnfocusedFormat().setAttributes(Attribute.NORMAL);
  }
}
