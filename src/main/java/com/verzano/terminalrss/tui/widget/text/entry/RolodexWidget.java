package com.verzano.terminalrss.tui.widget.text.entry;

import com.verzano.terminalrss.tui.TUIStringable;
import com.verzano.terminalrss.tui.TerminalUI;
import com.verzano.terminalrss.tui.widget.scrollable.list.model.ListModel;
import com.verzano.terminalrss.tui.widget.text.TextWidget;
import lombok.Getter;
import lombok.Setter;

import static com.verzano.terminalrss.tui.constants.Key.DOWN_ARROW;
import static com.verzano.terminalrss.tui.constants.Key.UP_ARROW;
import static com.verzano.terminalrss.tui.constants.Orientation.HORIZONTAL;
import static com.verzano.terminalrss.tui.constants.Position.CENTER_LEFT;

public class RolodexWidget<T extends TUIStringable> extends TextWidget {
  private ListModel<T> listModel;

  @Getter
  private volatile int selectedIndex = 0;

  @Getter @Setter
  private int itemsBefore;

  @Getter @Setter
  private int itemsAfter;

  public RolodexWidget(ListModel<T> listModel, int itemsBefore, int itemsAfter) {
    super("", HORIZONTAL, CENTER_LEFT);
    this.listModel = listModel;
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
    setText(listModel.getItemAt(this.selectedIndex).toTUIString());
  }

  public T getSelectedItem() {
    return listModel.getItemAt(selectedIndex);
  }

  public void setSelectedItem(T item) {
    setSelectedIndex(listModel.getItemIndex(item));
  }

  private int getPreviousIndex(int index) {
    return index == 0 ? listModel.getItemCount() - 1 : index - 1;
  }

  private int getNextIndex(int index) {
    return index == listModel.getItemCount() - 1 ? 0 : index + 1;
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
    return listModel.getItems().stream()
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
        printItem(listModel.getItemAt(index), getContentY() - i*getContentHeight());
      }

      index = selectedIndex;
      for (int i = 1; i <= itemsAfter; i++) {
        index = getNextIndex(index);
        printItem(listModel.getItemAt(index), getContentY() + i*getContentHeight());
      }
    }
  }
}
