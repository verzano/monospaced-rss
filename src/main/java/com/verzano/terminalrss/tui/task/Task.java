package com.verzano.terminalrss.tui.task;

public interface Task {
  Task NULL_TASK = () -> {};
  void fire();
}
