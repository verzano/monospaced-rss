package com.verzano.terminalrss.ui.widget;

import com.verzano.terminalrss.ui.metrics.Location;
import com.verzano.terminalrss.ui.metrics.Size;
import com.verzano.terminalrss.ui.widget.constants.Direction;

import java.util.LinkedList;
import java.util.List;

import static com.verzano.terminalrss.ui.metrics.Size.MATCH_TERMINAL;

// TODO make this abstract in some way so that other styles of arranging can be done
// TODO eventually have the size cut off or wrap the contained widgets
public class ContainerWidget extends TerminalWidget {
  private List<TerminalWidget> widgetStack = new LinkedList<>();
  private Direction direction;

  public ContainerWidget(Direction direction, Location location) {
    super(new Size(MATCH_TERMINAL, MATCH_TERMINAL), location);
    this.direction = direction;
  }

  public void addWidget(TerminalWidget terminalWidget) {
    switch (direction) {
      case HORIZONTAL:
        terminalWidget.setLocation(new Location(
            getX() + widgetStack.stream().mapToInt(TerminalWidget::getWidth).sum(),
            getY()));
        break;
      case VERTICAL:
        terminalWidget.setLocation(new Location(
            getX(),
            getY() + widgetStack.stream().mapToInt(TerminalWidget::getHeight).sum()));
        break;
    }

    widgetStack.add(terminalWidget);
  }

  @Override
  public void print() {
    widgetStack.forEach(TerminalWidget::print);
  }

  @Override
  public void size() {

  }
}
