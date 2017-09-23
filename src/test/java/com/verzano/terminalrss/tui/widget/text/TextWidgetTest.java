package com.verzano.terminalrss.tui.widget.text;

import static com.verzano.terminalrss.tui.constant.Orientation.HORIZONTAL;
import static com.verzano.terminalrss.tui.constant.Orientation.VERTICAL;
import static com.verzano.terminalrss.tui.constant.Position.BOTTOM;
import static com.verzano.terminalrss.tui.constant.Position.BOTTOM_RIGHT;
import static com.verzano.terminalrss.tui.constant.Position.CENTER;
import static com.verzano.terminalrss.tui.constant.Position.LEFT;
import static com.verzano.terminalrss.tui.constant.Position.RIGHT;
import static com.verzano.terminalrss.tui.constant.Position.TOP;
import static com.verzano.terminalrss.tui.constant.Position.TOP_RIGHT;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.verzano.terminalrss.tui.ansi.AnsiFormat;
import com.verzano.terminalrss.tui.ansi.Attribute;
import com.verzano.terminalrss.tui.ansi.Background;
import com.verzano.terminalrss.tui.ansi.Foreground;
import com.verzano.terminalrss.tui.container.Container;
import com.verzano.terminalrss.tui.widget.Widget;
import com.verzano.terminalrss.tui.widget.container.MockContainerBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TextWidgetTest {
  @Test
  @DisplayName("orientation == HORIZONTAL --> getNeededContentHeight() == 1")
  void getNeededHeight0() {
    TextWidget testObject = new TextWidgetBuilder().orientation(HORIZONTAL).build();

    assertEquals(1, testObject.getNeededContentHeight());
  }

  @Test
  @DisplayName("orientation == VERTICAL && text as t --> getNeededContentHeight() == t.length()")
  void getNeededHeight1() {
    String text = "TEXTOFLENGTH14";
    TextWidget testObject = new TextWidgetBuilder().text(text).orientation(VERTICAL).build();

    assertEquals(text.length(), testObject.getNeededContentHeight());
  }

  @Test
  @DisplayName("orientation == HORIZONTAL && text as t --> getNeededContentWidth == t.length()")
  void getNeededWidth0() {
    String text = "TEXTOFLENGTH14";
    TextWidget testObject = new TextWidgetBuilder().text(text).orientation(HORIZONTAL).build();

    assertEquals(text.length(), testObject.getNeededContentWidth());
  }

  @Test
  @DisplayName("orientation == VERTICAL --> getNeededContentWidth() == 1")
  void getNeededWidth1() {
    TextWidget testObject = new TextWidgetBuilder().orientation(VERTICAL).build();

    assertEquals(1, testObject.getNeededContentWidth());
  }

  @Test
  @DisplayName("orientation == HORIZONTAL" + " && textPosition == LEFT" + " && focusedFormat as ff" + " && text as t"
                   + " && widgetWidth == t.length" + " && isFocused"
                   + " --> getRowForText() == ff.getFormatString() + t + AnsiFormat.NORMAL" + ".getFormatString()")
  void getRowForText0() {
    String text = "TEXTOFLENGTH14";
    AnsiFormat focusedFormat = new AnsiFormat(Background._1, Foreground._4, Attribute.BOLD_ON);
    Container container = new MockContainerBuilder().widgetWidth(text.length()).build();
    TextWidget testObject = new TextWidgetBuilder().text(text)
        .orientation(HORIZONTAL)
        .textPosition(LEFT)
        .container(container)
        .focusedFormat(focusedFormat)
        .build();

    testObject.setFocused();

//    assertEquals(focusedFormat.getFormatString() + text + NORMAL.getFormatString(), testObject.getRowForText(text));
  }

  @Test
  @DisplayName("orientation == HORIZONTAL" + " && textPosition == LEFT" + " && unfocusedFormat as uf" + " && text as t"
                   + " && widgetWidth == t.length" + " && !isFocused"
                   + " --> getRowForText() == uf.getFormatString() + t + AnsiFormat.NORMAL.getFormatString" + "()")
  void getRowForText1() {
    String text = "TEXTOFLENGTH14";
    AnsiFormat unfocusedFormat = new AnsiFormat(Background._1, Foreground._4, Attribute.BOLD_ON);
    Container container = new MockContainerBuilder().widgetWidth(text.length()).build();
    TextWidget testObject = new TextWidgetBuilder().text(text)
        .orientation(HORIZONTAL)
        .textPosition(LEFT)
        .container(container)
        .unfocusedFormat(unfocusedFormat)
        .build();

    Widget.NULL_WIDGET.setFocused();

//    assertEquals(unfocusedFormat.getFormatString() + text + NORMAL.getFormatString(), testObject.getRowForText(text));
  }

  @Test
  @DisplayName("orientation == HORIZONTAL" + " && textPosition == [TOP_LEFT | LEFT | BOTTOM_LEFT]" + " && unfocusedFormat as uf"
                   + " && text as t" + " && (widgetWidth as w) < t.length" + " && !isFocused"
                   + " --> getRowForText() == uf.getFormatString() + t.subString(0, w) + "
                   + "AnsiFormat.NORMAL.getFormatString()")
  void getRowForText2() {
    String text = "TEXTOFLENGTH14";
    int widgetWidth = text.length() - 2;
    Container container = new MockContainerBuilder().widgetWidth(widgetWidth).build();

    TextWidget testObjectTopLeft = new TextWidgetBuilder().text(text)
        .orientation(HORIZONTAL)
        .textPosition(LEFT)
        .container(container)
        .build();

    TextWidget testObjectCenterLeft = new TextWidgetBuilder().text(text)
        .orientation(HORIZONTAL)
        .textPosition(LEFT)
        .container(container)
        .build();

    TextWidget testObjectBottomLeft = new TextWidgetBuilder().text(text)
        .orientation(HORIZONTAL)
        .textPosition(LEFT)
        .container(container)
        .build();

//    assertAll(() -> assertEquals(
//        testObjectTopLeft.getUnfocusedFormat().getFormatString() + text.substring(0, widgetWidth) + NORMAL.getFormatString(),
//        testObjectTopLeft.getRowForText(text)),
//        () -> assertEquals(testObjectCenterLeft.getUnfocusedFormat().getFormatString() + text.substring(0, widgetWidth)
//            + NORMAL.getFormatString(), testObjectCenterLeft.getRowForText(text)),
//        () -> assertEquals(testObjectBottomLeft.getUnfocusedFormat().getFormatString() + text.substring(0, widgetWidth)
//            + NORMAL.getFormatString(), testObjectBottomLeft.getRowForText(text)));
  }

  @Test
  @DisplayName(
      "orientation == HORIZONTAL" + " && textPosition == [TOP_RIGHT | RIGHT | BOTTOM_RIGHT]" + " && unfocusedFormat as uf"
          + " && text as t" + " && (widgetWidth as w) < t.length" + " && !isFocused"
          + " --> getRowForText() == uf.getFormatString() + t.subString(t.length() - w) + "
          + "AnsiFormat.NORMAL.getFormatString()")
  void getRowForText3() {
    String text = "TEXTOFLENGTH14";
    int diff = 2;
    int widgetWidth = text.length() - diff;
    Container container = new MockContainerBuilder().widgetWidth(widgetWidth).build();

    TextWidget testObjectTopRight = new TextWidgetBuilder().text(text)
        .orientation(HORIZONTAL)
        .textPosition(TOP_RIGHT)
        .container(container)
        .build();

    TextWidget testObjectCenterRight = new TextWidgetBuilder().text(text)
        .orientation(HORIZONTAL)
        .textPosition(RIGHT)
        .container(container)
        .build();

    TextWidget testObjectBottomRight = new TextWidgetBuilder().text(text)
        .orientation(HORIZONTAL)
        .textPosition(BOTTOM_RIGHT)
        .container(container)
        .build();

//    assertAll(() -> assertEquals(
//        testObjectTopRight.getUnfocusedFormat().getFormatString() + text.substring(diff) + NORMAL.getFormatString(),
//        testObjectTopRight.getRowForText(text)),
//        () -> assertEquals(
//            testObjectCenterRight.getUnfocusedFormat().getFormatString() + text.substring(diff) + NORMAL.getFormatString(),
//            testObjectCenterRight.getRowForText(text)),
//        () -> assertEquals(
//            testObjectBottomRight.getUnfocusedFormat().getFormatString() + text.substring(diff) + NORMAL.getFormatString(),
//            testObjectBottomRight.getRowForText(text)));
  }

  @Test
  @DisplayName(
      "orientation == HORIZONTAL" + " && textPosition == [TOP | CENTER | BOTTOM]" + " && unfocusedFormat as uf" + " && text as t"
          + " && (widgetWidth as w) < t.length" + " && !isFocused"
          + " --> getRowForText() == uf.getFormatString() + t.subString((t.length() - w)"
          + "/2, t.length - w/2) + AnsiFormat.NORMAL.getFormatString()")
  void getRowForText4() {
    String text = "TEXTOFLENGTH14";
    int diff = 2;
    int widgetWidth = text.length() - diff;
    Container container = new MockContainerBuilder().widgetWidth(widgetWidth).build();

    TextWidget testObjectTopCenter = new TextWidgetBuilder().text(text)
        .orientation(HORIZONTAL)
        .textPosition(TOP)
        .container(container)
        .build();

    TextWidget testObjectCenter = new TextWidgetBuilder().text(text)
        .orientation(HORIZONTAL)
        .textPosition(CENTER)
        .container(container)
        .build();

    TextWidget testObjectBottomCenter = new TextWidgetBuilder().text(text)
        .orientation(HORIZONTAL)
        .textPosition(BOTTOM)
        .container(container)
        .build();

//    assertAll(() -> assertEquals(
//        testObjectCenter.getUnfocusedFormat().getFormatString() + text.substring(diff/2, text.length() - diff/2)
//            + NORMAL.getFormatString(), testObjectCenter.getRowForText(text)),
//        () -> assertEquals(
//            testObjectTopCenter.getUnfocusedFormat().getFormatString() + text.substring(diff/2, text.length() - diff/2)
//                + NORMAL.getFormatString(), testObjectTopCenter.getRowForText(text)),
//        () -> assertEquals(
//            testObjectBottomCenter.getUnfocusedFormat().getFormatString() + text.substring(diff/2, text.length() - diff/2)
//                + NORMAL.getFormatString(), testObjectBottomCenter.getRowForText(text)));
  }
}