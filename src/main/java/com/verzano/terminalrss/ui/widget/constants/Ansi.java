package com.verzano.terminalrss.ui.widget.constants;

public class Ansi {
  private Ansi() { }

  public static final char ESC = '\u001b';

  // Movement
  public static final String SET_POSITION = ESC + "[%d;%dH";
  public static final String MOVE_UP = ESC + "[%dA";
  public static final String MOVE_DOWN = ESC + "[%dB";
  public static final String MOVE_RIGHT = ESC + "[%C";
  public static final String MOVE_LEFT = ESC + "[%D";

  // TODO convert this into a builder and enums...
  public static final String NORMAL = ESC + "[0m";
  public static final String BOLD = ESC + "[1m";
  public static final String REST_BOLD = ESC + "[21m";
  public static final String DIM = ESC + "[2m";
  public static final String REST_DIM= ESC + "[22m";
  public static final String UNDERLINE = ESC + "[4m";
  public static final String RESET_UNDERLINE = ESC + "[24m";
  public static final String BLINK = ESC + "[5m";
  public static final String RESET_BLINK = ESC + "[25m";
  public static final String REVERSE = ESC + "[7m";
  public static final String RESET_REVERSE = ESC + "[27m";
  public static final String UNDISPLAYED = ESC + "[8m";
  public static final String RESET_UNDISPLAYED = ESC + "[28m";
  public static final String RESET = ESC + "[m";
}
