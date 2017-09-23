package com.verzano.terminalrss.tui.widget.scrollable.list.model;

import com.verzano.terminalrss.tui.TuiStringable;
import java.util.Collection;

public interface ListModel<T extends TuiStringable> {
  boolean addItem(T item);
  boolean removeItem(T item);
  T getItemAt(int index);
  int getItemIndex(T item);
  Collection<T> getItems();
  void setItems(Collection<T> items);
  int getItemCount();
}
