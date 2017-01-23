package com.verzano.terminalrss.ui.widget.text;

import com.verzano.terminalrss.ui.TerminalUI;
import com.verzano.terminalrss.ui.widget.TerminalWidget;
import lombok.Getter;
import lombok.Setter;

public class TextAreaWidget extends TerminalWidget {
  @Getter @Setter
  private String text;

  public TextAreaWidget(String text) {
    this.text = text;
  }

  @Override
  public void print() {
    int beginIndex = 0;
    int endIndex = Math.min(getWidth(), text.length());
    for (int row = 0; row < getHeight(); row++) {
      TerminalUI.move(getX(), getY() + row);

      if (beginIndex < text.length()) {
        TerminalUI.print(text.substring(beginIndex, endIndex));
        beginIndex += getWidth();
        endIndex = Math.min(endIndex + getWidth(), text.length());
      } else {
        TerminalUI.printr(" ", getWidth());
      }
    }
  }
}
