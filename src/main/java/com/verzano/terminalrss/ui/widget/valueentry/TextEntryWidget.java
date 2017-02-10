package com.verzano.terminalrss.ui.widget.valueentry;

import com.verzano.terminalrss.ui.metrics.Size;
import com.verzano.terminalrss.ui.widget.bar.TextWidget;

import java.util.stream.IntStream;

import static com.verzano.terminalrss.ui.widget.constants.Ansi.BLINK;
import static com.verzano.terminalrss.ui.widget.constants.Ansi.NORMAL;
import static com.verzano.terminalrss.ui.widget.constants.Ansi.RESET_BLINK;
import static com.verzano.terminalrss.ui.widget.constants.Ansi.RESET_UNDERLINE;
import static com.verzano.terminalrss.ui.widget.constants.Ansi.UNDERLINE;
import static com.verzano.terminalrss.ui.widget.constants.Key.DELETE;
import static com.verzano.terminalrss.ui.widget.constants.Orientation.HORIZONTAL;
import static com.verzano.terminalrss.ui.widget.constants.Position.CENTER_LEFT;

public class TextEntryWidget extends TextWidget {
  public TextEntryWidget(Size size) {
    super("", HORIZONTAL, CENTER_LEFT, size);
    // All printable ASCII chars
    IntStream.range(32, 127).forEach(i -> addKeyAction((char)i + "", () -> {
      setText(getText() + (char)i);
      reprint();
    }));

    addKeyAction(DELETE, () -> {
      setText(getText().substring(0, Math.max(0, getText().length() - 1)));
      reprint();
    });

    setFocusedFormat(NORMAL);
    setNotFocusedFormat(NORMAL);
  }

  private String getCaret() {
    return isFocused()
        ? BLINK + UNDERLINE + ' ' + RESET_UNDERLINE + RESET_BLINK
        : " ";
  }

  @Override
  protected String getTextRow() {
    String textRow = getText();
    int width = getWidth();

    if (textRow.length() < width - 1) {
      textRow += getCaret()
          + getFocusedFormat() + new String(new char[width - textRow.length() - 1]).replace('\0', ' ');
    } else {
      textRow = textRow.substring(textRow.length() - width + 1) + getCaret();
    }

    return textRow;
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
