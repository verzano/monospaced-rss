package dev.verzano.monospaced.rss.source;

import com.google.gson.annotations.SerializedName;
import dev.verzano.monospaced.gui.widget.scrollable.list.model.Stringable;
import dev.verzano.monospaced.rss.content.ContentType;

import java.util.Date;

public class Source implements Stringable {
    public static final long NULL_SOURCE_ID = -1L;
    public static final Source NULL_SOURCE = new Source(NULL_SOURCE_ID, "", ContentType.NULL_CONTENT_TYPE, "", new Date(0), "");
    @SerializedName("id")
    private final long id;
    @SerializedName("uri")
    private final String uri;
    @SerializedName("content_type")
    private ContentType contentType;
    @SerializedName("content_tag")
    private String contentTag;
    @SerializedName("published_date")
    private Date publishedDate;
    @SerializedName("title")
    private String title;

    public Source(long id, String uri, ContentType contentType, String contentTag, Date publishedDate, String title) {
        this.id = id;
        this.uri = uri;
        this.contentType = contentType;
        this.contentTag = contentTag;
        this.publishedDate = publishedDate;
        this.title = title;
    }

    public String getContentTag() {
        return contentTag;
    }

    public void setContentTag(String contentTag) {
        this.contentTag = contentTag;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    public long getId() {
        return id;
    }

    public Date getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(Date publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUri() {
        return uri;
    }

    @Override
    public String stringify() {
        return title;
    }
}
