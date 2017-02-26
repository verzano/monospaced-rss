package com.verzano.terminalrss.tui.container.floor;

import com.verzano.terminalrss.tui.container.ContainerOptions;
import com.verzano.terminalrss.tui.metrics.Point;
import com.verzano.terminalrss.tui.metrics.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class FloorOptions extends ContainerOptions {
  private Size size;
  private Point location;
}
