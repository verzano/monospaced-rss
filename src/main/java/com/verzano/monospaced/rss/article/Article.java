package com.verzano.monospaced.rss.article;

import com.google.gson.annotations.SerializedName;
import com.verzano.monospaced.gui.widget.scrollable.list.model.Stringable;

import java.util.Date;

public class Article implements Stringable {
  public static final long NULL_ARTICLE_ID = -1L;
  public static final Article NULL_ARTICLE = new Article(NULL_ARTICLE_ID, "", -1L, new Date(0), "", "", new Date(0));
  @SerializedName("id")
  private final long id;
  @SerializedName("uri")
  private final String uri;
  @SerializedName("source_id")
  private final long sourceId;
  @SerializedName("published_at")
  private final Date publishedAt;
  @SerializedName("title")
  private String title;
  @SerializedName("content")
  private String content;
  @SerializedName("updated_at")
  private Date updatedAt;

  public Article(long id, String uri, long sourceId, Date publishedAt, String title, String content, Date updatedAt) {
    this.id = id;
    this.uri = uri;
    this.sourceId = sourceId;
    this.publishedAt = publishedAt;
    this.title = title;
    this.content = content;
    this.updatedAt = updatedAt;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public long getId() {
    return id;
  }

  public Date getPublishedAt() {
    return publishedAt;
  }

  public long getSourceId() {
    return sourceId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Date getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Date updatedAt) {
    this.updatedAt = updatedAt;
  }

  public String getUri() {
    return uri;
  }

  @Override
  public String stringify() {
    return title;
  }
}
