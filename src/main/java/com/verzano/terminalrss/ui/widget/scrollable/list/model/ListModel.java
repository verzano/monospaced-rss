package com.verzano.terminalrss.ui.widget.scrollable.list.model;

import com.verzano.terminalrss.ui.TUIStringable;

import java.util.Collection;

public interface ListModel<T extends TUIStringable> {
  void addItem(T item);

  T getItemAt(int index);

  int getItemIndex(T item);

  Collection<T> getItems();

  void setItems(Collection<T> items);

  int getItemCount();
}
