package com.verzano.terminalui.container.enclosure;

import com.verzano.terminalui.constant.CardinalDirection;
import com.verzano.terminalui.container.ContainerOptions;

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
