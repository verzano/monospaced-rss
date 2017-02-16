package com.verzano.terminalrss.ui.widget.floater.binary;

import com.verzano.terminalrss.ui.TerminalUI;
import com.verzano.terminalrss.ui.metrics.Size;
import com.verzano.terminalrss.ui.task.key.KeyTask;
import com.verzano.terminalrss.ui.widget.Widget;
import com.verzano.terminalrss.ui.widget.button.ButtonWidget;
import com.verzano.terminalrss.ui.widget.container.box.BoxContainer;
import com.verzano.terminalrss.ui.widget.floater.Floater;
import lombok.Getter;

import static com.verzano.terminalrss.ui.metrics.Size.FILL_NEEDED;
import static com.verzano.terminalrss.ui.widget.constants.Key.TAB;
import static com.verzano.terminalrss.ui.widget.constants.Orientation.HORIZONTAL;
import static com.verzano.terminalrss.ui.widget.constants.Orientation.VERTICAL;
import static com.verzano.terminalrss.ui.widget.constants.Position.CENTER;

public class BinaryChoiceFloater extends Floater {
  private BoxContainer container;

  @Getter
  private Widget displayWidget;

  @Getter
  private final BoxContainer buttonContainer;
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
    container = new BoxContainer(VERTICAL, 1, new Size(FILL_NEEDED, FILL_NEEDED));

    this.displayWidget = displayWidget;
    container.addWidget(displayWidget);

    buttonContainer = new BoxContainer(HORIZONTAL, 1, new Size(FILL_NEEDED, FILL_NEEDED));
    container.addWidget(buttonContainer);
    buttonContainer.setY(displayWidget.getHeight());

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
  }

  public void setDisplayWidget(Widget displayWidget) {
    this.displayWidget = displayWidget;

    container.removeWidgets();
    container.addWidget(displayWidget);
    container.addWidget(buttonContainer);
  }

  @Override
  public Widget getBaseWidget() {
    return container;
  }
}
