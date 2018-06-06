package com.verzano.monospaced.gui.widget.scrollable.list.model;

import java.util.Collection;

public interface ListModel<T extends Stringable> {
  boolean addItem(T item);
  T getItemAt(int index);
  int getItemCount();
  int getItemIndex(T item);
  Collection<T> getItems();
  void setItems(Collection<T> items);
  boolean removeItem(T item);
}
