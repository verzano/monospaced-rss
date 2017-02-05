package com.verzano.terminalrss.ui;

import com.verzano.terminalrss.ui.metrics.Size;
import com.verzano.terminalrss.ui.task.print.PrintTask;
import com.verzano.terminalrss.ui.widget.TerminalWidget;
import lombok.Getter;
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
// TODO create a layout manager type thing for the TerminalUI
// TODO resizing draws lots of extra shit
public class TerminalUI {
  private TerminalUI() { }

  @Getter @Setter
  private static TerminalWidget focusedWidget = NULL_WIDGET;

  private static final SortedSet<TerminalWidget> widgetStack = new ConcurrentSkipListSet<>(Z_COMPARTOR);

  private static final AtomicBoolean run = new AtomicBoolean(true);

  private static final Thread keyActionThread = new Thread(TerminalUI::keyActionLoop, "Key Action");

  private static final Thread printingThread = new Thread(TerminalUI::printingLoop, "Printing");
  private static final BlockingDeque<PrintTask> printTaskQueue = new LinkedBlockingDeque<>();

  private static final Thread resizingThread = new Thread(TerminalUI::resizingLoop, "Resizing");
  @Getter
  private static Size size;

  private static final Terminal terminal;
  static {
    try {
      terminal = TerminalBuilder.terminal();
      terminal.enterRawMode();
      terminal.echo(false);

      size = new Size(terminal.getWidth(), terminal.getHeight());

      printingThread.start();
      keyActionThread.start();
      resizingThread.start();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static int getWidth() {
    return size.getWidth();
  }

  public static int getHeight() {
    return size.getHeight();
  }

  private static void printingLoop() {
    clear();

    while (run.get()) {
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
      while (run.get()) {
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

  // TODO maybe have a like a little delay so that multiple resizes can be grouped together...
  private static void resizingLoop() {
    while (run.get()) {
      if (size.getWidth() != terminal.getWidth() || size.getHeight() != terminal.getHeight()) {
        size.setWidth(terminal.getWidth());
        size.setHeight(terminal.getHeight());
        resize();
        reprint();
      }
      try {
        Thread.sleep(200);
      } catch (InterruptedException ignored) {
        // TODO probably log this...
      }
    }
  }

  public static void shutdown() {
    new Thread(() -> {
      printTaskQueue.addFirst(() -> run.set(false));

      try {
        printingThread.join();
        keyActionThread.join();
        resizingThread.join();
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
    widgetStack.forEach(TerminalWidget::print);
  }

  private static void clear() {
    String emptyLine = new String(new char[size.getWidth()]).replace("\0", " ");
    for (int row = 1; row <= size.getHeight(); row++) {
      move(1, row);
      terminal.writer().print(emptyLine);
    }
    move(1, 1);
    terminal.flush();
  }

  public static void resize() {
    if (Thread.currentThread() != printingThread) {
      printTaskQueue.addFirst(TerminalUI::resize);
    } else {
      widgetStack.forEach(TerminalWidget::size);
    }
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
      terminal.writer().printf(SET_POSITION, y, x);
    }
  }

  public static void print(String s) {
    if (Thread.currentThread() != printingThread) {
      printTaskQueue.add(() -> print(s));
    } else {
      terminal.writer().print(s);
    }
  }

  public static void print(Object o) {
    print(o.toString());
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

  public static void schedulePrintTask(PrintTask printTask) {
    printTaskQueue.add(printTask);
  }
}
