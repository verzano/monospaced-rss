package com.verzano.terminalui.container.shelf;

import com.verzano.terminalui.container.ContainerOptions;
import com.verzano.terminalui.metric.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ShelfOptions extends ContainerOptions {
  private Size size;
}
