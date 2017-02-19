package com.verzano.terminalrss.ui.widget.text.entry;

import com.verzano.terminalrss.ui.metrics.Size;
import com.verzano.terminalrss.ui.widget.ansi.AnsiTextFormatBuilder;
import com.verzano.terminalrss.ui.widget.ansi.Attribute;
import com.verzano.terminalrss.ui.widget.text.TextWidget;

import java.util.stream.IntStream;

import static com.verzano.terminalrss.ui.widget.ansi.Attribute.BLINK_OFF;
import static com.verzano.terminalrss.ui.widget.ansi.Attribute.BLINK_ON;
import static com.verzano.terminalrss.ui.widget.ansi.Attribute.UNDERLINE_OFF;
import static com.verzano.terminalrss.ui.widget.ansi.Attribute.UNDERLINE_ON;
import static com.verzano.terminalrss.ui.widget.constants.Key.DELETE;
import static com.verzano.terminalrss.ui.widget.constants.Orientation.HORIZONTAL;
import static com.verzano.terminalrss.ui.widget.constants.Position.CENTER_LEFT;

public class TextEntryWidget extends TextWidget {
  private static final String CARET_PREFIX = AnsiTextFormatBuilder.build(UNDERLINE_ON, BLINK_ON);
  private static final String CARET_POSTFIX = AnsiTextFormatBuilder.build(UNDERLINE_OFF, BLINK_OFF);

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

    setFocusedAttribute(Attribute.NORMAL);
    setUnfocusedAttribute(Attribute.NORMAL);
  }

  private String getCaret() {
    return isFocused() ? CARET_PREFIX + " " + CARET_POSTFIX : " ";
  }

  @Override
  protected String getTextRow() {
    String textRow = getText();
    int width = getWidth();

    if (textRow.length() < width - 1) {
      textRow += getCaret() + new String(new char[width - textRow.length() - 1]).replace('\0', ' ');
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
