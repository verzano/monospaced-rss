package com.verzano.terminalrss.ui.widget.text;

import com.verzano.terminalrss.ui.TerminalUI;
import com.verzano.terminalrss.ui.metrics.Size;
import com.verzano.terminalrss.ui.widget.Widget;
import com.verzano.terminalrss.ui.widget.ansi.Attribute;
import com.verzano.terminalrss.ui.widget.constants.Orientation;
import com.verzano.terminalrss.ui.widget.constants.Position;
import lombok.Getter;
import lombok.Setter;

public class TextWidget extends Widget {
  @Getter @Setter
  private String text;

  @Getter @Setter
  private Orientation orientation;

  @Getter @Setter
  private Position textPosition;

  public TextWidget(String text, Orientation orientation, Position textPosition, Size size) {
    super(size);
    this.text = text;
    this.textPosition = textPosition;
    this.orientation = orientation;

    setFocusedAttribute(Attribute.INVERSE_ON);
    setUnfocusedAttribute(Attribute.INVERSE_ON);
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
        TerminalUI.move(getContentX(), getContentY());
        TerminalUI.print(getTextRow());
        for (int i = 1; i < getContentHeight(); i++) {
          TerminalUI.move(getContentX(), getContentY() + i);
          TerminalUI.print(getEmptyContentRow());
        }
        break;
      case CENTER_LEFT:
      case CENTER:
      case CENTER_RIGHT:
        int middleRow = getHeight()/2;
        for (int i = 0; i < getHeight(); i++) {
          TerminalUI.move(getContentX(), getContentY() + i);
          if (i == middleRow) {
            TerminalUI.print(getTextRow());
          } else {
            TerminalUI.print(getEmptyContentRow());
          }
        }
        break;
      case BOTTOM_LEFT:
      case BOTTOM_CENTER:
      case BOTTOM_RIGHT:
        TerminalUI.move(getContentX(), getContentY());
        for (int i = 1; i < getContentHeight(); i++) {
          TerminalUI.print(getEmptyContentRow());
          TerminalUI.move(getContentX(), getContentY() + i);
        }
        TerminalUI.print(getTextRow());
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
  public void printContent() {
    TerminalUI.move(getContentX(), getContentY());
    String toPrint = text;

    switch (orientation) {
      // TODO make vertical printContent correctly
      case VERTICAL:
        for (int row = 0; row < getContentHeight(); row++) {
          TerminalUI.move(getContentX(), getContentY() + row);
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
}
