package com.verzano.terminalui.task;

public abstract class NamedTask implements Task {
  private String name;

  public NamedTask(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
