package com.verzano.monospaced.rss.source.gui;

import com.verzano.monospaced.gui.MonospacedGui;
import com.verzano.monospaced.gui.ansi.AnsiFormat;
import com.verzano.monospaced.gui.ansi.Attribute;
import com.verzano.monospaced.gui.ansi.Background;
import com.verzano.monospaced.gui.ansi.Foreground;
import com.verzano.monospaced.gui.constant.Position;
import com.verzano.monospaced.gui.container.shelf.Shelf;
import com.verzano.monospaced.gui.container.shelf.ShelfOptions;
import com.verzano.monospaced.gui.floater.binary.BinaryChoiceFloater;
import com.verzano.monospaced.gui.metric.Size;
import com.verzano.monospaced.gui.metric.Spacing;
import com.verzano.monospaced.gui.widget.scrollable.list.model.BasicListModel;
import com.verzano.monospaced.gui.widget.text.entry.RolodexWidget;
import com.verzano.monospaced.gui.widget.text.entry.TextEntryWidget;
import com.verzano.monospaced.rss.content.ContentType;
import com.verzano.monospaced.rss.source.Source;

import static com.verzano.monospaced.gui.constant.Key.TAB;
import static com.verzano.monospaced.gui.constant.Orientation.HORIZONTAL;
import static com.verzano.monospaced.gui.metric.Size.FILL_NEEDED;
import static com.verzano.monospaced.rss.source.Source.NULL_SOURCE_ID;

public class EditSourceFloater extends BinaryChoiceFloater {
  private static final String ADD_SOURCE_TEXT = "Add Source";
  private static final String EDIT_SOURCE_TEXT = "Edit Source";
  private static final String CANCEL_TEXT = "Cancel";
  private final TextEntryWidget uriTextEntry = new TextEntryWidget();
  private final RolodexWidget<ContentType> contentTypeRolodex = new RolodexWidget<>(new BasicListModel<>(ContentType.nonNullValues()),
      0,
      1);
  private final TextEntryWidget contentTagEntry = new TextEntryWidget();
  private Long sourceId = NULL_SOURCE_ID;

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
    contentTagEntry.setLabel("Content Tag");
    contentTagEntry.setLabelPosition(Position.TOP_LEFT);
    contentTagEntry.setShowLabel(true);
    contentTagEntry.addKeyAction(TAB, () -> {
      getPositiveButton().setFocused();
      MonospacedGui.reprint();
    });

    contentTypeRolodex.setLabel("Content Type");
    contentTypeRolodex.setLabelPosition(Position.TOP_LEFT);
    contentTypeRolodex.setShowLabel(true);
    contentTypeRolodex.addKeyAction(TAB, () -> {
      contentTagEntry.setFocused();
      MonospacedGui.reprint();
    });

    uriTextEntry.setLabel("RSS Feed URL");
    uriTextEntry.setLabelPosition(Position.TOP_LEFT);
    uriTextEntry.setShowLabel(true);
    uriTextEntry.addKeyAction(TAB, () -> {
      contentTypeRolodex.setFocused();
      MonospacedGui.reprint();
    });

    Shelf displayWidget = new Shelf(HORIZONTAL, 1);
    displayWidget.setPadding(new Spacing(1, 1, 1, 0));
    displayWidget.addWidget(uriTextEntry, new ShelfOptions(new Size(30, FILL_NEEDED)));
    displayWidget.addWidget(contentTypeRolodex, new ShelfOptions(new Size(20, FILL_NEEDED)));
    displayWidget.addWidget(contentTagEntry, new ShelfOptions(new Size(20, FILL_NEEDED)));
    setDisplayWidget(displayWidget);

    setFocusedFormat(new AnsiFormat(Background.DEFAULT, Foreground.DEFAULT, Attribute.INVERSE_ON));
    setUnfocusedFormat(new AnsiFormat(Background.DEFAULT, Foreground.DEFAULT, Attribute.INVERSE_ON));
  }

  public String getContentTag() {
    return contentTagEntry.getText();
  }

  public ContentType getContentType() {
    return contentTypeRolodex.getSelectedItem();
  }

  public String getUri() {
    return uriTextEntry.getText();
  }

  public Long getSourceId() {
    return sourceId;
  }
}