package com.verzano.terminalrss.tui.widget.scrollable.list.model;

import com.verzano.terminalrss.tui.TUIStringable;

import java.util.Collection;

public class BasicListModel<T extends TUIStringable> implements ListModel<T> {
  @Override
  public boolean addItem(T item) {
    return false;
  }

  @Override
  public boolean removeItem(T item) {
    return false;
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
