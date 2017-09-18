package com.verzano.terminalrss.tui.widget.text;

import com.verzano.terminalrss.tui.TerminalUi;
import com.verzano.terminalrss.tui.ansi.AnsiFormat;
import com.verzano.terminalrss.tui.ansi.Attribute;
import com.verzano.terminalrss.tui.constant.Orientation;
import com.verzano.terminalrss.tui.constant.Position;
import com.verzano.terminalrss.tui.widget.Widget;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class TextWidget extends Widget {
  @Getter
  @Setter
  private String text = "";

  @Getter
  @Setter
  private Orientation orientation = Orientation.HORIZONTAL;

  @Getter
  @Setter
  private Position textPosition = Position.LEFT;

  public TextWidget(String text, Orientation orientation, Position textPosition) {
    this.text = text;
    this.textPosition = textPosition;
    this.orientation = orientation;

    getFocusedFormat().setAttributes(Attribute.INVERSE_ON);
    getUnfocusedFormat().setAttributes(Attribute.INVERSE_ON);
  }

  protected String getRowForText(String text) {
    if (text.length() != getWidth()) {
      switch (textPosition) {
        case TOP_LEFT:
        case LEFT:
        case BOTTOM_LEFT:
          if (text.length() > getWidth()) {
            text = text.substring(0, getWidth());
          } else {
            text += new String(new char[getWidth() - text.length()]).replace('\0', ' ');
          }
          break;
        case TOP:
        case CENTER:
        case BOTTOM:
          if (text.length() > getWidth()) {
            double halfExtra = (text.length() - getWidth()) / 2D;

            text = text.substring((int) halfExtra, text.length() - (int) Math.ceil(halfExtra));
          } else {
            double halfRemaining = (getWidth() - text.length()) / 2D;
            text = new String(new char[(int) Math.ceil(halfRemaining)]).replace('\0', ' ')
                + text
                + new String(new char[(int) halfRemaining]).replace('\0', ' ');
          }
          break;
        case TOP_RIGHT:
        case RIGHT:
        case BOTTOM_RIGHT:
          if (text.length() > getWidth()) {
            text = text.substring(text.length() - getWidth(), text.length());
          } else {
            text = new String(new char[getWidth() - text.length()]).replace('\0', ' ') + text;
          }
          break;
        default:
          break;

      }
    }

    return getAnsiFormatPrefix() + text + AnsiFormat.NORMAL.getFormatString();
  }

  private void printHorizontal() {
    switch (textPosition) {
      case TOP_LEFT:
      case TOP:
      case TOP_RIGHT:
        TerminalUi.move(getContentX(), getContentY());
        TerminalUi.print(getRowForText(text));
        for (int i = 1; i < getContentHeight(); i++) {
          TerminalUi.move(getContentX(), getContentY() + i);
          TerminalUi.print(getEmptyContentRow());
        }
        break;
      case LEFT:
      case CENTER:
      case RIGHT:
        int middleRow = getHeight() / 2;
        for (int i = 0; i < getHeight(); i++) {
          TerminalUi.move(getContentX(), getContentY() + i);
          if (i == middleRow) {
            TerminalUi.print(getRowForText(text));
          } else {
            TerminalUi.print(getEmptyContentRow());
          }
        }
        break;
      case BOTTOM_LEFT:
      case BOTTOM:
      case BOTTOM_RIGHT:
        TerminalUi.move(getContentX(), getContentY());
        for (int i = 1; i < getContentHeight(); i++) {
          TerminalUi.print(getEmptyContentRow());
          TerminalUi.move(getContentX(), getContentY() + i);
        }
        TerminalUi.print(getRowForText(text));
        break;
      default:
        break;
    }
  }

  @Override
  public int getNeededWidth() {
    int width = 0;
    switch (orientation) {
      case VERTICAL:
        width = 1;
        break;
      case HORIZONTAL:
        width = text.length();
        break;
      default:
        break;
    }
    return width;
  }

  @Override
  public int getNeededHeight() {
    int height = 0;
    switch (orientation) {
      case VERTICAL:
        height = text.length();
        break;
      case HORIZONTAL:
        height = 1;
        break;
      default:
        break;
    }
    return height;
  }

  @Override
  public void printContent() {
    switch (orientation) {
      // TODO make vertical printContent correctly
      case VERTICAL:
        TerminalUi.move(getContentX(), getContentY());
        String toPrint = text;
        for (int row = 0; row < getContentHeight(); row++) {
          TerminalUi.move(getContentX(), getContentY() + row);
          if (row < toPrint.length()) {
            TerminalUi.print(
                getAnsiFormatPrefix()
                    + toPrint.charAt(row)
                    + AnsiFormat.NORMAL.getFormatString());
          } else {
            TerminalUi.print(getAnsiFormatPrefix() + " " + AnsiFormat.NORMAL.getFormatString());
          }
        }
        break;
      case HORIZONTAL:
        printHorizontal();
        break;
      default:
        break;
    }
  }
}
