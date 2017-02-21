package com.verzano.terminalrss.ui.container.shelf;

import com.verzano.terminalrss.ui.container.ContainerOptions;
import com.verzano.terminalrss.ui.metrics.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class ShelfOptions extends ContainerOptions {
  @Getter @Setter
  private Size size;
}
