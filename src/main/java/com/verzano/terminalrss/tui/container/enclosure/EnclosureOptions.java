package com.verzano.terminalrss.tui.container.enclosure;

import com.verzano.terminalrss.tui.constants.CardinalDirection;
import com.verzano.terminalrss.tui.container.ContainerOptions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class EnclosureOptions extends ContainerOptions{
  private CardinalDirection position;
}
