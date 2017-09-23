package com.verzano.terminalui.widget.text.entry;

import static com.verzano.terminalui.ansi.AnsiFormat.NORMAL;
import static com.verzano.terminalui.ansi.Attribute.BLINK_ON;
import static com.verzano.terminalui.ansi.Attribute.UNDERLINE_ON;
import static com.verzano.terminalui.constant.Key.DELETE;
import static com.verzano.terminalui.constant.Orientation.HORIZONTAL;
import static com.verzano.terminalui.constant.Position.LEFT;

import com.verzano.terminalui.TerminalUi;
import com.verzano.terminalui.ansi.AnsiFormat;
import com.verzano.terminalui.ansi.Attribute;
import com.verzano.terminalui.ansi.Background;
import com.verzano.terminalui.ansi.Foreground;
import com.verzano.terminalui.widget.text.TextWidget;
import java.util.stream.IntStream;
import lombok.Getter;
import lombok.Setter;

public class TextEntryWidget extends TextWidget {
  @Getter
  @Setter
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
      TerminalUi.move(getContentX(), getContentY() + i);
      if(i == middleRow) {
        TerminalUi.print(text);
      } else {
        TerminalUi.print(getEmptyContentRow());
      }
    }
  }
}
