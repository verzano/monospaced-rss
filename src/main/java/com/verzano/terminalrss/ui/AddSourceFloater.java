package com.verzano.terminalrss.ui;

import com.verzano.terminalrss.content.ContentType;
import com.verzano.terminalrss.ui.ansi.Attribute;
import com.verzano.terminalrss.ui.container.shelf.Shelf;
import com.verzano.terminalrss.ui.container.shelf.ShelfOptions;
import com.verzano.terminalrss.ui.floater.binary.BinaryChoiceFloater;
import com.verzano.terminalrss.ui.metrics.Size;
import com.verzano.terminalrss.ui.task.key.KeyTask;
import com.verzano.terminalrss.ui.widget.text.entry.RolodexWidget;
import com.verzano.terminalrss.ui.widget.text.entry.TextEntryWidget;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.verzano.terminalrss.ui.constants.Key.TAB;
import static com.verzano.terminalrss.ui.constants.Orientation.HORIZONTAL;
import static com.verzano.terminalrss.ui.metrics.Size.FILL_NEEDED;

public class AddSourceFloater {
  private BinaryChoiceFloater floater;

  private TextEntryWidget uriTextEntry;
  private RolodexWidget<ContentType> contentTypeRolodex;
  private TextEntryWidget contentTagEntry;

  public AddSourceFloater(KeyTask addSourceAction, KeyTask cancelAction) {
    Shelf displayWidget = new Shelf(HORIZONTAL, 1);

    uriTextEntry = new TextEntryWidget();
    uriTextEntry.addKeyAction(TAB, () -> {
      contentTypeRolodex.setFocused();
      TerminalUI.reprint();
    });

    List<ContentType> types = Arrays.stream(ContentType.values())
        .filter(ct -> ct != ContentType.NULL_TYPE)
        .collect(Collectors.toList());
    contentTypeRolodex = new RolodexWidget<>(types, 3, 3);
    contentTypeRolodex.addKeyAction(TAB, () -> {
      contentTagEntry.setFocused();
      TerminalUI.reprint();
    });

    contentTagEntry = new TextEntryWidget();
    contentTagEntry.addKeyAction(TAB, () -> {
      floater.getPositiveButton().setFocused();
      TerminalUI.reprint();
    });

    displayWidget.addWidget(uriTextEntry, new ShelfOptions(new Size(30, FILL_NEEDED)));
    displayWidget.addWidget(contentTypeRolodex, new ShelfOptions(new Size(20, FILL_NEEDED)));
    displayWidget.addWidget(contentTagEntry, new ShelfOptions(new Size(20, FILL_NEEDED)));

    floater = new BinaryChoiceFloater(displayWidget, addSourceAction, "Add Source", cancelAction, "Cancel");

    floater.getFocusedFormat().setAttributes(Attribute.INVERSE_ON);
    floater.getUnfocusedFormat().setAttributes(Attribute.INVERSE_ON);
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

  public void showFloater() {
    TerminalUI.setFloater(floater);
  }
}
