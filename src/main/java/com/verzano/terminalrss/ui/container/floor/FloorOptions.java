package com.verzano.terminalrss.ui.container.floor;

import com.verzano.terminalrss.ui.container.ContainerOptions;
import com.verzano.terminalrss.ui.metrics.Point;
import com.verzano.terminalrss.ui.metrics.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class FloorOptions extends ContainerOptions {
  private Size size;
  private Point location;
}
