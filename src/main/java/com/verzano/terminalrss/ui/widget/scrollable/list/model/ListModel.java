package com.verzano.terminalrss.ui.widget.scrollable.list.model;

import java.util.Collection;

public interface ListModel<T> {
  void addItem(T item);

  T getItemAt(int index);

  int getItemIndex(T item);

  Collection<T> getItems();

  void setItems(Collection<T> items);

  int getItemCount();
}
