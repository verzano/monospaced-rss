package com.verzano.terminalrss.tui.floater.binary;

import com.verzano.terminalrss.tui.TerminalUi;
import com.verzano.terminalrss.tui.constant.CardinalDirection;
import com.verzano.terminalrss.tui.container.enclosure.EnclosureOptions;
import com.verzano.terminalrss.tui.container.shelf.Shelf;
import com.verzano.terminalrss.tui.container.shelf.ShelfOptions;
import com.verzano.terminalrss.tui.floater.Floater;
import com.verzano.terminalrss.tui.metric.Margins;
import com.verzano.terminalrss.tui.metric.Size;
import com.verzano.terminalrss.tui.task.key.KeyTask;
import com.verzano.terminalrss.tui.widget.Widget;
import com.verzano.terminalrss.tui.widget.button.ButtonWidget;
import lombok.Getter;

import static com.verzano.terminalrss.tui.constant.Key.TAB;
import static com.verzano.terminalrss.tui.constant.Orientation.HORIZONTAL;
import static com.verzano.terminalrss.tui.constant.Position.CENTER;
import static com.verzano.terminalrss.tui.metric.Size.FILL_NEEDED;

public class BinaryChoiceFloater extends Floater {
  private final Shelf buttonContainer = new Shelf(HORIZONTAL, 1);

  @Getter
  private ButtonWidget positiveButton;

  @Getter
  private ButtonWidget negativeButton;

  @Getter
  private Widget displayWidget;

  public BinaryChoiceFloater(
      Widget displayWidget,
      KeyTask positiveTask,
      String positiveText,
      KeyTask negativeTask,
      String negativeText) {
    this.displayWidget = displayWidget;

    buttonContainer.setMargins(new Margins(1));

    positiveButton = new ButtonWidget(positiveTask, positiveText, CENTER);
    buttonContainer.addWidget(positiveButton, new ShelfOptions(new Size(FILL_NEEDED, FILL_NEEDED)));

    negativeButton = new ButtonWidget(negativeTask, negativeText, CENTER);
    buttonContainer.addWidget(negativeButton, new ShelfOptions(new Size(FILL_NEEDED, FILL_NEEDED)));

    positiveButton.addKeyAction(TAB, () -> {
      negativeButton.setFocused();
      TerminalUi.reprint();
    });
    negativeButton.addKeyAction(TAB, () -> {
      getDisplayWidget().setFocused();
      TerminalUi.reprint();
    });

    addWidget(this.displayWidget, new EnclosureOptions(CardinalDirection.CENTER));
    addWidget(buttonContainer, new EnclosureOptions(CardinalDirection.SOUTH));
  }

  public void setDisplayWidget(Widget displayWidget) {
    this.displayWidget = displayWidget;
    addWidget(displayWidget, new EnclosureOptions(CardinalDirection.CENTER));
  }
}
