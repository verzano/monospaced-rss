package com.verzano.terminalrss.ui;

import com.verzano.terminalrss.ui.widget.PrintTask;
import com.verzano.terminalrss.ui.widget.TerminalWidget;
import lombok.Setter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.util.SortedSet;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.verzano.terminalrss.ui.widget.TerminalWidget.NULL_WIDGET;
import static com.verzano.terminalrss.ui.widget.TerminalWidget.Z_COMPARTOR;
import static com.verzano.terminalrss.ui.widget.constants.Ansi.ESC;
import static com.verzano.terminalrss.ui.widget.constants.Ansi.SET_POSITION;

// TODO use an executor to schedule events
// TODO use a thread to check size (these should replace each other in the event queue)
// TODO use a thread to check mouse events cursor offscreen instead of removing it?
// TODO create a layout manager type thing for the TerminalUI
public class TerminalUI {
  private TerminalUI() { }

  @Setter
  private static TerminalWidget focusedWidget = NULL_WIDGET;

  private static final SortedSet<TerminalWidget> widgetStack = new ConcurrentSkipListSet<>(Z_COMPARTOR);

  private static final AtomicBoolean runKeyActionThread = new AtomicBoolean(true);
  private static final Thread keyActionThread = new Thread(TerminalUI::keyActionLoop, "Key Action");

  private static final AtomicBoolean runPrintingThread = new AtomicBoolean(true);
  private static final Thread printingThread = new Thread(TerminalUI::printingLoop, "Printing");
  private static final BlockingDeque<PrintTask> printTaskQueue = new LinkedBlockingDeque<>();

  private static final Terminal terminal;
  static {
    try {
      terminal = TerminalBuilder.terminal();
      terminal.enterRawMode();
      terminal.echo(false);

      printingThread.start();
      keyActionThread.start();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static void printingLoop() {
    clear();

    while(runPrintingThread.get()) {
      try {
        printTaskQueue.take().print();
        terminal.writer().flush();
      } catch (InterruptedException e) {
        // TODO logging
        throw new RuntimeException(e);
      }
    }

    clear();
  }

  private static void keyActionLoop() {
    try {
      while (runKeyActionThread.get()) {
        // TODO this is kind of a lame way to do this
        int key = terminal.reader().read(100);
        switch (key) {
          case ESC:
            switch (terminal.reader().read()) {
              case '[':
                focusedWidget.fireEscapedKeyActions(terminal.reader().read());
                break;
            }
          case -2:
            break;
          default:
            focusedWidget.fireKeyActions(key);
            break;
        }
      }
    } catch (IOException e) {
      // TODO logging
      throw new RuntimeException(e);
    }
  }

  public static void shutdown() {
    new Thread(() -> {
      printTaskQueue.addFirst(() -> runPrintingThread.set(false));

      runKeyActionThread.set(false);

      try {
        printingThread.join();
        keyActionThread.join();
      } catch (InterruptedException ignored) {
        // TODO logging...
      }

      try {
        terminal.close();
      } catch (IOException e) {
        // TODO logging
        throw new RuntimeException(e);
      }
    }).start();
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

  private static void print() {
    // TODO ensure this order is correct
    widgetStack.forEach(TerminalWidget::print);
  }

  private static void clear() {
    move(1, 1);
    String emptyLine = new String(new char[terminal.getWidth()]).replace("\0", " ");
    for (int row = 0; row < terminal.getHeight(); row++) {
      terminal.writer().println(emptyLine);
    }
    move(1, 1);
    terminal.flush();
  }

  public static void reprint() {
    if (Thread.currentThread() != printingThread) {
      printTaskQueue.add(TerminalUI::reprint);
    } else {
      clear();
      print();
    }
  }

  public static void move(int x, int y) {
    if (Thread.currentThread() != printingThread) {
      printTaskQueue.add(() -> move(x, y));
    } else {
      printf(SET_POSITION, y, x);
    }
  }

  public static void print(String s) {
    if (Thread.currentThread() != printingThread) {
      printTaskQueue.add(() -> print(s));
    } else {
      terminal.writer().print(s);
    }
  }

  public static void printf(String s, Object... args) {
    if (Thread.currentThread() != printingThread) {
      printTaskQueue.add(() -> printf(s, args));
    } else {
      terminal.writer().printf(s, args);
    }
  }

  public static void printn(String s, int n) {
    if (Thread.currentThread() != printingThread) {
      printTaskQueue.add(() -> printn(s, n));
    } else {
      for (int i = 0; i < n; i++) {
        terminal.writer().print(s);
      }
    }
  }

  public static int getWidth() {
    return terminal.getWidth();
  }

  public static int getHeight() {
    return terminal.getHeight();
  }
}
