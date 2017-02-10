package com.verzano.terminalrss.ui.widget.floating.binary;

import com.verzano.terminalrss.ui.TerminalUI;
import com.verzano.terminalrss.ui.metrics.Location;
import com.verzano.terminalrss.ui.metrics.Size;
import com.verzano.terminalrss.ui.task.key.KeyTask;
import com.verzano.terminalrss.ui.widget.Widget;
import com.verzano.terminalrss.ui.widget.button.ButtonWidget;
import com.verzano.terminalrss.ui.widget.container.box.BoxContainer;
import com.verzano.terminalrss.ui.widget.floating.FloatingWidget;
import lombok.Getter;

import static com.verzano.terminalrss.ui.metrics.Size.FILL_NEEDED;
import static com.verzano.terminalrss.ui.metrics.Size.FILL_PARENT;
import static com.verzano.terminalrss.ui.widget.constants.Ansi.RESET_REVERSE;
import static com.verzano.terminalrss.ui.widget.constants.Ansi.REVERSE;
import static com.verzano.terminalrss.ui.widget.constants.Key.TAB;
import static com.verzano.terminalrss.ui.widget.constants.Orientation.HORIZONTAL;
import static com.verzano.terminalrss.ui.widget.constants.Position.CENTER;

public class BinaryChoiceFloater extends FloatingWidget {
  @Getter
  private Widget displayWidget;

  @Getter
  private final BoxContainer buttonContainer;
  @Getter
  private final ButtonWidget positiveButton;
  @Getter
  private final ButtonWidget negativeButton;

  private String emptyRow;

  public BinaryChoiceFloater(
      Widget displayWidget,
      KeyTask positiveTask,
      String positiveText,
      KeyTask negativeTask,
      String negativeText,
      Size size) {
    super(size);
    this.displayWidget = displayWidget;
    displayWidget.setParent(this);

    buttonContainer = new BoxContainer(HORIZONTAL, 1, new Size(FILL_PARENT, FILL_NEEDED));
    buttonContainer.setParent(this);
    buttonContainer.setY(getY() + displayWidget.getHeight());

    positiveButton = new ButtonWidget(positiveTask, positiveText, CENTER, new Size(FILL_NEEDED, FILL_NEEDED));
    buttonContainer.addWidget(positiveButton);

    negativeButton = new ButtonWidget(negativeTask, negativeText, CENTER, new Size(FILL_NEEDED, FILL_NEEDED));
    buttonContainer.addWidget(negativeButton);

    positiveButton.addKeyAction(TAB, () -> {
      negativeButton.setFocused();
      TerminalUI.reprint();
    });
    negativeButton.addKeyAction(TAB, () -> {
      this.displayWidget.setFocused();
      TerminalUI.reprint();
    });

    emptyRow = REVERSE + new String(new char[getWidth()]).replace('\0', ' ') + RESET_REVERSE;
  }

  public void setDisplayWidget(Widget displayWidget) {
    this.displayWidget = displayWidget;
    displayWidget.setParent(this);
    buttonContainer.setY(getY() + displayWidget.getHeight() + 1);
    emptyRow = REVERSE + new String(new char[getWidth()]).replace('\0', ' ') + RESET_REVERSE;
  }

  @Override
  public void setLocation(Location location) {
    setX(location.getX());
    setY(location.getY());
  }

  @Override
  public void setX(int x) {
    int delta = x - getX();
    displayWidget.setX(displayWidget.getX() + delta);
    buttonContainer.setX(buttonContainer.getX() + delta);
    super.setX(x);
  }

  @Override
  public void setY(int y) {
    int delta = y - getY();
    displayWidget.setY(displayWidget.getY() + delta);
    buttonContainer.setY(buttonContainer.getY() + delta);
    super.setY(y);
  }

  @Override
  public int getNeededWidth() {
    return Math.max(displayWidget.getNeededWidth(), buttonContainer.getNeededWidth());
  }

  @Override
  public int getNeededHeight() {
    return displayWidget.getNeededHeight() + buttonContainer.getNeededHeight() + 1;
  }

  @Override
  public void print() {
    for (int row = 0; row < getHeight(); row++) {
      TerminalUI.move(getX(), getY() + row);
      TerminalUI.print(emptyRow);
    }
    displayWidget.print();

    TerminalUI.move(getX(), getY() + displayWidget.getHeight() + 1);
    TerminalUI.print(emptyRow);
    buttonContainer.print();
  }
}
