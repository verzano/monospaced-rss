package com.verzano.monospaced.gui.widget.text.entry;

import com.verzano.monospaced.gui.MonospacedGui;
import com.verzano.monospaced.gui.ansi.AnsiFormat;
import com.verzano.monospaced.gui.ansi.Attribute;
import com.verzano.monospaced.gui.ansi.Background;
import com.verzano.monospaced.gui.ansi.Foreground;
import com.verzano.monospaced.gui.widget.text.TextWidget;

import java.util.stream.IntStream;

import static com.verzano.monospaced.gui.ansi.AnsiFormat.NORMAL;
import static com.verzano.monospaced.gui.ansi.Attribute.BLINK_ON;
import static com.verzano.monospaced.gui.ansi.Attribute.UNDERLINE_ON;
import static com.verzano.monospaced.gui.constant.Key.DELETE;
import static com.verzano.monospaced.gui.constant.Orientation.HORIZONTAL;
import static com.verzano.monospaced.gui.constant.Position.LEFT;

public class TextEntryWidget extends TextWidget {
  private AnsiFormat caretFormat = new AnsiFormat(Background.NONE, Foreground.NONE, UNDERLINE_ON, BLINK_ON);

  public TextEntryWidget() {
    super("", HORIZONTAL, LEFT);
    // All printable ASCII chars
    // TODO put this in a class (no magic numbers)
    IntStream.range(32, 127).forEach(i -> addKeyAction((char)i + "", () -> {
      setText(getText() + (char)i);
      reprint();
    }));

    addKeyAction(DELETE, () -> {
      setText(getText().substring(0, Math.max(0, getText().length() - 1)));
      reprint();
    });

    getFocusedFormat().setAttributes(Attribute.NORMAL);
    getUnfocusedFormat().setAttributes(Attribute.NORMAL);
  }

  public AnsiFormat getCaretFormat() {
    return caretFormat;
  }

  public void setCaretFormat(AnsiFormat caretFormat) {
    this.caretFormat = caretFormat;
  }

  private String getCaret() {
    return isFocused() ? caretFormat.getFormatString() + " " + NORMAL.getFormatString() : " ";
  }

  @Override
  public int getNeededContentHeight() {
    return 1;
  }

  @Override
  public int getNeededContentWidth() {
    return 1;
  }

  @Override
  public void printContent() {
    String text = getText();
    int width = getContentWidth();

    if(text.length() < width - 1) {
      text += getCaret() + new String(new char[width - text.length() - 1]).replace('\0', ' ');
    } else {
      text = text.substring(text.length() - width + 1) + getCaret();
    }

    int middleRow = getContentHeight()/2;
    for(int i = 0; i < getContentHeight(); i++) {
      MonospacedGui.move(getContentX(), getContentY() + i);
      if(i == middleRow) {
        MonospacedGui.print(text);
      } else {
        MonospacedGui.print(getEmptyContentRow());
      }
    }
  }
}
