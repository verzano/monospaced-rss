package com.verzano.terminalrss.ui.widget;

import com.verzano.terminalrss.content.ContentType;
import com.verzano.terminalrss.ui.TerminalUI;
import com.verzano.terminalrss.ui.metrics.Size;
import com.verzano.terminalrss.ui.task.key.KeyTask;
import com.verzano.terminalrss.ui.widget.container.box.BoxContainer;
import com.verzano.terminalrss.ui.widget.floating.binary.BinaryChoiceFloater;
import com.verzano.terminalrss.ui.widget.valueentry.RolodexWidget;
import com.verzano.terminalrss.ui.widget.valueentry.TextEntryWidget;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.verzano.terminalrss.ui.metrics.Size.FILL_NEEDED;
import static com.verzano.terminalrss.ui.widget.constants.Direction.HORIZONTAL;
import static com.verzano.terminalrss.ui.widget.constants.Key.TAB;

// TODO make it either not resizable or that the get size is calculated...
public class AddSourceFloater extends BinaryChoiceFloater {
  private TextEntryWidget uriTextEntry;
  private RolodexWidget<ContentType> contentTypeRolodex;
  private TextEntryWidget contentTagEntry;

  public AddSourceFloater(KeyTask addSourceAction, KeyTask cancelAction) {
    super(
        NULL_WIDGET,
        addSourceAction, "Add Source",
        cancelAction, "Cancel",
        new Size(FILL_NEEDED, FILL_NEEDED));
    BoxContainer mainContainer = new BoxContainer(HORIZONTAL, new Size(FILL_NEEDED, FILL_NEEDED)) {
      @Override
      public void setFocused() {
        uriTextEntry.setFocused();
      }
    };

    uriTextEntry = new TextEntryWidget(new Size(30, 3));
    uriTextEntry.addKeyAction(TAB, () -> {
      contentTypeRolodex.setFocused();
      reprint();
    });

    List<ContentType> types = Arrays.stream(ContentType.values())
        .filter(ct -> ct != ContentType.NULL_TYPE)
        .collect(Collectors.toList());
    contentTypeRolodex = new RolodexWidget<>(
        types,
        new Size(20, 3));
    contentTypeRolodex.addKeyAction(TAB, () -> {
      contentTagEntry.setFocused();
      reprint();
    });

    contentTagEntry = new TextEntryWidget(
        new Size(20, 3));
    contentTagEntry.addKeyAction(TAB, () -> {
      getPositiveButton().setFocused();
      reprint();
    });

    mainContainer.addWidget(uriTextEntry);
    mainContainer.addWidget(contentTypeRolodex);
    mainContainer.addWidget(contentTagEntry);

    setDisplayWidget(mainContainer);
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

  @Override
  public void setFocused() {
    TerminalUI.setFocusedWidget(uriTextEntry);
  }
}
