package com.verzano.terminalrss.tui.widget.scrollable.list;

import com.verzano.terminalrss.tui.TerminalUi;
import com.verzano.terminalrss.tui.TuiStringable;
import com.verzano.terminalrss.tui.ansi.AnsiFormat;
import com.verzano.terminalrss.tui.ansi.Attribute;
import com.verzano.terminalrss.tui.ansi.Background;
import com.verzano.terminalrss.tui.ansi.Foreground;
import com.verzano.terminalrss.tui.constant.Direction;
import com.verzano.terminalrss.tui.widget.scrollable.ScrollableWidget;
import com.verzano.terminalrss.tui.widget.scrollable.list.model.ListModel;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

import static com.verzano.terminalrss.tui.ansi.AnsiFormat.NORMAL;
import static com.verzano.terminalrss.tui.constant.Key.DOWN_ARROW;
import static com.verzano.terminalrss.tui.constant.Key.UP_ARROW;

public class ListWidget<T extends TuiStringable> extends ScrollableWidget {
  private ListModel<T> listModel;

  private int selectedItemIndex;

  @Getter
  @Setter
  private AnsiFormat selectedItemFormat = new AnsiFormat(Background.NONE, Foreground.NONE,
      Attribute.INVERSE_ON);

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

  public void setListModel(ListModel<T> listModel) {
    this.listModel = listModel;
    selectedItemIndex = 0;
    setViewTop(0);
    setInternalHeight(this.listModel.getItemCount());
  }

  public void addItem(T item) {
    if (listModel.addItem(item)) {
      if (listModel.getItemCount() > 1 && listModel.getItemIndex(item) <= selectedItemIndex) {
        selectedItemIndex++;
      }
      setInternalHeight(listModel.getItemCount());
    }
  }

  public void removeItem(T item) {
    if (listModel.removeItem(item)) {
      if (selectedItemIndex == listModel.getItemCount()) {
        selectedItemIndex = Math.max(0, selectedItemIndex - 1);
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

  public T getSelectedItem() {
    return listModel.getItemAt(selectedItemIndex);
  }

  @Override
  public void scroll(Direction dir, int distance) {
    switch (dir) {
      case UP:
        if (getViewTop() == selectedItemIndex) {
          setViewTop(Math.max(0, getViewTop() - 1));
        }
        selectedItemIndex = Math.max(0, selectedItemIndex - distance);
        break;
      case DOWN:
        selectedItemIndex = Math.min(listModel.getItemCount() - 1, selectedItemIndex + distance);
        if (selectedItemIndex == getViewTop() + getHeight()) {
          setViewTop(Math.min(getViewTop() + 1, listModel.getItemCount() - getHeight()));
        }
        break;
    }
  }

  @Override
  public int getNeededWidth() {
    return listModel.getItems().stream()
        .mapToInt(item -> item.toTuiString().length())
        .max()
        .orElse(0) + 1;
  }

  @Override
  public int getNeededHeight() {
    return listModel.getItemCount();
  }

  @Override
  public void printContent() {
    super.printContent();

    int width = getContentWidth() - 1;

    for (int row = 0; row < getContentHeight(); row++) {
      TerminalUi.move(getContentX(), getContentY() + row);
      int index = row + getViewTop();

      if (index >= listModel.getItemCount()) {
        TerminalUi.printn(" ", width);
      } else {
        String toPrint = listModel.getItemAt(index).toTuiString();
        if (toPrint.length() > width) {
          toPrint = toPrint.substring(0, width);
        } else if (toPrint.length() < width) {
          toPrint = toPrint + new String(new char[width - toPrint.length()]).replace('\0', ' ');
        }

        if (index == selectedItemIndex) {
          toPrint = selectedItemFormat.getFormatString() + toPrint + NORMAL.getFormatString();
        }

        TerminalUi.print(toPrint);
      }
    }
  }
}
