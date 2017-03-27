package com.verzano.terminalrss.tui.ansi;

public class Ansi {

  public static final char ESC = '\u001b';
  // Movement
  public static final String SET_POSITION = ESC + "[%d;%dH";
  public static final String MOVE_UP = ESC + "[%dA";
  public static final String MOVE_DOWN = ESC + "[%dB";
  public static final String MOVE_RIGHT = ESC + "[%C";
  public static final String MOVE_LEFT = ESC + "[%D";

  private Ansi() {}
}
