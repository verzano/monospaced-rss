package com.verzano.terminalrss.tui.widget.text.entry;

import static com.verzano.terminalrss.tui.ansi.AnsiFormat.NORMAL;
import static com.verzano.terminalrss.tui.ansi.Attribute.BLINK_ON;
import static com.verzano.terminalrss.tui.ansi.Attribute.UNDERLINE_ON;
import static com.verzano.terminalrss.tui.constants.Key.DELETE;
import static com.verzano.terminalrss.tui.constants.Orientation.HORIZONTAL;
import static com.verzano.terminalrss.tui.constants.Position.CENTER_LEFT;

import com.verzano.terminalrss.tui.ansi.AnsiFormat;
import com.verzano.terminalrss.tui.ansi.Attribute;
import com.verzano.terminalrss.tui.ansi.Background;
import com.verzano.terminalrss.tui.ansi.Foreground;
import com.verzano.terminalrss.tui.widget.text.TextWidget;
import java.util.stream.IntStream;
import lombok.Getter;
import lombok.Setter;

public class TextEntryWidget extends TextWidget {

  @Getter
  @Setter
  private AnsiFormat caretFormat = new AnsiFormat(
      Background.NONE,
      Foreground.NONE,
      UNDERLINE_ON,
      BLINK_ON);

  public TextEntryWidget() {
    super("", HORIZONTAL, CENTER_LEFT);
    // All printable ASCII chars
    IntStream.range(32, 127).forEach(i -> addKeyAction((char) i + "", () -> {
      setText(getText() + (char) i);
      reprint();
    }));

    addKeyAction(DELETE, () -> {
      setText(getText().substring(0, Math.max(0, getText().length() - 1)));
      reprint();
    });

    getFocusedFormat().setAttributes(Attribute.NORMAL);
    getUnfocusedFormat().setAttributes(Attribute.NORMAL);
  }

  private String getCaret() {
    return isFocused() ? caretFormat.getFormatString() + " " + NORMAL.getFormatString() : " ";
  }

  @Override
  protected String getRowForText(String text) {
    int width = getWidth();

    if (text.length() < width - 1) {
      text += getCaret() + new String(new char[width - text.length() - 1]).replace('\0', ' ');
    } else {
      text = text.substring(text.length() - width + 1) + getCaret();
    }

    return text;
  }

  @Override
  public int getNeededWidth() {
    return 1;
  }

  @Override
  public int getNeededHeight() {
    return 1;
  }
}
