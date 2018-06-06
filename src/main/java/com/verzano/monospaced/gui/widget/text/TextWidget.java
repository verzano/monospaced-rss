package com.verzano.monospaced.gui.widget.text;

import com.verzano.monospaced.gui.MonospacedGui;
import com.verzano.monospaced.gui.ansi.AnsiFormat;
import com.verzano.monospaced.gui.ansi.Attribute;
import com.verzano.monospaced.gui.constant.Orientation;
import com.verzano.monospaced.gui.constant.Position;
import com.verzano.monospaced.gui.util.PrintUtils;
import com.verzano.monospaced.gui.widget.Widget;

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
        MonospacedGui.move(getContentX(), getContentY());
        MonospacedGui.print(PrintUtils.getRowForText(text, textPosition, getAnsiFormatPrefix(), getWidth()));
        for(int i = 1; i < getContentHeight(); i++) {
          MonospacedGui.move(getContentX(), getContentY() + i);
          MonospacedGui.print(getEmptyContentRow());
        }
        break;
      case LEFT:
      case CENTER:
      case RIGHT:
        int middleRow = getContentHeight()/2;
        for(int i = 0; i < getContentHeight(); i++) {
          MonospacedGui.move(getContentX(), getContentY() + i);
          if(i == middleRow) {
            MonospacedGui.print(PrintUtils.getRowForText(text, textPosition, getAnsiFormatPrefix(), getWidth()));
          } else {
            MonospacedGui.print(getEmptyContentRow());
          }
        }
        break;
      case BOTTOM_LEFT:
      case BOTTOM:
      case BOTTOM_RIGHT:
        MonospacedGui.move(getContentX(), getContentY());
        for(int i = 1; i < getContentHeight(); i++) {
          MonospacedGui.print(getEmptyContentRow());
          MonospacedGui.move(getContentX(), getContentY() + i);
        }
        MonospacedGui.print(PrintUtils.getRowForText(text, textPosition, getAnsiFormatPrefix(), getWidth()));
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
        MonospacedGui.move(getContentX(), getContentY());
        String toPrint = text;
        for(int row = 0; row < getContentHeight(); row++) {
          MonospacedGui.move(getContentX(), getContentY() + row);
          if(row < toPrint.length()) {
            MonospacedGui.print(getAnsiFormatPrefix() + toPrint.charAt(row) + AnsiFormat.NORMAL.getFormatString());
          } else {
            MonospacedGui.print(getAnsiFormatPrefix() + " " + AnsiFormat.NORMAL.getFormatString());
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
