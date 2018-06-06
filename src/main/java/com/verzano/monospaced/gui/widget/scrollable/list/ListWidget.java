package com.verzano.monospaced.gui.widget.scrollable.list;

import com.verzano.monospaced.gui.MonospacedGui;
import com.verzano.monospaced.gui.ansi.AnsiFormat;
import com.verzano.monospaced.gui.ansi.Attribute;
import com.verzano.monospaced.gui.ansi.Background;
import com.verzano.monospaced.gui.ansi.Foreground;
import com.verzano.monospaced.gui.constant.Direction;
import com.verzano.monospaced.gui.widget.scrollable.ScrollableWidget;
import com.verzano.monospaced.gui.widget.scrollable.list.model.ListModel;
import com.verzano.monospaced.gui.widget.scrollable.list.model.Stringable;

import java.util.Collection;

import static com.verzano.monospaced.gui.constant.Key.DOWN_ARROW;
import static com.verzano.monospaced.gui.constant.Key.UP_ARROW;

public class ListWidget<T extends Stringable> extends ScrollableWidget {
  private ListModel<T> listModel;
  private int selectedItemIndex;
  private AnsiFormat selectedItemFormat = new AnsiFormat(Background.NONE, Foreground.NONE, Attribute.INVERSE_ON);

  public ListWidget(ListModel<T> listModel) {
    setListModel(listModel);

    addKeyAction(UP_ARROW, () -> {
      scroll(Direction.UP, 1);
      reprint();
    });
    addKeyAction(DOWN_ARROW, () -> {
      scroll(Direction.DOWN, 1);
      reprint();
    });
  }

  public ListModel<T> getListModel() {
    return listModel;
  }

  public void setListModel(ListModel<T> listModel) {
    this.listModel = listModel;
    selectedItemIndex = 0;
    setViewTop(0);
    setInternalHeight(this.listModel.getItemCount());
  }

  public T getSelectedItem() {
    return listModel.getItemAt(selectedItemIndex);
  }

  public AnsiFormat getSelectedItemFormat() {
    return selectedItemFormat;
  }

  public void setSelectedItemFormat(AnsiFormat selectedItemFormat) {
    this.selectedItemFormat = selectedItemFormat;
  }

  public void removeItem(T item) {
    if(listModel.removeItem(item)) {
      if(selectedItemIndex == listModel.getItemCount()) {
        selectedItemIndex = Math.max(0, selectedItemIndex - 1);
      }
      setInternalHeight(listModel.getItemCount());
    }
  }

  public void addItem(T item) {
    if(listModel.addItem(item)) {
      if(listModel.getItemCount() > 1 && listModel.getItemIndex(item) <= selectedItemIndex) {
        selectedItemIndex++;
      }
      setInternalHeight(listModel.getItemCount());
    }
  }

  public void setItems(Collection<T> items) {
    listModel.setItems(items);
    selectedItemIndex = 0;
    setViewTop(0);
    setInternalHeight(listModel.getItemCount());
  }

  @Override
  public int getNeededContentHeight() {
    return listModel.getItemCount();
  }

  @Override
  public int getNeededContentWidth() {
    return listModel.getItems().stream().mapToInt(item -> item.stringify().length()).max().orElse(0) + 1;
  }

  @Override
  public void scroll(Direction dir, int distance) {
    switch(dir) {
      case UP:
        if(getViewTop() == selectedItemIndex) {
          setViewTop(Math.max(0, getViewTop() - 1));
        }
        selectedItemIndex = Math.max(0, selectedItemIndex - distance);
        break;
      case DOWN:
        selectedItemIndex = Math.min(listModel.getItemCount() - 1, selectedItemIndex + distance);
        if(selectedItemIndex == getViewTop() + getHeight()) {
          setViewTop(Math.min(getViewTop() + 1, listModel.getItemCount() - getHeight()));
        }
        break;
    }
  }

  @Override
  public void printContent() {
    super.printContent();

    int width = getContentWidth() - 1;

    for(int row = 0; row < getContentHeight(); row++) {
      MonospacedGui.move(getContentX(), getContentY() + row);
      int index = row + getViewTop();

      if(index >= listModel.getItemCount()) {
        MonospacedGui.printn(" ", width);
      } else {
        String toPrint = listModel.getItemAt(index).stringify();
        if(toPrint.length() > width) {
          toPrint = toPrint.substring(0, width);
        } else if(toPrint.length() < width) {
          toPrint = toPrint + new String(new char[width - toPrint.length()]).replace('\0', ' ');
        }

        if(index == selectedItemIndex) {
          toPrint = selectedItemFormat.getFormatString() + toPrint + AnsiFormat.NORMAL.getFormatString();
        }

        MonospacedGui.print(toPrint);
      }
    }
  }
}
