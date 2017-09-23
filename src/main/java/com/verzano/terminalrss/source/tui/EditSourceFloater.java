package com.verzano.terminalrss.source.tui;

import static com.verzano.terminalrss.source.Source.NULL_SOURCE_ID;
import static com.verzano.terminalrss.tui.constant.Key.TAB;
import static com.verzano.terminalrss.tui.constant.Orientation.HORIZONTAL;
import static com.verzano.terminalrss.tui.metric.Size.FILL_NEEDED;

import com.verzano.terminalrss.content.ContentType;
import com.verzano.terminalrss.source.Source;
import com.verzano.terminalrss.tui.TerminalUi;
import com.verzano.terminalrss.tui.ansi.Attribute;
import com.verzano.terminalrss.tui.container.shelf.Shelf;
import com.verzano.terminalrss.tui.container.shelf.ShelfOptions;
import com.verzano.terminalrss.tui.floater.binary.BinaryChoiceFloater;
import com.verzano.terminalrss.tui.metric.Size;
import com.verzano.terminalrss.tui.metric.Spacing;
import com.verzano.terminalrss.tui.widget.scrollable.list.model.BasicListModel;
import com.verzano.terminalrss.tui.widget.text.entry.RolodexWidget;
import com.verzano.terminalrss.tui.widget.text.entry.TextEntryWidget;
import lombok.Getter;

public class EditSourceFloater extends BinaryChoiceFloater {
  private static final String ADD_SOURCE_TEXT = "Add Source";
  private static final String EDIT_SOURCE_TEXT = "Edit Source";
  private static final String CANCEL_TEXT = "Cancel";
  private final TextEntryWidget uriTextEntry = new TextEntryWidget();
  private final RolodexWidget<ContentType> contentTypeRolodex = new RolodexWidget<>(new BasicListModel<>(ContentType.nonNullValues()), 0, 1);
  private final TextEntryWidget contentTagEntry = new TextEntryWidget();
  @Getter private Long sourceId = NULL_SOURCE_ID;

  public EditSourceFloater() {
    this(ADD_SOURCE_TEXT, CANCEL_TEXT);
  }

  public EditSourceFloater(Source source) {
    this(EDIT_SOURCE_TEXT, CANCEL_TEXT);

    sourceId = source.getId();
    uriTextEntry.setText(source.getUri());
    contentTypeRolodex.setSelectedItem(source.getContentType());
    contentTagEntry.setText(source.getContentTag());
  }

  private EditSourceFloater(String positiveText, String negativeText) {
    super(NULL_WIDGET, positiveText, negativeText);
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
    displayWidget.setPadding(new Spacing(1, 1, 1, 0));
    displayWidget.addWidget(uriTextEntry, new ShelfOptions(new Size(30, FILL_NEEDED)));
    displayWidget.addWidget(contentTypeRolodex, new ShelfOptions(new Size(20, FILL_NEEDED)));
    displayWidget.addWidget(contentTagEntry, new ShelfOptions(new Size(20, FILL_NEEDED)));
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
}
