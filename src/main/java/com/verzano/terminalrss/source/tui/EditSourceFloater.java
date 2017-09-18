package com.verzano.terminalrss.source.tui;

import com.verzano.terminalrss.content.ContentType;
import com.verzano.terminalrss.source.Source;
import com.verzano.terminalrss.tui.TerminalUi;
import com.verzano.terminalrss.tui.ansi.Attribute;
import com.verzano.terminalrss.tui.container.shelf.Shelf;
import com.verzano.terminalrss.tui.container.shelf.ShelfOptions;
import com.verzano.terminalrss.tui.floater.binary.BinaryChoiceFloater;
import com.verzano.terminalrss.tui.metric.Margins;
import com.verzano.terminalrss.tui.metric.Size;
import com.verzano.terminalrss.tui.task.key.KeyTask;
import com.verzano.terminalrss.tui.widget.scrollable.list.model.BasicListModel;
import com.verzano.terminalrss.tui.widget.text.entry.RolodexWidget;
import com.verzano.terminalrss.tui.widget.text.entry.TextEntryWidget;
import lombok.Getter;

import static com.verzano.terminalrss.source.Source.NULL_SOURCE_ID;
import static com.verzano.terminalrss.tui.constant.Key.TAB;
import static com.verzano.terminalrss.tui.constant.Orientation.HORIZONTAL;
import static com.verzano.terminalrss.tui.metric.Size.FILL_NEEDED;

public class EditSourceFloater extends BinaryChoiceFloater {
  private final KeyTask addSourceTask;
  private final KeyTask editSourceTask;
  private final TextEntryWidget uriTextEntry = new TextEntryWidget();
  private final RolodexWidget<ContentType> contentTypeRolodex = new RolodexWidget<>(
      new BasicListModel<>(ContentType.nonNullValues()),
      0,
      1);
  private final TextEntryWidget contentTagEntry = new TextEntryWidget();
  @Getter
  private Long sourceId = NULL_SOURCE_ID;

  public EditSourceFloater(KeyTask addSourceTask, KeyTask editSourceTask, KeyTask cancelTask) {
    super(NULL_WIDGET, addSourceTask, "Add Source", cancelTask, "Cancel");
    this.addSourceTask = addSourceTask;
    this.editSourceTask = editSourceTask;

    contentTagEntry.addKeyAction(TAB, () -> {
      getPositiveButton().setFocused();
      TerminalUi.reprint();
    });

    contentTypeRolodex.addKeyAction(TAB, () -> {
      contentTagEntry.setFocused();
      TerminalUi.reprint();
    });

    uriTextEntry.addKeyAction(TAB, () -> {
      contentTypeRolodex.setFocused();
      TerminalUi.reprint();
    });

    Shelf displayWidget = new Shelf(HORIZONTAL, 1);
    displayWidget.addWidget(uriTextEntry, new ShelfOptions(new Size(30, FILL_NEEDED)));
    displayWidget.addWidget(contentTypeRolodex, new ShelfOptions(new Size(20, FILL_NEEDED)));
    displayWidget.addWidget(contentTagEntry, new ShelfOptions(new Size(20, FILL_NEEDED)));
    displayWidget.setMargins(new Margins(1));
    setDisplayWidget(displayWidget);

    getFocusedFormat().setAttributes(Attribute.INVERSE_ON);
    getUnfocusedFormat().setAttributes(Attribute.INVERSE_ON);
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

  public void setMode(boolean edit) {
    if (edit) {
      // TODO make group these into a single Object like Action in swing
      getPositiveButton().setText("Save Source");
      getPositiveButton().setOnPress(editSourceTask);
    } else {
      getPositiveButton().setText("Add Source");
      getPositiveButton().setOnPress(addSourceTask);
    }

    arrange();
  }

  public void setSource(Source source) {
    sourceId = source.getId();
    uriTextEntry.setText(source.getUri());
    contentTypeRolodex.setSelectedItem(source.getContentType());
    contentTagEntry.setText(source.getContentTag());
  }

  public void clearSource() {
    sourceId = NULL_SOURCE_ID;
    uriTextEntry.setText("");
    contentTypeRolodex.setSelectedIndex(0);
    contentTagEntry.setText("");
  }

  public void showFloater() {
    TerminalUi.setFloater(this);
    TerminalUi.getFloater().reprint();
  }
}
