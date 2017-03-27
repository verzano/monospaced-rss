package com.verzano.terminalrss.tui.container.shelf;

import com.verzano.terminalrss.tui.container.ContainerOptions;
import com.verzano.terminalrss.tui.metrics.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class ShelfOptions extends ContainerOptions {

  @Getter
  @Setter
  private Size size;
}
