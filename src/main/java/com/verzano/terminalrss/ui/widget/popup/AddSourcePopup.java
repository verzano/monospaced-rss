package com.verzano.terminalrss.ui.widget.popup;

import com.verzano.terminalrss.ui.TerminalUI;
import com.verzano.terminalrss.ui.widget.TerminalWidget;

import java.util.stream.IntStream;

import static com.verzano.terminalrss.ui.widget.constants.Ansi.RESET;
import static com.verzano.terminalrss.ui.widget.constants.Ansi.REVERSE;
import static com.verzano.terminalrss.ui.widget.constants.Key.DELETE;

public class AddSourcePopup extends TerminalWidget {
  private String text = "";

  public AddSourcePopup() {
    super(24, 5, 1, 1);
    // All printable ASCII chars
    IntStream.range(32, 127).forEach(i -> addKeyAction(i, () -> {
      text += (char)i;
      reprint();
    }));
    addKeyAction(DELETE, () -> {
      text = text.substring(0, Math.max(0, text.length() - 1));
      reprint();
    });
    // TODO add a way of quitting

    // TODO make this less magical
    setZ(1000);
    size();
  }

  @Override
  public void print() {
    // TODO improve effeciency, duh
    String emptyRow = REVERSE + new String(new char[getWidth()]).replace('\0', ' ') + RESET;
    TerminalUI.move(getX(), getY());
    TerminalUI.print(emptyRow);

    TerminalUI.move(getX(), getY() + 1);
    TerminalUI.print(REVERSE + "    " + RESET);

    String toPrint = text;
    int textWidth = getWidth() - 8;

    if (toPrint.length() < textWidth) {
      toPrint = toPrint + new String(new char[textWidth - toPrint.length()]).replace('\0', ' ');
    } else if (toPrint.length() == textWidth) {
      toPrint = toPrint.substring(0, textWidth);
    } else {
      toPrint = toPrint.substring(toPrint.length() - textWidth + 1) + ' ';
    }

    TerminalUI.print(toPrint);
    TerminalUI.print(REVERSE + "    " + RESET);

    TerminalUI.move(getX(), getY() + 2);
    TerminalUI.print(emptyRow);
  }

  @Override
  public void size() {
    setX((TerminalUI.getWidth() - getWidth())/2 + 1);
    setY((TerminalUI.getHeight() - getHeight())/2 + 1);
  }
}
