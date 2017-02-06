package com.verzano.terminalrss.ui.widget.popup;

import com.verzano.terminalrss.content.ContentType;
import com.verzano.terminalrss.ui.TerminalUI;
import com.verzano.terminalrss.ui.metrics.Location;
import com.verzano.terminalrss.ui.metrics.Size;
import com.verzano.terminalrss.ui.task.key.KeyTask;
import com.verzano.terminalrss.ui.widget.TerminalWidget;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.verzano.terminalrss.ui.metrics.Size.MATCH_TERMINAL;
import static com.verzano.terminalrss.ui.widget.constants.Ansi.RESET;
import static com.verzano.terminalrss.ui.widget.constants.Ansi.REVERSE;
import static com.verzano.terminalrss.ui.widget.constants.Key.ENTER;
import static com.verzano.terminalrss.ui.widget.constants.Key.TAB;

// TODO make it either not resizable or that the get size is calculated...
public class AddSourcePopup extends TerminalWidget {
  private TextEntryWidget uriTextEntry;
  private RolodexWidget<ContentType> contentTypeRolodex;
  private TextEntryWidget contentTagEntry;

  private String emptyBar;

  public AddSourcePopup(KeyTask completeAction) {
    super(new Size(MATCH_TERMINAL, MATCH_TERMINAL), new Location(0, 0));
    uriTextEntry = new TextEntryWidget(new Size(30, 3), new Location(1, 1));
    uriTextEntry.addKeyAction(TAB, () -> {
      contentTypeRolodex.setFocused();
      reprint();
    });
    uriTextEntry.addKeyAction(ENTER, completeAction);

    List<ContentType> types = Arrays.stream(ContentType.values())
        .filter(ct -> ct != ContentType.NULL_TYPE)
        .collect(Collectors.toList());
    contentTypeRolodex = new RolodexWidget<>(
        types,
        new Size(20, 3),
        new Location(uriTextEntry.getWidth() + uriTextEntry.getX() + 1, 1));
    contentTypeRolodex.addKeyAction(TAB, () -> {
      contentTagEntry.setFocused();
      reprint();
    });
    contentTypeRolodex.addKeyAction(ENTER, completeAction);

    contentTagEntry = new TextEntryWidget(
        new Size(20, 3),
        new Location(contentTypeRolodex.getWidth() + contentTypeRolodex.getX() + 1, 1));
    contentTagEntry.addKeyAction(TAB, () -> {
      uriTextEntry.setFocused();
      reprint();
    });
    contentTagEntry.addKeyAction(ENTER, completeAction);

    setWidth(uriTextEntry.getWidth() + contentTypeRolodex.getWidth() + contentTagEntry.getWidth() + 4);
    setHeight(Math.max(uriTextEntry.getHeight(), Math.max(contentTypeRolodex.getHeight(), contentTagEntry.getHeight())) + 2);
    size();
  }

  public String getUri() {
    return uriTextEntry.getText();
  }

  public ContentType getContentType() {
    return contentTypeRolodex.getSelectedItem();
  }

  public String getContentTag() {
    return contentTagEntry.getText();
  }

  public void clear() {
    uriTextEntry.setText("");
    contentTagEntry.setText("");
    contentTypeRolodex.setSelectedIndex(0);
  }

  private void centerOnScreen() {
    setX(TerminalUI.getWidth()/2 - getWidth()/2);
    setY(TerminalUI.getHeight()/2 - getHeight()/2);
  }

  @Override
  public void setX(int x) {
    int delta = x - getX();
    uriTextEntry.setX(uriTextEntry.getX() + delta);
    contentTypeRolodex.setX(contentTypeRolodex.getX() + delta);
    contentTagEntry.setX(contentTagEntry.getX() + delta);
    super.setX(x);
  }

  @Override
  public void setY(int y) {
    int delta = y - getY();
    uriTextEntry.setY(uriTextEntry.getY() + delta);
    contentTypeRolodex.setY(contentTypeRolodex.getY() + delta);
    contentTagEntry.setY(contentTagEntry.getY() + delta);
    super.setY(y);
  }

  @Override
  public void print() {
    for (int row = 0; row < getHeight(); row++) {
      TerminalUI.move(getX(), getY() + row);
      TerminalUI.print(emptyBar);
    }

    uriTextEntry.print();
    contentTypeRolodex.print();
    contentTagEntry.print();
  }

  @Override
  public void size() {
    emptyBar = REVERSE + new String(new char[getWidth()]).replace('\0', ' ') + RESET;
    centerOnScreen();
  }

  @Override
  public void setFocused() {
    TerminalUI.setFocusedWidget(uriTextEntry);
  }
}
