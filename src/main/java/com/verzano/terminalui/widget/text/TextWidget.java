package com.verzano.terminalui.widget.text;

import com.verzano.terminalui.TerminalUi;
import com.verzano.terminalui.ansi.AnsiFormat;
import com.verzano.terminalui.ansi.Attribute;
import com.verzano.terminalui.constant.Orientation;
import com.verzano.terminalui.constant.Position;
import com.verzano.terminalui.util.PrintUtils;
import com.verzano.terminalui.widget.Widget;

public class TextWidget extends Widget {
  private String text = "";
  private Orientation orientation = Orientation.HORIZONTAL;
  private Position textPosition = Position.LEFT;

  public TextWidget() {}

  public TextWidget(String text, Orientation orientation, Position textPosition) {
    this.text = text;
    this.textPosition = textPosition;
    this.orientation = orientation;

    getFocusedFormat().setAttributes(Attribute.INVERSE_ON);
    getUnfocusedFormat().setAttributes(Attribute.INVERSE_ON);
  }

  public Orientation getOrientation() {
    return orientation;
  }

  public void setOrientation(Orientation orientation) {
    this.orientation = orientation;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public Position getTextPosition() {
    return textPosition;
  }

  public void setTextPosition(Position textPosition) {
    this.textPosition = textPosition;
  }

  private void printHorizontal() {
    switch(textPosition) {
      case TOP_LEFT:
      case TOP:
      case TOP_RIGHT:
        TerminalUi.move(getContentX(), getContentY());
        TerminalUi.print(PrintUtils.getRowForText(text, textPosition, getAnsiFormatPrefix(), getWidth()));
        for(int i = 1; i < getContentHeight(); i++) {
          TerminalUi.move(getContentX(), getContentY() + i);
          TerminalUi.print(getEmptyContentRow());
        }
        break;
      case LEFT:
      case CENTER:
      case RIGHT:
        int middleRow = getContentHeight()/2;
        for(int i = 0; i < getContentHeight(); i++) {
          TerminalUi.move(getContentX(), getContentY() + i);
          if(i == middleRow) {
            TerminalUi.print(PrintUtils.getRowForText(text, textPosition, getAnsiFormatPrefix(), getWidth()));
          } else {
            TerminalUi.print(getEmptyContentRow());
          }
        }
        break;
      case BOTTOM_LEFT:
      case BOTTOM:
      case BOTTOM_RIGHT:
        TerminalUi.move(getContentX(), getContentY());
        for(int i = 1; i < getContentHeight(); i++) {
          TerminalUi.print(getEmptyContentRow());
          TerminalUi.move(getContentX(), getContentY() + i);
        }
        TerminalUi.print(PrintUtils.getRowForText(text, textPosition, getAnsiFormatPrefix(), getWidth()));
        break;
      default:
        break;
    }
  }

  @Override
  public int getNeededContentHeight() {
    int height = 0;
    switch(orientation) {
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
  public int getNeededContentWidth() {
    int width = 0;
    switch(orientation) {
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
  public void printContent() {
    switch(orientation) {
      // TODO make vertical printContent correctly
      case VERTICAL:
        TerminalUi.move(getContentX(), getContentY());
        String toPrint = text;
        for(int row = 0; row < getContentHeight(); row++) {
          TerminalUi.move(getContentX(), getContentY() + row);
          if(row < toPrint.length()) {
            TerminalUi.print(getAnsiFormatPrefix() + toPrint.charAt(row) + AnsiFormat.NORMAL.getFormatString());
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
