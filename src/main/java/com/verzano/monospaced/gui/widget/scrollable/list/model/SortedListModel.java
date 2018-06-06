package com.verzano.monospaced.gui.widget.scrollable.list.model;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class SortedListModel<T extends Stringable> implements ListModel<T> {
  private List<T> items = new LinkedList<>();
  private Comparator<T> sortOrder;

  public SortedListModel(Comparator<T> sortOrder) {
    this.sortOrder = sortOrder;
  }

  @Override
  public boolean addItem(T item) {
    boolean added = items.add(item);
    if(added) {
      items.sort(sortOrder);
    }

    return added;
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
    this.items.clear();
    this.items.addAll(items);
    this.items.sort(sortOrder);
  }

  @Override
  public boolean removeItem(T item) {
    return items.remove(item);
  }
}
