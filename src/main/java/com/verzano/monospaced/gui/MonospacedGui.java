package com.verzano.monospaced.gui;

import com.verzano.monospaced.gui.ansi.Ansi;
import com.verzano.monospaced.gui.constant.Key;
import com.verzano.monospaced.gui.container.floor.Floor;
import com.verzano.monospaced.gui.container.floor.FloorOptions;
import com.verzano.monospaced.gui.floater.Floater;
import com.verzano.monospaced.gui.metric.Point;
import com.verzano.monospaced.gui.metric.Size;
import com.verzano.monospaced.gui.task.print.PrintTask;
import com.verzano.monospaced.gui.widget.Widget;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;

// TODO use an executor to schedule events
// TODO add defaults for attributes as well as some css style waterfall thing for getting them
// TODO order all of the fields in all of the classes correctly
// TODO only permit one floater at a time, or have a stack of floaters
// TODO 'focused' is if any of its children are focused
// TODO come up with a better way to determine the time between pulling for draw events
// TODO allow resize events to be grouped together
// TODO foreground/background can be inherited from parent
public class MonospacedGui {
  private static final AtomicBoolean run = new AtomicBoolean(true);
  private static final BlockingDeque<PrintTask> printTaskQueue = new LinkedBlockingDeque<>();
  private static final Terminal terminal;
  private static Floor floor = new Floor();
  private static Floater floater = Floater.NULL_FLOATER;
  private static Widget focusedWidget = Widget.NULL_WIDGET;
  private static final Thread keyActionThread = new Thread(MonospacedGui::keyActionLoop, "Key Action");
  private static Size size;
  private static final Thread printingThread = new Thread(MonospacedGui::printingLoop, "Printing");
  private static final Thread resizingThread = new Thread(MonospacedGui::resizingLoop, "Resizing");

  static {
    try {
      terminal = TerminalBuilder.terminal();
      terminal.enterRawMode();
      terminal.echo(false);

      size = new Size(terminal.getWidth(), terminal.getHeight());

      printingThread.start();
      keyActionThread.start();
      resizingThread.start();

      printTaskQueue.addFirst(() -> print(Ansi.HIDE_CURSOR));
    } catch(IOException e) {
      throw new RuntimeException(e);
    }
  }

  private MonospacedGui() {}

  public static Floater getFloater() {
    return floater;
  }

  public static Widget getFocusedWidget() {
    return focusedWidget;
  }

  public static void setFocusedWidget(Widget focusedWidget) {
    MonospacedGui.focusedWidget = focusedWidget;
  }

  public static Size getSize() {
    return size;
  }

  private static void clear() {
    String emptyLine = new String(new char[size.getWidth()]).replace("\0", " ");
    for(int row = 1; row <= size.getHeight(); row++) {
      move(1, row);
      terminal.writer().print(emptyLine);
    }
    move(1, 1);
    terminal.flush();
  }

  public static int getHeight() {
    return size.getHeight();
  }

  public static int getWidth() {
    return size.getWidth();
  }

  private static void keyActionLoop() {
    try {
      while(run.get()) {
        int key = terminal.reader().read(100);
        switch(key) {
          case Ansi.ESC:
            switch(terminal.reader().read()) {
              case '[':
                focusedWidget.fireKeyActions(Key.ESCAPED_PREFIX + (char)terminal.reader().read());
                break;
            }
            break;
          case -2:
            break;
          default:
            focusedWidget.fireKeyActions((char)key + "");
            break;
        }
      }
    } catch(IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void move(int x, int y) {
    if(Thread.currentThread() != printingThread) {
      printTaskQueue.add(() -> move(x, y));
    } else {
      terminal.writer().printf(Ansi.SET_POSITION, y, x);
    }
  }

  public static void print(String s) {
    if(Thread.currentThread() != printingThread) {
      printTaskQueue.add(() -> print(s));
    } else {
      terminal.writer().print(s);
    }
  }

  private static void printingLoop() {
    clear();

    while(run.get()) {
      try {
        printTaskQueue.take().print();
        terminal.writer().flush();
      } catch(InterruptedException e) {
        throw new RuntimeException(e);
      }
    }

    clear();
  }

  public static void printn(String s, int n) {
    if(Thread.currentThread() != printingThread) {
      printTaskQueue.add(() -> printn(s, n));
    } else {
      for(int i = 0; i < n; i++) {
        terminal.writer().print(s);
      }
    }
  }

  public static void removeFloater() {
    MonospacedGui.floater = Floater.NULL_FLOATER;
  }

  public static void reprint() {
    if(Thread.currentThread() != printingThread) {
      printTaskQueue.add(MonospacedGui::reprint);
    } else {
      floor.print();
      if(floater != Floater.NULL_FLOATER) {
        floater.print();
      }
    }
  }

  public static void resize() {
    if(Thread.currentThread() != printingThread) {
      printTaskQueue.addFirst(MonospacedGui::resize);
    } else {
      floor.arrange();
      if(floater != Floater.NULL_FLOATER) {
        floater.arrange();
      }
    }
  }

  private static void resizingLoop() {
    while(run.get()) {
      if(size.getWidth() != terminal.getWidth() || size.getHeight() != terminal.getHeight()) {
        size.setWidth(terminal.getWidth());
        size.setHeight(terminal.getHeight());
        resize();
        reprint();
      }
      try {
        Thread.sleep(200);
      } catch(InterruptedException ignored) {
      }
    }
  }

  public static void schedulePrintTask(PrintTask printTask) {
    printTaskQueue.add(printTask);
  }

  public static void setBaseWidget(Widget baseWidget) {
    floor.addWidget(baseWidget, new FloorOptions(new Size(Size.FILL_CONTAINER, Size.FILL_CONTAINER), new Point(1, 1)));
  }

  public static void setFloater(Floater floater) {
    MonospacedGui.floater = floater;
    MonospacedGui.floater.setFocused();
  }

  public static void shutdown() {
    new Thread(() -> {
      printTaskQueue.addFirst(() -> print(Ansi.SHOW_CURSOR));
      printTaskQueue.addFirst(() -> run.set(false));

      try {
        printingThread.join();
        keyActionThread.join();
        resizingThread.join();
      } catch(InterruptedException ignored) {
      }

      try {
        terminal.close();
      } catch(IOException e) {
        throw new RuntimeException(e);
      }
    }).start();
  }
}
