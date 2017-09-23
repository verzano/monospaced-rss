package com.verzano.terminalui.widget.scrollable.list.model;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class BasicListModel<T extends Stringable> implements ListModel<T> {
  private List<T> items;

  public BasicListModel(Collection<T> items) {
    setItems(items);
  }

  @Override
  public boolean addItem(T item) {
    return items.add(item);
  }

  @Override
  public T getItemAt(int index) {
    return items.get(index);
  }

  @Override
  public int getItemCount() {
    return items.size();
  }

  @Override
  public int getItemIndex(T item) {
    return items.indexOf(item);
  }

  @Override
  public Collection<T> getItems() {
    return items;
  }

  @Override
  public void setItems(Collection<T> items) {
    this.items = new LinkedList<>(items);
  }

  @Override
  public boolean removeItem(T item) {
    return items.remove(item);
  }
}
