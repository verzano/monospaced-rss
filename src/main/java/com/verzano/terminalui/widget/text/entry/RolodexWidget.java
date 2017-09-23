package com.verzano.terminalui.widget.text.entry;

import static com.verzano.terminalui.constant.Key.DOWN_ARROW;
import static com.verzano.terminalui.constant.Key.UP_ARROW;
import static com.verzano.terminalui.constant.Orientation.HORIZONTAL;
import static com.verzano.terminalui.constant.Position.LEFT;
import static com.verzano.terminalui.util.PrintUtils.getRowForText;

import com.verzano.terminalui.TerminalUi;
import com.verzano.terminalui.widget.scrollable.list.model.ListModel;
import com.verzano.terminalui.widget.scrollable.list.model.Stringable;
import com.verzano.terminalui.widget.text.TextWidget;
import lombok.Getter;
import lombok.Setter;

public class RolodexWidget<T extends Stringable> extends TextWidget {
  private ListModel<T> listModel;
  @Getter
  private volatile int selectedIndex;
  @Getter
  @Setter
  private int itemsBefore;
  @Getter
  @Setter
  private int itemsAfter;

  public RolodexWidget(ListModel<T> listModel, int itemsBefore, int itemsAfter) {
    super("", HORIZONTAL, LEFT);
    this.listModel = listModel;
    this.itemsBefore = itemsBefore;
    this.itemsAfter = itemsAfter;

    setSelectedIndex(0);

    addKeyAction(UP_ARROW, () -> {
      setSelectedIndex(getPreviousIndex(selectedIndex));
      reprint();
    });

    addKeyAction(DOWN_ARROW, () -> {
      setSelectedIndex(getNextIndex(selectedIndex));
      reprint();
    });
  }

  @Override
  public int getNeededContentHeight() {
    return 1;
  }

  @Override
  public int getNeededContentWidth() {
    return listModel.getItems().stream().mapToInt(item -> item.stringify().length()).max().orElse(0);
  }

  @Override
  public void printContent() {
    super.printContent();

    if(isFocused()) {
      int index = selectedIndex;
      for(int i = 1; i <= itemsBefore; i++) {
        index = getPreviousIndex(index);
        printItem(listModel.getItemAt(index), getContentY() - i*getContentHeight());
      }

      index = selectedIndex;
      for(int i = 1; i <= itemsAfter; i++) {
        index = getNextIndex(index);
        printItem(listModel.getItemAt(index), getContentY() + i*getContentHeight());
      }
    }
  }

  private int getNextIndex(int index) {
    return index == listModel.getItemCount() - 1 ? 0 : index + 1;
  }

  private int getPreviousIndex(int index) {
    return index == 0 ? listModel.getItemCount() - 1 : index - 1;
  }

  public T getSelectedItem() {
    return listModel.getItemAt(selectedIndex);
  }

  public void setSelectedItem(T item) {
    setSelectedIndex(listModel.getItemIndex(item));
  }

  private void printItem(T item, int y) {
    int middleRow = getContentHeight()/2;
    for(int i = 0; i < getContentHeight(); i++) {
      TerminalUi.move(getContentX(), y + i);
      if(i == middleRow) {
        TerminalUi.print(getRowForText(item.stringify(), getTextPosition(), getAnsiFormatPrefix(), getContentWidth()));
      } else {
        TerminalUi.print(getEmptyContentRow());
      }
    }
  }

  public void setSelectedIndex(int selectedIndex) {
    this.selectedIndex = selectedIndex;
    setText(listModel.getItemAt(this.selectedIndex).stringify());
  }
}
