package com.verzano.terminalrss.ui.widget.scrollable.list.model;

import com.verzano.terminalrss.ui.TUIStringable;

import java.util.Collection;

public class BasicListModel<T extends TUIStringable> implements ListModel<T> {
  @Override
  public void addItem(T item) {

  }

  @Override
  public T getItemAt(int index) {
    return null;
  }

  @Override
  public int getItemIndex(T item) {
    return 0;
  }

  @Override
  public Collection<T> getItems() {
    return null;
  }

  @Override
  public void setItems(Collection<T> items) {

  }

  @Override
  public int getItemCount() {
    return 0;
  }
}
