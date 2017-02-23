package com.verzano.terminalrss.ui.widget.text.entry;

import com.verzano.terminalrss.ui.TerminalUI;
import com.verzano.terminalrss.ui.widget.text.TextWidget;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import static com.verzano.terminalrss.ui.constants.Key.DOWN_ARROW;
import static com.verzano.terminalrss.ui.constants.Key.UP_ARROW;
import static com.verzano.terminalrss.ui.constants.Orientation.HORIZONTAL;
import static com.verzano.terminalrss.ui.constants.Position.CENTER_LEFT;

// TODO this should be backed by some kind of list model
public class RolodexWidget<T> extends TextWidget {
  private List<T> items;

  @Getter
  private volatile int selectedIndex = 0;

  @Getter @Setter
  private int itemsBefore;

  @Getter @Setter
  private int itemsAfter;

  public RolodexWidget(List<T> items, int itemsBefore, int itemsAfter) {
    super("", HORIZONTAL, CENTER_LEFT);
    this.items = items;
    this.itemsBefore = itemsBefore;
    this.itemsAfter = itemsAfter;

    addKeyAction(UP_ARROW, () -> {
      setSelectedIndex(getPreviousIndex(selectedIndex));
      reprint();
    });

    addKeyAction(DOWN_ARROW, () -> {
      setSelectedIndex(getNextIndex(selectedIndex));
      reprint();
    });
  }

  public void setSelectedIndex(int selectedIndex) {
    this.selectedIndex = selectedIndex;
    setText(items.get(this.selectedIndex).toString());
  }

  public T getSelectedItem() {
    return items.get(selectedIndex);
  }

  private int getPreviousIndex(int index) {
    return index == 0 ? items.size() - 1 : index - 1;
  }

  private int getNextIndex(int index) {
    return index == items.size() - 1 ? 0 : index + 1;
  }

  private String cropOrPad(String s) {
    int maxWidth = getWidth();

    if (s.length() < maxWidth) {
      s += new String(new char[maxWidth - s.length()]).replace('\0', ' ');
    } else if (s.length() > maxWidth) {
      s = s.substring(0, maxWidth);
    }

    return s;
  }

  private void printItem(T item, int y) {
    int middleRow = getHeight()/2;
    for (int i = 0; i < getHeight(); i++) {
      TerminalUI.move(getContentX(), y + i);
      if (i == middleRow) {
        TerminalUI.print(getRowForText(item.toString()));
      } else {
        TerminalUI.print(getEmptyContentRow());
      }
    }
  }

  // TODO this gets a little fucky if any of the toStrings prints a newline... which nothing should ever do
  @Override
  public int getNeededWidth() {
    return items.stream()
        .mapToInt(item -> item.toString().length())
        .max()
        .orElseGet(() -> 0);
  }

  @Override
  public int getNeededHeight() {
    return 1;
  }

  @Override
  public void printContent() {
    super.printContent();

    if (isFocused()) {
      int index = selectedIndex;
      for (int i = 1; i <= itemsBefore; i++) {
        index = getPreviousIndex(index);
        printItem(items.get(index), getContentY() - i*getContentHeight());
      }

      index = selectedIndex;
      for (int i = 1; i <= itemsAfter; i++) {
        index = getNextIndex(index);
        printItem(items.get(index), getContentY() + i*getContentHeight());
      }
    }
  }
}
