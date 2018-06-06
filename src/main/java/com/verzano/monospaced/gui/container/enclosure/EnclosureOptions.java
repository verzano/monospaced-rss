package com.verzano.monospaced.gui.container.enclosure;

import com.verzano.monospaced.gui.constant.CardinalDirection;
import com.verzano.monospaced.gui.container.ContainerOptions;

public class EnclosureOptions extends ContainerOptions {
  private CardinalDirection position;

  public EnclosureOptions(CardinalDirection position) {
    this.position = position;
  }

  public CardinalDirection getPosition() {
    return position;
  }

  public void setPosition(CardinalDirection position) {
    this.position = position;
  }
}
