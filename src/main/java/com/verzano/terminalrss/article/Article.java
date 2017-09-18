package com.verzano.terminalrss.article;

import com.google.gson.annotations.SerializedName;
import com.verzano.terminalrss.tui.TuiStringable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@AllArgsConstructor
public class Article implements TuiStringable {
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

  @Setter
  @SerializedName("title")
  private String title;

  @Setter
  @SerializedName("content")
  private String content;

  @Setter
  @SerializedName("updated_at")
  private Date updatedAt;

  @Override
  public String toTuiString() {
    return title;
  }
}
