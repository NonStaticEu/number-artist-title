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

import static eu.nonstatic.util.StringUtils.emptyToNull;
import static eu.nonstatic.util.StringUtils.isEmpty;
import static eu.nonstatic.util.StringUtils.trimToNull;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.NonNull;

/**
 * @param artist
 * @param artistTitleSep
 * @param title
 * @param multi
 */
public record ArtistTitle(String artist, String artistTitleSep, String title, boolean multi) implements Comparable<ArtistTitle> {

  static final String ARTIST_TITLE_SEPARATOR_HYPHEN = "-";
  private static final Pattern PATTERN_SEPARATOR_HYPHEN = getSeparatorRegex(ARTIST_TITLE_SEPARATOR_HYPHEN);
  static final String ARTIST_TITLE_SEPARATOR_DASH = "–";
  private static final Pattern PATTERN_SEPARATOR_DASH = getSeparatorRegex(ARTIST_TITLE_SEPARATOR_DASH);

  static final String ARTIST_TITLE_SEPARATOR_DEFAULT = ' ' + ARTIST_TITLE_SEPARATOR_HYPHEN + ' ';

  public static final List<String> SEPARATORS = List.of(' ' + ARTIST_TITLE_SEPARATOR_DASH + ' ', ' ' + ARTIST_TITLE_SEPARATOR_HYPHEN + ' '); // In order of preference


  public ArtistTitle {
    if (artistTitleSep == null) {
      if (artist != null) {
        throw new IllegalArgumentException("sep is null and artist isn't");
      } else if (multi) {
        throw new IllegalArgumentException("sep is null but multi is true");
      }
    }
  }


  public static ArtistTitle ofExact(String str, @NonNull String sep) {
    if (str != null) {
      int i = str.indexOf(sep);
      if (i >= 0) {
        String artist = str.substring(0, i);
        String title = str.substring(i + sep.length());
        boolean multi = title.contains(sep);
        return new ArtistTitle(emptyToNull(artist), sep, emptyToNull(title), multi);
      }
    }
    return new ArtistTitle(null, null, emptyToNull(str), false);
  }

  public static ArtistTitle of(String str, @NonNull String sep) {
    if (str != null) {
      if (sep.trim().isEmpty()) {
        return ofExact(str, sep);
      } else {
        Pattern pattern = getSeparatorPattern(sep); // https://www.regular-expressions.info/shorthand.html#more
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
          int s = matcher.start();
          int e = matcher.end();
          String artist = str.substring(0, s);
          String title = str.substring(e);
          sep = str.substring(s, e);
          boolean multi = pattern.matcher(title).find();
          return new ArtistTitle(trimToNull(artist), sep, trimToNull(title), multi);
        }
      }
    }
    return new ArtistTitle(null, null, trimToNull(str), false);
  }

  private static Pattern getSeparatorPattern(@NonNull String sep) {
    return switch (sep.trim()) {
      case ARTIST_TITLE_SEPARATOR_HYPHEN -> PATTERN_SEPARATOR_HYPHEN;
      case ARTIST_TITLE_SEPARATOR_DASH -> PATTERN_SEPARATOR_DASH;
      default -> getSeparatorRegex(sep);
    };
  }

  private static Pattern getSeparatorRegex(@NonNull String sep) {
    return Pattern.compile("\\h+" + Pattern.quote(sep.trim()) + "\\h+");
  }

  public static ArtistTitle of(String str) {
    ArtistTitle at = ArtistTitle.of(str, ARTIST_TITLE_SEPARATOR_DASH);
    if (!at.split()) {
      at = ArtistTitle.of(str, ARTIST_TITLE_SEPARATOR_HYPHEN);
    }
    return at;
  }

  public String sepTrimmed() {
    return artistTitleSep != null ? artistTitleSep.trim() : null;
  }

  public ArtistTitle withArtist(String artist) {
    return new ArtistTitle(artist, artistTitleSep, title, multi);
  }

  public ArtistTitle withSep(String sep) {
    return new ArtistTitle(artist, sep, title, multi);
  }

  public ArtistTitle withTitle(String title) {
    return new ArtistTitle(artist, artistTitleSep, title, multi);
  }

  public boolean split() {
    return artist != null;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    if (artist != null) {
      sb.append(artist);
    }
    if (!isEmpty(title)) {
      if (!sb.isEmpty() && artistTitleSep != null) {
        sb.append(artistTitleSep);
      }
      sb.append(title);
    }

    return sb.toString();
  }

  public static ArtistTitleBuilder builder() {
    return new ArtistTitleBuilder();
  }

  ArtistTitleBuilder toBuilder() {
    return new ArtistTitleBuilder().artist(artist).artistTitleSep(artistTitleSep).title(title).multi(multi);
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ArtistTitle n = (ArtistTitle) o;
    return Objects.equals(artistTitleSep, n.artistTitleSep)
        && Objects.equals(artist, n.artist)
        && Objects.equals(title, n.title);
  }

  @Override
  public int hashCode() {
    return Objects.hash(artist, artistTitleSep, title);
  }

  @Override
  public int compareTo(ArtistTitle at) {
    return toString().compareTo(at.toString());
  }

  public int compareToIgnoreCase(ArtistTitle at) {
    return String.CASE_INSENSITIVE_ORDER.compare(toString(), at.toString());
  }


  public static class ArtistTitleBuilder {

    private String artist;
    private String artistTitleSep = ARTIST_TITLE_SEPARATOR_DEFAULT;
    private String title;
    private boolean multi;

    ArtistTitleBuilder() {
    }

    public ArtistTitleBuilder artist(String artist) {
      this.artist = artist;
      return this;
    }

    public ArtistTitleBuilder artistTitleSep(String artistTitleSep) {
      this.artistTitleSep = artistTitleSep;
      return this;
    }

    public ArtistTitleBuilder title(String title) {
      this.title = title;
      return this;
    }

    public ArtistTitleBuilder multi(boolean multi) {
      this.multi = multi;
      return this;
    }

    public ArtistTitle build() {
      return new ArtistTitle(this.artist, this.artistTitleSep, this.title, this.multi);
    }
  }
}