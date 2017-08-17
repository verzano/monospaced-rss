package com.verzano.terminalrss.tui.widget.text;

import com.verzano.terminalrss.tui.ansi.AnsiFormat;
import com.verzano.terminalrss.tui.constants.Orientation;
import com.verzano.terminalrss.tui.constants.Position;
import com.verzano.terminalrss.tui.container.Container;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
class TextWidgetBuilder {
  private String text = "";
  private Orientation orientation = Orientation.HORIZONTAL;
  private Position textPosition = Position.CENTER_LEFT;
  private Container container = Container.NULL_CONTAINER;
  private AnsiFormat focusedFormat = AnsiFormat.NORMAL;
  private AnsiFormat unfocusedFormat = AnsiFormat.NORMAL;

  TextWidget build() {
    TextWidget textWidget = new TextWidget(text, orientation, textPosition);
    textWidget.setContainer(container);
    textWidget.setFocusedFormat(focusedFormat);
    textWidget.setUnfocusedFormat(unfocusedFormat);
    return textWidget;
  }

  TextWidgetBuilder text(String text) {
    this.text = text;
    return this;
  }

  TextWidgetBuilder orientation(Orientation orientation) {
    this.orientation = orientation;
    return this;
  }

  TextWidgetBuilder textPosition(Position textPosition) {
    this.textPosition = textPosition;
    return this;
  }

  TextWidgetBuilder container(Container container) {
    this.container = container;
    return this;
  }

  TextWidgetBuilder focusedFormat(AnsiFormat focusedFormat) {
    this.focusedFormat = focusedFormat;
    return this;
  }

  TextWidgetBuilder unfocusedFormat(AnsiFormat unfocusedFormat) {
    this.unfocusedFormat = unfocusedFormat;
    return this;
  }
}