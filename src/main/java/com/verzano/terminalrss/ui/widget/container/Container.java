package com.verzano.terminalrss.ui.widget.container;

import com.verzano.terminalrss.ui.metrics.Location;
import com.verzano.terminalrss.ui.metrics.Size;
import com.verzano.terminalrss.ui.widget.Widget;

import java.util.Collection;

// TODO eventually have the size cut off or wrap the contained widgets
public abstract class Container extends Widget {
  public abstract Collection<Widget> getContainedWidgets();

  public Container(Size size) {
    super(size);
  }

  // Overridden versions of this must call super.addWidget(widget)
  public void addWidget(Widget widget) {
    widget.setParent(this);
  }

  // Overridden versions of this must call super.removeWidgets()
  public void removeWidgets() {
    getContainedWidgets().forEach(w -> w.setParent(NULL_WIDGET));
  }

  @Override
  public void setLocation(Location location) {
    setX(location.getX());
    setY(location.getY());
  }

  @Override
  public void setX(int x) {
    int delta = x - getX();
    getContainedWidgets().forEach(w -> w.setX(w.getX() + delta));
    super.setX(x);
  }

  @Override
  public void setY(int y) {
    int delta = y - getY();
    getContainedWidgets().forEach(w -> w.setY(w.getY() + delta));
    super.setY(y);
  }

  @Override
  public void print() {
    super.print();
    getContainedWidgets().forEach(Widget::print);
  }

  @Override
  public void setFocused() {
    Collection<Widget> widgets = getContainedWidgets();
    if (widgets.size() > 0) {
      widgets.iterator().next().setFocused();
    } else {
      super.setFocused();
    }
  }
}
