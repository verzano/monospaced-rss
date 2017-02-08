package com.verzano.terminalrss.ui.widget.valueentry;

import com.verzano.terminalrss.ui.TerminalUI;
import com.verzano.terminalrss.ui.metrics.Size;
import com.verzano.terminalrss.ui.widget.Widget;
import lombok.Getter;
import lombok.Setter;

import java.util.stream.IntStream;

import static com.verzano.terminalrss.ui.widget.constants.Ansi.RESET;
import static com.verzano.terminalrss.ui.widget.constants.Ansi.REVERSE;
import static com.verzano.terminalrss.ui.widget.constants.Key.DELETE;

public class TextEntryWidget extends Widget {
  @Getter @Setter
  private String text = "";

  private String emptyBar;

  private static final String EMPTY_COL = REVERSE + " " + RESET;

  public TextEntryWidget(Size size) {
    super(size);

    // All printable ASCII chars
    IntStream.range(32, 127).forEach(i -> addKeyAction((char)i + "", () -> {
      text += (char)i;
      reprint();
    }));

    addKeyAction(DELETE, () -> {
      text = text.substring(0, Math.max(0, text.length() - 1));
      reprint();
    });

    size();
  }

  // TODO 1 really? i dont fucking know
  @Override
  public int getNeededWidth() {
    return 1;
  }

  @Override
  public int getNeededHeight() {
    return 1;
  }

  // TODO this ignores height
  @Override
  public void print() {
    TerminalUI.move(getX(), getY());
    TerminalUI.print(emptyBar);

    TerminalUI.move(getX(), getY() + 1);
    TerminalUI.print(EMPTY_COL);

    String toPrint = text;
    int textWidth = getWidth() - 2;

    if (toPrint.length() < textWidth - 1) {
      toPrint = toPrint + new String(new char[textWidth - toPrint.length()]).replace('\0', ' ');
    } else if (toPrint.length() == textWidth - 1) {
      toPrint = toPrint.substring(0, textWidth - 1) + ' ';
    } else {
      toPrint = toPrint.substring(toPrint.length() - textWidth + 1) + ' ';
    }

    TerminalUI.print(toPrint);
    TerminalUI.print(EMPTY_COL);

    TerminalUI.move(getX(), getY() + 2);
    TerminalUI.print(emptyBar);
  }

  @Override
  public void size() {
    emptyBar = REVERSE + new String(new char[getWidth()]).replace('\0', ' ') + RESET;
  }
}
