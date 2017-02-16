package com.verzano.terminalrss.ui.widget.ansi;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class AnsiTextFormat {
  private AnsiTextFormat() { }

  public static String build(Attribute... attributes) {
    return build(Foreground.NONE, Background.NONE, attributes);
  }

  public static String build(Foreground foreground, Attribute... attributes) {
    return build(foreground, Background.NONE, attributes);
  }

  public static String build(Background background, Attribute... attributes) {
    return build(Foreground.NONE, background, attributes);
  }

  public static String build(Foreground foreground) {
    return build(foreground, Background.NONE, Attribute.NONE);
  }

  public static String build(Foreground foreground, Background background) {
    return build(foreground, background, Attribute.NONE);
  }

  public static String build(Background background) {
    return build(Foreground.NONE, background, Attribute.NONE);
  }

  public static String build(Foreground foreground, Background background, Attribute... attributes) {
    Set<Attribute> cleanAttributes = Arrays.stream(attributes)
        .distinct()
        .filter(a -> a != Attribute.NONE)
        .collect(Collectors.toSet());

    String ansiCode;
    if (cleanAttributes.isEmpty()
        && foreground == Foreground.NONE
        && background == Background.NONE) {
      ansiCode = "";
    } else {
      ansiCode = Ansi.ESC + "[";
      boolean hasOne = false;
      if (!cleanAttributes.isEmpty()) {
        ansiCode += cleanAttributes.stream()
            .map(Attribute::getCode)
            .reduce((a, b) -> a + ";" + b)
            .orElseGet(() -> "0");
        hasOne = true;
      }

      if (foreground != Foreground.NONE) {
        ansiCode += (hasOne ? ";" : "") + foreground.getCode();
        hasOne = true;
      }

      if (background != Background.NONE) {
        ansiCode += (hasOne ? ";" : "") + background.getCode();
      }

      ansiCode += "m";
    }

    return ansiCode;
  }
}
