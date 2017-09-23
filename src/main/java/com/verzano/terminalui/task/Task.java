package com.verzano.terminalui.task;

public interface Task {
  Task NULL_TASK = () -> {};
  void fire();
}
