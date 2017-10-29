package com.verzano.terminalui.floater.binary;

import com.verzano.terminalui.TerminalUi;
import com.verzano.terminalui.constant.CardinalDirection;
import com.verzano.terminalui.constant.Key;
import com.verzano.terminalui.constant.Orientation;
import com.verzano.terminalui.constant.Position;
import com.verzano.terminalui.container.enclosure.EnclosureOptions;
import com.verzano.terminalui.container.shelf.Shelf;
import com.verzano.terminalui.container.shelf.ShelfOptions;
import com.verzano.terminalui.floater.Floater;
import com.verzano.terminalui.metric.Size;
import com.verzano.terminalui.metric.Spacing;
import com.verzano.terminalui.task.NamedTask;
import com.verzano.terminalui.widget.Widget;
import com.verzano.terminalui.widget.button.ButtonWidget;

public class BinaryChoiceFloater extends Floater {
  private ButtonWidget positiveButton;
  private ButtonWidget negativeButton;
  private Widget displayWidget;
  private boolean positiveSelected;

  public BinaryChoiceFloater(Widget displayWidget, String positiveText, String negativeText) {
    setDisplayWidget(displayWidget);

    Shelf buttonContainer = new Shelf(Orientation.HORIZONTAL, 1);
    buttonContainer.setPadding(new Spacing(1));

    positiveButton = new ButtonWidget(new NamedTask(positiveText) {
      @Override
      public void fire() {
        positiveSelected = true;
        dispose();
      }
    }, Position.CENTER);
    buttonContainer.addWidget(positiveButton, new ShelfOptions(new Size(Size.FILL_NEEDED, Size.FILL_NEEDED)));

    negativeButton = new ButtonWidget(new NamedTask(negativeText) {
      @Override
      public void fire() {
        positiveSelected = false;
        dispose();
      }
    }, Position.CENTER);
    buttonContainer.addWidget(negativeButton, new ShelfOptions(new Size(Size.FILL_NEEDED, Size.FILL_NEEDED)));

    positiveButton.addKeyAction(Key.TAB, () -> {
      negativeButton.setFocused();
      TerminalUi.reprint();
    });
    negativeButton.addKeyAction(Key.TAB, () -> {
      getDisplayWidget().setFocused();
      TerminalUi.reprint();
    });

    addWidget(buttonContainer, new EnclosureOptions(CardinalDirection.SOUTH));
  }

  public Widget getDisplayWidget() {
    return displayWidget;
  }

  public ButtonWidget getNegativeButton() {
    return negativeButton;
  }

  public ButtonWidget getPositiveButton() {
    return positiveButton;
  }

  public void setDisplayWidget(Widget displayWidget) {
    this.displayWidget = displayWidget;
    addWidget(displayWidget, new EnclosureOptions(CardinalDirection.CENTER));
  }

  public boolean isPositiveSelected() {
    return positiveSelected;
  }

  @Override
  public void setFocused() {
    displayWidget.setFocused();
  }
}
