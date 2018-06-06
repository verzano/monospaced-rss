package com.verzano.monospaced.gui.ansi;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class AnsiFormat {
  public static final AnsiFormat NORMAL = new AnsiFormat(Background.NONE, Foreground.NONE, Attribute.NORMAL);
  private Background background;
  private Foreground foreground;
  private Set<Attribute> attributes;
  private boolean dirty = true;
  private String formatString;

  public AnsiFormat(Background background, Foreground foreground, Attribute... attributes) {
    this(background, foreground, new HashSet<>(Arrays.asList(attributes)));
  }

  public AnsiFormat(Background background, Foreground foreground, Set<Attribute> attributes) {
    this.background = background;
    this.foreground = foreground;
    this.attributes = attributes;
  }

  private static boolean compareSets(Set<Attribute> set1, Set<Attribute> set2) {
    return !(set1 == null || set2 == null) && set1.size() == set2.size() && set1.containsAll(set2);
  }

  public Set<Attribute> getAttributes() {
    return attributes;
  }

  public void setAttributes(Attribute... attributes) {
    setAttributes(new HashSet<>(Arrays.asList(attributes)));
  }

  public Background getBackground() {
    return background;
  }

  public void setBackground(Background background) {
    if(this.background != background) {
      this.background = background;
      dirty = true;
    }
  }

  public Foreground getForeground() {
    return foreground;
  }

  public void setForeground(Foreground foreground) {
    if(this.foreground != foreground) {
      this.foreground = foreground;
      dirty = true;
    }
  }

  public String getFormatString() {
    if(dirty) {
      Set<Attribute> cleanAttributes = attributes.stream().filter(a -> a != Attribute.NONE).collect(Collectors.toSet());

      if(cleanAttributes.isEmpty() && foreground == Foreground.NONE && background == Background.NONE) {
        formatString = "";
      } else {
        formatString = Ansi.ESC + "[";
        boolean hasOne = false;
        if(!cleanAttributes.isEmpty()) {
          formatString += cleanAttributes.stream().map(Attribute::getCode).reduce((a, b) -> a + ";" + b).orElse("0");
          hasOne = true;
        }

        if(foreground != Foreground.NONE) {
          formatString += (hasOne ? ";" : "") + foreground.getCode();
          hasOne = true;
        }

        if(background != Background.NONE) {
          formatString += (hasOne ? ";" : "") + background.getCode();
        }

        formatString += "m";
      }

      dirty = false;
    }

    return formatString;
  }

  public void setAttributes(Set<Attribute> attributes) {
    if(!compareSets(this.attributes, attributes)) {
      this.attributes = attributes;
      dirty = true;
    }
  }
}
