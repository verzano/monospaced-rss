package com.verzano.terminalrss.ui.widget.button;

import com.verzano.terminalrss.ui.TerminalUI;
import com.verzano.terminalrss.ui.metrics.Size;
import com.verzano.terminalrss.ui.task.key.KeyTask;
import com.verzano.terminalrss.ui.widget.Widget;
import com.verzano.terminalrss.ui.widget.constants.Position;
import lombok.Getter;
import lombok.Setter;

import static com.verzano.terminalrss.ui.widget.constants.Ansi.RESET;
import static com.verzano.terminalrss.ui.widget.constants.Ansi.REVERSE;
import static com.verzano.terminalrss.ui.widget.constants.Key.ENTER;

// TODO make this more efficient
public class ButtonWidget extends Widget {
  @Getter @Setter
  private String text;

  @Getter @Setter
  private Position textPosition;

  private String emptyRow;

  public ButtonWidget(KeyTask onPress, String text, Position textPosition, Size size) {
    super(size);
    this.text = text;
    this.textPosition = textPosition;
    addKeyAction(ENTER, onPress);

    emptyRow = new String(new char[getWidth()]).replace('\0', ' ');
  }

  private String getTextRow() {
    String textRow = text;

    if (textRow.length() != getWidth()) {
      switch (textPosition) {
        case TOP_LEFT:
        case CENTER_LEFT:
        case BOTTOM_LEFT:
          if (textRow.length() > getWidth()) {
            textRow = textRow.substring(0, getWidth());
          } else {
            textRow += new String(new char[getWidth() - textRow.length()]).replace('\0', ' ');
          }
          break;
        case TOP_CENTER:
        case CENTER:
        case BOTTOM_CENTER:
          if (textRow.length() > getWidth()) {
            double halfExtra = (textRow.length() - getWidth())/2D;

            textRow = textRow.substring((int)halfExtra, textRow.length() - (int)Math.ceil(halfExtra));
          } else {
            double halfRemaining = (textRow.length() - getWidth())/2D;
            textRow = new String(new char[(int)Math.ceil(halfRemaining)]).replace('\0', ' ')
                + textRow
                + new String(new char[(int)halfRemaining]).replace('\0', ' ');
          }
          break;
        case TOP_RIGHT:
        case CENTER_RIGHT:
        case BOTTOM_RIGHT:
          if (textRow.length() > getWidth()) {
            textRow = textRow.substring(textRow.length() - getWidth(), textRow.length());
          } else {
            textRow = new String(new char[getWidth() - textRow.length()]).replace('\0', ' ') + textRow;
          }
          break;
      }
    }

    if (isFocused()) {
      textRow = REVERSE + textRow + RESET;
    }
    return textRow;
  }

  private String getEmptyRow() {
    String emptyRow = this.emptyRow;
    if (isFocused()) {
      emptyRow = REVERSE + emptyRow + RESET;
    }

    return emptyRow;
  }

  @Override
  public int getNeededWidth() {
    return text.length();
  }

  @Override
  public int getNeededHeight() {
    return 1;
  }

  @Override
  public void print() {
    String emptyRow = getEmptyRow();

    switch (textPosition) {
      case TOP_LEFT:
      case TOP_CENTER:
      case TOP_RIGHT:
        TerminalUI.move(getX(), getY());
        TerminalUI.print(getTextRow());
        for (int i = 1; i < getHeight(); i++) {
          TerminalUI.move(getX(), getY() + i);
          TerminalUI.print(emptyRow);
        }
        break;
      case CENTER_LEFT:
      case CENTER:
      case CENTER_RIGHT:
        int middleRow = getHeight()/2;
        for (int i = 0; i < getHeight(); i++) {
          TerminalUI.move(getX(), getY() + i);
          if (i == middleRow) {
            TerminalUI.print(getTextRow());
          } else {
            TerminalUI.print(emptyRow);
          }
        }
        break;
      case BOTTOM_LEFT:
      case BOTTOM_CENTER:
      case BOTTOM_RIGHT:
        TerminalUI.move(getX(), getY());
        for (int i = 1; i < getHeight(); i++) {
          TerminalUI.print(emptyRow);
          TerminalUI.move(getX(), getY() + i);
        }
        TerminalUI.print(getTextRow());
        break;
    }
  }

  @Override
  public void size() {

  }
}
