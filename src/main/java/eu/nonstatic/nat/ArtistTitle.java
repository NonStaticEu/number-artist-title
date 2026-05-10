/**
 * Number-Artist-Title
 * Copyright (C) 2026 NonStatic
 *
 * This file is part of number-artist-title.
 * number-artist-title is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with . If not, see <https://www.gnu.org/licenses/>.
 */
package eu.nonstatic.nat;

import static eu.nonstatic.util.StringUtils.trimToNull;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.NonNull;

public record ArtistTitle(String artist, String sep, String title, boolean multi) implements Comparable<ArtistTitle> {
  static final String ARTIST_TITLE_SEPARATOR_HYPHEN = "-";
  private static final Pattern PATTERN_SEPARATOR_HYPHEN = getSeparatorRegex(ARTIST_TITLE_SEPARATOR_HYPHEN);
  static final String ARTIST_TITLE_SEPARATOR_DASH = "–";
  private static final Pattern PATTERN_SEPARATOR_DASH = getSeparatorRegex(ARTIST_TITLE_SEPARATOR_DASH);

  static final String ARTIST_TITLE_SEPARATOR_DEFAULT = ARTIST_TITLE_SEPARATOR_HYPHEN;

  public static final List<String> SEPARATORS = List.of(' '+ARTIST_TITLE_SEPARATOR_DASH+' ', ' '+ARTIST_TITLE_SEPARATOR_HYPHEN+' '); // In order of preference


  public ArtistTitle {
    if(sep == null) {
      if(artist != null) {
        throw new IllegalArgumentException("sep is null and artist isn't");
      } else if(multi) {
        throw new IllegalArgumentException("sep is null but multi is true");
      }
    }
  }

  public ArtistTitle(String artist, String title) {
    this(artist, ARTIST_TITLE_SEPARATOR_DEFAULT, title, false);
  }

  public ArtistTitle(String artist, String sep, String title) {
    this(artist, sep, title, false);
  }

  public static ArtistTitle of(String str, @NonNull String sep) {
    if(str != null) {
      Pattern pattern = getSeparatorPattern(sep); // https://www.regular-expressions.info/shorthand.html#more
      Matcher matcher = pattern.matcher(str);
      if(matcher.find()) {
        int s = matcher.start();
        int e = matcher.end();
        String artist = str.substring(0, s);
        String title = str.substring(e);
        boolean multi = pattern.matcher(title).find();
        return new ArtistTitle(trimToNull(artist), sep, trimToNull(title), multi);
      }
    }
    return new ArtistTitle(null, null, trimToNull(str), false);
  }

  private static Pattern getSeparatorPattern(String sep) {
    return switch (sep) {
      case ARTIST_TITLE_SEPARATOR_HYPHEN -> PATTERN_SEPARATOR_HYPHEN;
      case ARTIST_TITLE_SEPARATOR_DASH -> PATTERN_SEPARATOR_DASH;
      default -> getSeparatorRegex(sep);
    };
  }

  private static Pattern getSeparatorRegex(String sep) {
    return Pattern.compile("\\h+" + sep + "\\h+");
  }

  public static ArtistTitle of(String str) {
    ArtistTitle at = ArtistTitle.of(str, ARTIST_TITLE_SEPARATOR_DASH);
    if(!at.split()) {
      at = ArtistTitle.of(str, ARTIST_TITLE_SEPARATOR_HYPHEN);
    }
    return at;
  }


  public ArtistTitle withArtist(String artist) {
    return new ArtistTitle(artist, sep, title, multi);
  }

  public ArtistTitle withSep(String sep) {
    return new ArtistTitle(artist, sep, title, multi);
  }

  public ArtistTitle withTitle(String title) {
    return new ArtistTitle(artist, sep, title, multi);
  }

  public boolean split() {
    return sep != null;
  }

  public String toArtistTitle() {
    StringBuilder sb = new StringBuilder();
    if(artist != null) {
      sb.append(artist);
    }
    if(sep != null) {
      sb.append(sep);
    }
    if(title != null) {
      sb.append(title);
    }
    return sb.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ArtistTitle n = (ArtistTitle) o;
    return Objects.equals(sep, n.sep)
        && Objects.equals(artist, n.artist)
        && Objects.equals(title, n.title);
  }

  @Override
  public int hashCode() {
    return Objects.hash(artist, sep, title);
  }

  @Override
  public int compareTo(ArtistTitle at) {
    return toArtistTitle().compareTo(at.toArtistTitle());
  }

  public int compareToIgnoreCase(ArtistTitle at) {
    return String.CASE_INSENSITIVE_ORDER.compare(toArtistTitle(), at.toArtistTitle());
  }
}