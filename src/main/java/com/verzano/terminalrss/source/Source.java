package com.verzano.terminalrss.source;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@AllArgsConstructor
public class Source {
  public static final Source NULL_SOURCE = new Source(-1L, "", "", new Date(0), "");

  @SerializedName("id")
  private final long id;

  @SerializedName("uri")
  private final String uri;

  @SerializedName("content_tag")
  private String contentTag;

  @Setter
  @SerializedName("published_date")
  private Date publishedDate;

  @Setter
  @SerializedName("title")
  private String title;

  @Override
  public String toString() {
    return title;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Source source = (Source) o;

    return uri.equals(source.uri);
  }

  @Override
  public int hashCode() {
    return uri.hashCode();
  }
}
