package com.verzano.terminalrss.ui.widget.text.entry;

import com.verzano.terminalrss.ui.metrics.Size;
import com.verzano.terminalrss.ui.widget.Widget;
import com.verzano.terminalrss.ui.widget.ansi.Attribute;
import com.verzano.terminalrss.ui.widget.text.TextWidget;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import static com.verzano.terminalrss.ui.widget.constants.Key.DOWN_ARROW;
import static com.verzano.terminalrss.ui.widget.constants.Key.UP_ARROW;
import static com.verzano.terminalrss.ui.widget.constants.Orientation.HORIZONTAL;
import static com.verzano.terminalrss.ui.widget.constants.Position.CENTER_LEFT;

// TODO both this and the list widget should be backed by some kind of list model
public class RolodexWidget<T> extends Widget {
  private TextWidget printableItem = new TextWidget("", HORIZONTAL, CENTER_LEFT, new Size(1, 1));

  private List<T> items;

  @Getter @Setter
  private volatile int selectedIndex = 0;

  @Getter @Setter
  private int itemsBefore;

  @Getter @Setter
  private int itemsAfter;

  public RolodexWidget(List<T> items, int itemsBefore, int itemsAfter, Size size) {
    super(size);
    this.items = items;
    this.itemsBefore = itemsBefore;
    this.itemsAfter = itemsAfter;

    printableItem.setSize(size);
    printableItem.setUnfocusedAttribute(Attribute.NORMAL);

    addKeyAction(UP_ARROW, () -> {
      selectedIndex = getPreviousIndex(selectedIndex);
      reprint();
    });

    addKeyAction(DOWN_ARROW, () -> {
      selectedIndex = getNextIndex(selectedIndex);
      reprint();
    });
  }

  public T getSelectedItem() {
    return items.get(selectedIndex);
  }

  private int getPreviousIndex(int index) {
    return index == 0 ? items.size() - 1 : index - 1;
  }

  private int getNextIndex(int index) {
    return index == items.size() - 1 ? 0 : index + 1;
  }

  private String cropOrPad(String s) {
    int maxWidth = getWidth();

    if (s.length() < maxWidth) {
      s += new String(new char[maxWidth - s.length()]).replace('\0', ' ');
    } else if (s.length() > maxWidth) {
      s = s.substring(0, maxWidth);
    }

    return s;
  }

  // TODO this gets a little fucky if any of the toStrings prints a newline... which nothing should ever do
  @Override
  public int getNeededWidth() {
    return items.stream()
        .mapToInt(item -> item.toString().length())
        .max()
        .orElseGet(() -> 0);
  }

  @Override
  public int getNeededHeight() {
    return 1;
  }

  // TODO this ignores height
  @Override
  public void print() {
    super.print();

    printableItem.setX(getX());
    if (isFocused()) {
      T item;
      int index = selectedIndex;
      printableItem.setUnfocusedAttribute(Attribute.NORMAL);
      for (int i = 1; i <= itemsBefore; i++) {
        index = getPreviousIndex(index);
        item = items.get(index);
        printableItem.setY(getY() - i);
        printableItem.setText(cropOrPad(item.toString()));
        printableItem.print();
      }

      printableItem.setUnfocusedAttribute(Attribute.INVERSE_ON);
      item = items.get(selectedIndex);
      printableItem.setY(getY());
      printableItem.setText(cropOrPad(item.toString()));
      printableItem.print();

      index = selectedIndex;
      printableItem.setUnfocusedAttribute(Attribute.NORMAL);
      for (int i = 1; i <= itemsAfter; i++) {
        index = getNextIndex(index);
        item = items.get(index);
        printableItem.setY(getY() + i);
        printableItem.setText(cropOrPad(item.toString()));
        printableItem.print();
      }
    } else {
      T item = items.get(selectedIndex);
      printableItem.setY(getY());
      printableItem.setText(cropOrPad(item.toString()));
      printableItem.print();
    }
  }
}
