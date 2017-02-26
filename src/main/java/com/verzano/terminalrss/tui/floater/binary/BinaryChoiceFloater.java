package com.verzano.terminalrss.tui.floater.binary;

import com.verzano.terminalrss.tui.TerminalUI;
import com.verzano.terminalrss.tui.constants.CardinalDirection;
import com.verzano.terminalrss.tui.container.enclosure.EnclosureOptions;
import com.verzano.terminalrss.tui.container.shelf.Shelf;
import com.verzano.terminalrss.tui.container.shelf.ShelfOptions;
import com.verzano.terminalrss.tui.floater.Floater;
import com.verzano.terminalrss.tui.metrics.Size;
import com.verzano.terminalrss.tui.task.key.KeyTask;
import com.verzano.terminalrss.tui.widget.Widget;
import com.verzano.terminalrss.tui.widget.button.ButtonWidget;
import lombok.Getter;

import static com.verzano.terminalrss.tui.constants.Key.TAB;
import static com.verzano.terminalrss.tui.constants.Orientation.HORIZONTAL;
import static com.verzano.terminalrss.tui.constants.Position.CENTER;
import static com.verzano.terminalrss.tui.metrics.Size.FILL_NEEDED;

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

    positiveButton = new ButtonWidget(positiveTask, positiveText, CENTER);
    buttonContainer.addWidget(positiveButton, new ShelfOptions(new Size(FILL_NEEDED, FILL_NEEDED)));

    negativeButton = new ButtonWidget(negativeTask, negativeText, CENTER);
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
