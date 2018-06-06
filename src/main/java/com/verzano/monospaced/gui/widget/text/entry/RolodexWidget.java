package com.verzano.monospaced.gui.widget.text.entry;

import com.verzano.monospaced.gui.MonospacedGui;
import com.verzano.monospaced.gui.widget.scrollable.list.model.ListModel;
import com.verzano.monospaced.gui.widget.scrollable.list.model.Stringable;
import com.verzano.monospaced.gui.widget.text.TextWidget;

import static com.verzano.monospaced.gui.constant.Key.DOWN_ARROW;
import static com.verzano.monospaced.gui.constant.Key.UP_ARROW;
import static com.verzano.monospaced.gui.constant.Orientation.HORIZONTAL;
import static com.verzano.monospaced.gui.constant.Position.LEFT;
import static com.verzano.monospaced.gui.util.PrintUtils.getRowForText;

public class RolodexWidget<T extends Stringable> extends TextWidget {
  private ListModel<T> listModel;
  private volatile int selectedIndex;
  private int itemsBefore;
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

  public int getItemsAfter() {
    return itemsAfter;
  }

  public void setItemsAfter(int itemsAfter) {
    this.itemsAfter = itemsAfter;
  }

  public int getItemsBefore() {
    return itemsBefore;
  }

  public void setItemsBefore(int itemsBefore) {
    this.itemsBefore = itemsBefore;
  }

  public ListModel<T> getListModel() {
    return listModel;
  }

  public void setListModel(ListModel<T> listModel) {
    this.listModel = listModel;
  }

  private int getNextIndex(int index) {
    return index == listModel.getItemCount() - 1 ? 0 : index + 1;
  }

  private int getPreviousIndex(int index) {
    return index == 0 ? listModel.getItemCount() - 1 : index - 1;
  }

  public int getSelectedIndex() {
    return selectedIndex;
  }

  public void setSelectedIndex(int selectedIndex) {
    this.selectedIndex = selectedIndex;
    setText(listModel.getItemAt(this.selectedIndex).stringify());
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
      MonospacedGui.move(getContentX(), y + i);
      if(i == middleRow) {
        MonospacedGui.print(getRowForText(item.stringify(), getTextPosition(), getAnsiFormatPrefix(), getContentWidth()));
      } else {
        MonospacedGui.print(getEmptyContentRow());
      }
    }
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
}
