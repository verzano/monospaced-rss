package com.verzano.terminalrss.ui.container.enclosure;

import com.verzano.terminalrss.ui.constants.CardinalDirection;
import com.verzano.terminalrss.ui.container.ContainerOptions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class EnclosureOptions extends ContainerOptions{
  private CardinalDirection position;
}
