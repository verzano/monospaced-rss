package com.verzano.terminalrss.tui.floater.binary;

import static com.verzano.terminalrss.tui.constant.Key.TAB;
import static com.verzano.terminalrss.tui.constant.Orientation.HORIZONTAL;
import static com.verzano.terminalrss.tui.constant.Position.CENTER;
import static com.verzano.terminalrss.tui.metric.Size.FILL_NEEDED;

import com.verzano.terminalrss.tui.TerminalUi;
import com.verzano.terminalrss.tui.constant.CardinalDirection;
import com.verzano.terminalrss.tui.container.enclosure.EnclosureOptions;
import com.verzano.terminalrss.tui.container.shelf.Shelf;
import com.verzano.terminalrss.tui.container.shelf.ShelfOptions;
import com.verzano.terminalrss.tui.floater.Floater;
import com.verzano.terminalrss.tui.metric.Size;
import com.verzano.terminalrss.tui.metric.Spacing;
import com.verzano.terminalrss.tui.task.NamedTask;
import com.verzano.terminalrss.tui.widget.Widget;
import com.verzano.terminalrss.tui.widget.button.ButtonWidget;
import lombok.Getter;

public class BinaryChoiceFloater extends Floater {
  @Getter
  private ButtonWidget positiveButton;

  @Getter
  private ButtonWidget negativeButton;

  @Getter
  private Widget displayWidget;

  @Getter
  private boolean positiveSelected;

  public BinaryChoiceFloater(Widget displayWidget, String positiveText, String negativeText) {
    setDisplayWidget(displayWidget);

    Shelf buttonContainer = new Shelf(HORIZONTAL, 1);
    buttonContainer.setPadding(new Spacing(1));

    positiveButton = new ButtonWidget(new NamedTask(positiveText) {
      @Override
      public void fire() {
        positiveSelected = true;
        dispose();
      }
    }, CENTER);
    buttonContainer.addWidget(positiveButton, new ShelfOptions(new Size(FILL_NEEDED, FILL_NEEDED)));

    negativeButton = new ButtonWidget(new NamedTask(negativeText) {
      @Override
      public void fire() {
        positiveSelected = false;
        dispose();
      }
    }, CENTER);
    buttonContainer.addWidget(negativeButton, new ShelfOptions(new Size(FILL_NEEDED, FILL_NEEDED)));

    positiveButton.addKeyAction(TAB, () -> {
      negativeButton.setFocused();
      TerminalUi.reprint();
    });
    negativeButton.addKeyAction(TAB, () -> {
      getDisplayWidget().setFocused();
      TerminalUi.reprint();
    });

    addWidget(buttonContainer, new EnclosureOptions(CardinalDirection.SOUTH));
  }

  public void setDisplayWidget(Widget displayWidget) {
    this.displayWidget = displayWidget;
    addWidget(displayWidget, new EnclosureOptions(CardinalDirection.CENTER));
  }

  @Override
  public void setFocused() {
    displayWidget.setFocused();
  }
}
