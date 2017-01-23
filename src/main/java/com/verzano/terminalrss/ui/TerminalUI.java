package com.verzano.terminalrss.ui;

import com.verzano.terminalrss.ui.widget.TerminalWidget;
import lombok.Setter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.verzano.terminalrss.ui.widget.TerminalWidget.NULL_WIDGET;
import static com.verzano.terminalrss.ui.widget.TerminalWidget.Z_COMPARTOR;
import static com.verzano.terminalrss.ui.widget.constants.Ansi.ESC;
import static com.verzano.terminalrss.ui.widget.constants.Ansi.SET_POSITION;

// TODO make this a base component, add a list and a text view, add listeners, add mouse listener to stop scrolling
// TODO use an executor to schedule events
// TODO use a thread to check size (these should replace each other in the event queue)
// TODO use a thread to check mouse events cursor offscreen instead of removing it?
// TODO just leave the
public class TerminalUI {
  private TerminalUI() { }

  @Setter
  private static TerminalWidget focusedWidget = NULL_WIDGET;

  private static final SortedSet<TerminalWidget> widgetStack = new ConcurrentSkipListSet<>(Z_COMPARTOR);
  private static final AtomicBoolean run = new AtomicBoolean(true);

  private static final Terminal terminal;
  static {
    try {
      terminal = TerminalBuilder.terminal();
      terminal.enterRawMode();
      terminal.echo(false);
      // TODO do this better somehow
      new Thread(TerminalUI::startup).start();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static void startup() {
    clear();

    try {
      while (run.get()) {
        paint();
        terminal.flush();

        int key = terminal.reader().read();
        switch (key) {
          // TODO remove this
          case 'q':
            shutdown();
            break;
          case ESC:
            switch (terminal.reader().read()) {
              case '[':
                focusedWidget.fireEscapedKeyActions(terminal.reader().read());
                break;
            }
          default:
            focusedWidget.fireKeyActions(key);
            break;
        }
      }

      clear();
      terminal.flush();
      terminal.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void shutdown() {
    run.set(false);
  }

  public static void addWidget(TerminalWidget widget) {
    widgetStack.add(widget);
  }

  public static void removeWidget(TerminalWidget widget) {
    widgetStack.remove(widget);

    if (focusedWidget == widget) {
      // TODO get the highest Z widget maybe
      focusedWidget = NULL_WIDGET;
    }
  }

  private static void paint() {
    // TODO ensure this order is correct
    widgetStack.forEach(TerminalWidget::print);
  }

  private static void clear() {
    move(1, 1);
    for (int row = 0; row < terminal.getHeight(); row++) {
      // TODO could be more efficient but its only used for shutting down the shit...
      terminal.writer().println(new String(new char[terminal.getWidth()]).replace("\0", " "));
    }
    move(1, 1);
  }

  public static void repaint() {
    clear();
    paint();
  }

  public static void move(int x, int y) {
    printf(SET_POSITION, y, x);
  }

  public static void print(String s) {
    terminal.writer().print(s);
  }

  public static void printf(String s, Object... args) {
    terminal.writer().printf(s, args);
  }

  public static void printr(String s, int n) {
    for (int i = 0; i < n; i++) {
      terminal.writer().print(s);
    }
  }

  public static int getWidth() {
    return terminal.getWidth();
  }

  public static int getHeight() {
    return terminal.getHeight();
  }
}
