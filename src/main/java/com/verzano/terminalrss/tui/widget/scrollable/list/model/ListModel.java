package com.verzano.terminalrss.tui.widget.scrollable.list.model;

import com.verzano.terminalrss.tui.TuiStringable;
import java.util.Collection;

public interface ListModel<T extends TuiStringable> {
  boolean addItem(T item);
  T getItemAt(int index);
  int getItemCount();
  int getItemIndex(T item);
  Collection<T> getItems();
  void setItems(Collection<T> items);
  boolean removeItem(T item);
}
