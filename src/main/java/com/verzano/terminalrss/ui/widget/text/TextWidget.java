package com.verzano.terminalrss.ui.widget.text;

import com.verzano.terminalrss.ui.TerminalUI;
import com.verzano.terminalrss.ui.metrics.Size;
import com.verzano.terminalrss.ui.widget.Widget;
import com.verzano.terminalrss.ui.widget.ansi.AnsiTextFormat;
import com.verzano.terminalrss.ui.widget.ansi.Attribute;
import com.verzano.terminalrss.ui.widget.constants.Orientation;
import com.verzano.terminalrss.ui.widget.constants.Position;
import lombok.Getter;
import lombok.Setter;

// TODO allow an N wide vertical bar or M high horizontal bar
// TODO       OR
// TODO lock width and height based on the size of the terminal
// TODO allow positioning of text left/right or top/bottom
public class TextWidget extends Widget {
  @Getter @Setter
  private String text;

  @Getter @Setter
  private Orientation orientation;

  @Getter @Setter
  private Position textPosition;

  private String emptyRow;

  private static String NORMAL = AnsiTextFormat.build(Attribute.NORMAL);

  public TextWidget(String text, Orientation orientation, Position textPosition, Size size) {
    super(size);
    this.text = text;
    this.textPosition = textPosition;
    this.orientation = orientation;

    setFocusedAttribute(Attribute.INVERSE_ON);
    setUnfocusedAttribute(Attribute.INVERSE_ON);

    emptyRow = new String(new char[getWidth()]).replace('\0', ' ');
  }

  protected String getTextRow() {
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

    return getTextFormattingPrefix() + textRow + NORMAL;
  }

  private void printHorizontal() {
    switch (textPosition) {
      case TOP_LEFT:
      case TOP_CENTER:
      case TOP_RIGHT:
        TerminalUI.move(getX(), getY());
        TerminalUI.print(getTextRow());
        for (int i = 1; i < getHeight(); i++) {
          TerminalUI.move(getX(), getY() + i);
          TerminalUI.print(getEmptyRow());
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
            TerminalUI.print(getEmptyRow());
          }
        }
        break;
      case BOTTOM_LEFT:
      case BOTTOM_CENTER:
      case BOTTOM_RIGHT:
        TerminalUI.move(getX(), getY());
        for (int i = 1; i < getHeight(); i++) {
          TerminalUI.print(getEmptyRow());
          TerminalUI.move(getX(), getY() + i);
        }
        TerminalUI.print(getTextRow());
        break;
    }
  }

  public String getEmptyRow() {
    return getTextFormattingPrefix() + emptyRow + NORMAL;
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
    }
    return height;
  }

  @Override
  public void print() {
    TerminalUI.move(getX(), getY());
    String toPrint = text;

    switch (orientation) {
      // TODO make vertical print correctly
      case VERTICAL:
        for (int row = 0; row < getHeight(); row++) {
          TerminalUI.move(getX(), getY() + row);
          if (row < toPrint.length()) {
            TerminalUI.print(getTextFormattingPrefix() + toPrint.charAt(row) + NORMAL);
          } else {
            TerminalUI.print(getTextFormattingPrefix() + " " + NORMAL);
          }
        }
        break;
      case HORIZONTAL:
        printHorizontal();
        break;
    }
  }

  @Override
  public void size() {
    emptyRow = new String(new char[getWidth()]).replace('\0', ' ');
  }
}