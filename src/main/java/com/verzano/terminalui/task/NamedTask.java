package com.verzano.terminalui.task;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public abstract class NamedTask implements Task {
  String name;
}
