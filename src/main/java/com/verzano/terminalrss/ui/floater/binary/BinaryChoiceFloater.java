package com.verzano.terminalrss.ui.floater.binary;

import com.verzano.terminalrss.ui.TerminalUI;
import com.verzano.terminalrss.ui.constants.CardinalDirection;
import com.verzano.terminalrss.ui.container.enclosure.EnclosureOptions;
import com.verzano.terminalrss.ui.container.shelf.Shelf;
import com.verzano.terminalrss.ui.container.shelf.ShelfOptions;
import com.verzano.terminalrss.ui.floater.Floater;
import com.verzano.terminalrss.ui.metrics.Size;
import com.verzano.terminalrss.ui.task.key.KeyTask;
import com.verzano.terminalrss.ui.widget.Widget;
import com.verzano.terminalrss.ui.widget.button.ButtonWidget;
import lombok.Getter;

import static com.verzano.terminalrss.ui.constants.Key.TAB;
import static com.verzano.terminalrss.ui.constants.Orientation.HORIZONTAL;
import static com.verzano.terminalrss.ui.constants.Position.CENTER;
import static com.verzano.terminalrss.ui.metrics.Size.FILL_NEEDED;

public class BinaryChoiceFloater extends Floater {
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
    this.displayWidget = displayWidget;
    buttonContainer = new Shelf(HORIZONTAL, 1);

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

    addWidget(displayWidget, new EnclosureOptions(CardinalDirection.CENTER));
    addWidget(buttonContainer, new EnclosureOptions(CardinalDirection.SOUTH));
  }
}
