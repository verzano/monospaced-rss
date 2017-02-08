package com.verzano.terminalrss.ui.widget.container;

import com.verzano.terminalrss.ui.metrics.Location;
import com.verzano.terminalrss.ui.metrics.Size;
import com.verzano.terminalrss.ui.widget.Widget;

import java.util.Collection;

// TODO make this abstract in some way so that other styles of arranging can be done
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
    getContainedWidgets().forEach(Widget::print);
  }
}
