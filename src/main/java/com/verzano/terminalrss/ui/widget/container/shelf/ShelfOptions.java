package com.verzano.terminalrss.ui.widget.container.shelf;

import com.verzano.terminalrss.ui.metrics.Size;
import com.verzano.terminalrss.ui.widget.container.ContainerOptions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class ShelfOptions extends ContainerOptions {
  @Getter @Setter
  private Size size;
}
