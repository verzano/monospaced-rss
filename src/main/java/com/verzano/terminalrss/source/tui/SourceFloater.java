package com.verzano.terminalrss.source.tui;

import static com.verzano.terminalrss.tui.constants.Key.TAB;
import static com.verzano.terminalrss.tui.constants.Orientation.HORIZONTAL;
import static com.verzano.terminalrss.tui.metrics.Size.FILL_NEEDED;

import com.verzano.terminalrss.content.ContentType;
import com.verzano.terminalrss.source.Source;
import com.verzano.terminalrss.tui.TerminalUi;
import com.verzano.terminalrss.tui.ansi.Attribute;
import com.verzano.terminalrss.tui.container.shelf.Shelf;
import com.verzano.terminalrss.tui.container.shelf.ShelfOptions;
import com.verzano.terminalrss.tui.floater.binary.BinaryChoiceFloater;
import com.verzano.terminalrss.tui.metrics.Margins;
import com.verzano.terminalrss.tui.metrics.Size;
import com.verzano.terminalrss.tui.task.key.KeyTask;
import com.verzano.terminalrss.tui.widget.scrollable.list.model.BasicListModel;
import com.verzano.terminalrss.tui.widget.text.entry.RolodexWidget;
import com.verzano.terminalrss.tui.widget.text.entry.TextEntryWidget;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

// TODO this should probably extends the biChoiceFloater...
public class SourceFloater {

  private final KeyTask addSourceTask;
  private final KeyTask editSourceTask;
  private BinaryChoiceFloater floater;
  private TextEntryWidget uriTextEntry;
  private RolodexWidget<ContentType> contentTypeRolodex;
  private TextEntryWidget contentTagEntry;
  @Getter
  private Long sourceId = -1L;

  public SourceFloater(KeyTask addSourceTask, KeyTask editSourceTask, KeyTask cancelTask) {
    this.addSourceTask = addSourceTask;
    this.editSourceTask = editSourceTask;

    contentTagEntry = new TextEntryWidget();
    contentTagEntry.addKeyAction(TAB, () -> {
      floater.getPositiveButton().setFocused();
      TerminalUi.reprint();
    });

    List<ContentType> types = Arrays.stream(ContentType.values())
        .filter(ct -> ct != ContentType.NULL_TYPE)
        .collect(Collectors.toList());
    contentTypeRolodex = new RolodexWidget<>(new BasicListModel<>(types), 3, 3);
    contentTypeRolodex.addKeyAction(TAB, () -> {
      contentTagEntry.setFocused();
      TerminalUi.reprint();
    });

    uriTextEntry = new TextEntryWidget();
    uriTextEntry.addKeyAction(TAB, () -> {
      contentTypeRolodex.setFocused();
      TerminalUi.reprint();
    });

    Shelf displayWidget = new Shelf(HORIZONTAL, 1);
    displayWidget.addWidget(uriTextEntry, new ShelfOptions(new Size(30, FILL_NEEDED)));
    displayWidget.addWidget(contentTypeRolodex, new ShelfOptions(new Size(20, FILL_NEEDED)));
    displayWidget.addWidget(contentTagEntry, new ShelfOptions(new Size(20, FILL_NEEDED)));
    displayWidget.setMargins(new Margins(1));

    floater = new BinaryChoiceFloater(
        displayWidget,
        addSourceTask,
        "Add Source",
        cancelTask,
        "Cancel");

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

  public void setMode(boolean edit) {
    if (edit) {
      // TODO make group these into a single Object like Action in swing
      floater.getPositiveButton().setText("Save Source");
      floater.getPositiveButton().setOnPress(editSourceTask);
    } else {
      floater.getPositiveButton().setText("Add Source");
      floater.getPositiveButton().setOnPress(addSourceTask);
    }

    floater.arrange();
  }

  public void setSource(Source source) {
    sourceId = source.getId();
    uriTextEntry.setText(source.getUri());
    contentTypeRolodex.setSelectedItem(source.getContentType());
    contentTagEntry.setText(source.getContentTag());
  }

  public void clearSource() {
    sourceId = -1L;
    uriTextEntry.setText("");
    contentTypeRolodex.setSelectedIndex(0);
    contentTagEntry.setText("");
  }

  public void showFloater() {
    TerminalUi.setFloater(floater);
    TerminalUi.getFloater().reprint();
  }
}
