package com.verzano.terminalrss.tui.widget.text.entry;

import com.verzano.terminalrss.tui.TUIStringable;
import com.verzano.terminalrss.tui.TerminalUI;
import com.verzano.terminalrss.tui.widget.text.TextWidget;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import static com.verzano.terminalrss.tui.constants.Key.DOWN_ARROW;
import static com.verzano.terminalrss.tui.constants.Key.UP_ARROW;
import static com.verzano.terminalrss.tui.constants.Orientation.HORIZONTAL;
import static com.verzano.terminalrss.tui.constants.Position.CENTER_LEFT;

// TODO this should be backed by some kind of list model
public class RolodexWidget<T extends TUIStringable> extends TextWidget {
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
    setText(items.get(this.selectedIndex).toTUIString());
  }

  public T getSelectedItem() {
    return items.get(selectedIndex);
  }

  public void setSelectedItem(T item) {
    setSelectedIndex(items.indexOf(item));
  }

  private int getPreviousIndex(int index) {
    return index == 0 ? items.size() - 1 : index - 1;
  }

  private int getNextIndex(int index) {
    return index == items.size() - 1 ? 0 : index + 1;
  }

  private void printItem(T item, int y) {
    int middleRow = getHeight()/2;
    for (int i = 0; i < getHeight(); i++) {
      TerminalUI.move(getContentX(), y + i);
      if (i == middleRow) {
        TerminalUI.print(getRowForText(item.toTUIString()));
      } else {
        TerminalUI.print(getEmptyContentRow());
      }
    }
  }

  @Override
  public int getNeededWidth() {
    return items.stream()
        .mapToInt(item -> item.toTUIString().length())
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
