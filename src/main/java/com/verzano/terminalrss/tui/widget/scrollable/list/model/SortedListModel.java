package com.verzano.terminalrss.tui.widget.scrollable.list.model;

import com.verzano.terminalrss.tui.TUIStringable;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

// TODO return copies so that you can't dick with stuff
public class SortedListModel<T extends TUIStringable> implements ListModel<T> {
  private List<T> items = new LinkedList<>();
  private Comparator<T> sortOrder;

  public SortedListModel(Comparator<T> sortOrder) {
    this.sortOrder = sortOrder;
  }

  @Override
  public void addItem(T item) {
    items.add(item);
    items.sort(sortOrder);
  }

  @Override
  public T getItemAt(int index) {
    return items.get(index);
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
  public int getItemCount() {
    return items.size();
  }
}
