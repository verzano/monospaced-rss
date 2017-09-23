package com.verzano.terminalui.container.floor;

import com.verzano.terminalui.container.ContainerOptions;
import com.verzano.terminalui.metric.Point;
import com.verzano.terminalui.metric.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FloorOptions extends ContainerOptions {
  private Size size;
  private Point location;
}
