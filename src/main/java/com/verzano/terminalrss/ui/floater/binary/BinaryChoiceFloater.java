package com.verzano.terminalrss.ui.floater.binary;

import com.verzano.terminalrss.ui.TerminalUI;
import com.verzano.terminalrss.ui.floater.Floater;
import com.verzano.terminalrss.ui.metrics.Size;
import com.verzano.terminalrss.ui.task.key.KeyTask;
import com.verzano.terminalrss.ui.widget.Widget;
import com.verzano.terminalrss.ui.widget.button.ButtonWidget;
import com.verzano.terminalrss.ui.widget.container.shelf.Shelf;
import com.verzano.terminalrss.ui.widget.container.shelf.ShelfOptions;
import lombok.Getter;

import static com.verzano.terminalrss.ui.metrics.Size.FILL_NEEDED;
import static com.verzano.terminalrss.ui.widget.constants.Key.TAB;
import static com.verzano.terminalrss.ui.widget.constants.Orientation.HORIZONTAL;
import static com.verzano.terminalrss.ui.widget.constants.Orientation.VERTICAL;
import static com.verzano.terminalrss.ui.widget.constants.Position.CENTER;

public class BinaryChoiceFloater extends Floater {
  private Shelf container;

  @Getter
  private Widget displayWidget;

  @Getter
  private final Shelf buttonContainer;
  @Getter
  private final ButtonWidget positiveButton;
  @Getter
  private final ButtonWidget negativeButton;

  public BinaryChoiceFloater(
      Widget displayWidget,
      KeyTask positiveTask,
      String positiveText,
      KeyTask negativeTask,
      String negativeText) {
    container = new Shelf(VERTICAL, 1);

    this.displayWidget = displayWidget;
    container.addWidget(displayWidget, new ShelfOptions(new Size(FILL_NEEDED, FILL_NEEDED)));

    buttonContainer = new Shelf(HORIZONTAL, 1);
    container.addWidget(buttonContainer, new ShelfOptions(new Size(FILL_NEEDED, FILL_NEEDED)));

    positiveButton = new ButtonWidget(positiveTask, positiveText, CENTER, new Size(FILL_NEEDED, FILL_NEEDED));
    buttonContainer.addWidget(positiveButton, new ShelfOptions(new Size(FILL_NEEDED, FILL_NEEDED)));

    negativeButton = new ButtonWidget(negativeTask, negativeText, CENTER, new Size(FILL_NEEDED, FILL_NEEDED));
    buttonContainer.addWidget(negativeButton, new ShelfOptions(new Size(FILL_NEEDED, FILL_NEEDED)));

    positiveButton.addKeyAction(TAB, () -> {
      negativeButton.setFocused();
      TerminalUI.reprint();
    });
    negativeButton.addKeyAction(TAB, () -> {
      this.displayWidget.setFocused();
      TerminalUI.reprint();
    });
  }

  public void setDisplayWidget(Widget displayWidget) {
    this.displayWidget = displayWidget;

    container.removeWidgets();
    container.addWidget(displayWidget, new ShelfOptions(new Size(FILL_NEEDED, FILL_NEEDED)));
    container.addWidget(buttonContainer, new ShelfOptions(new Size(FILL_NEEDED, FILL_NEEDED)));
  }

  @Override
  public Widget getBaseWidget() {
    return container;
  }
}
