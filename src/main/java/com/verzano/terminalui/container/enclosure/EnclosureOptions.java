package com.verzano.terminalui.container.enclosure;

import com.verzano.terminalui.constant.CardinalDirection;
import com.verzano.terminalui.container.ContainerOptions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EnclosureOptions extends ContainerOptions {
  private CardinalDirection position;
}
