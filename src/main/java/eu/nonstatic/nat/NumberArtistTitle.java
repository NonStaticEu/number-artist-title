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

import eu.nonstatic.utils.file.FileNameExt;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.NonNull;

public record NumberArtistTitle(@NonNull Numberish numberish, @NonNull ArtistTitle artistTitle, String ext)
    implements Comparable<NumberArtistTitle> {
  // Keep ~aligned with TrackNumberAndTotal
  private static final Pattern TRACK_NUMBER_PATTERN = Pattern.compile("^([A-H]{1,2}\\d{0,2}\\b|\\d{2,3}\\b)?(.*)", Pattern.CASE_INSENSITIVE);
  public static final String NUMBER_ARTIST_SEPARATOR_DEFAULT = " ";
  private static final char[] NUMBER_ARTIST_SEPARATORS = {' ', '.', '-', '_'};

  private static final String FILENAME_REPLACEMENT_SEPARATOR = "-";
  private static final String FILENAME_REPLACEMENT_ASTERISK = ".";
  private static final String FILENAME_REPLACEMENT_QUESTION_MARK = "!";
  private static final String FILENAME_REPLACEMENT_QUOTE = "'";
  private static final String FILENAME_REPLACEMENT_OTHER = " ";

  public NumberArtistTitle {
    if(numberish.isNegative()) {
      throw new IllegalArgumentException("Number is negative: " + numberish);
    }
  }

  public NumberArtistTitle(int number, String artist, String title) {
    this(number, artist, title, null);
  }

  public NumberArtistTitle(int number, String artist, String title, String ext) {
    this(new Numberish(number), new ArtistTitle(artist, title), ext);
  }

  public NumberArtistTitle(String number, String artist, String title) {
    this(number, artist, title, null);
  }

  public NumberArtistTitle(String number, String artist, String title, String ext) {
    this(new Numberish(number), new ArtistTitle(artist, title), ext);
  }

  public static NumberArtistTitle of(String fileName) {
    FileNameExt fileNameExt = FileNameExt.of(fileName);
    return of(fileNameExt);
  }

  public static NumberArtistTitle of(FileNameExt fileNameExt) {
    Matcher matcher = TRACK_NUMBER_PATTERN.matcher(fileNameExt.name());
    if (matcher.matches()) {
      // It will only fail when the track has no number and the artist begins with one, like "12 Moons - Northern Star"
      String g1 = matcher.group(1); // number
      String g2 = matcher.group(2); // [artist - ]title
      return new NumberArtistTitle(new Numberish(g1), ArtistTitle.of(trimNumSep(g2)), fileNameExt.ext());
    } else {
      return new NumberArtistTitle(Numberish.EMPTY, ArtistTitle.of(fileNameExt.name()), fileNameExt.ext());
    }
  }

  private static String trimNumSep(String str) {
    char[] chars = str.toCharArray();
    int i = 0;
    for (; i < chars.length; i++) {
      char c = chars[i];
      if(!contains(c, NUMBER_ARTIST_SEPARATORS)) {
        break;
      }
    }
    return str.substring(i);
  }

  private static boolean contains(char needle, char... haystack) {
    for (char c : haystack) {
      if (c == needle) {
        return true;
      }
    }
    return false;
  }


  public String number() {
    return numberish.text();
  }

  public String artist() {
    return artistTitle.artist();
  }

  public String title() {
    return artistTitle.title();
  }

  public boolean multi() {
    return artistTitle.multi();
  }


  public NumberArtistTitle withNumber(String number) {
    return withNumber(new Numberish(number));
  }

  public NumberArtistTitle withNumber(@NonNull Numberish numberish) {
    return new NumberArtistTitle(numberish, artistTitle, ext);
  }

  public NumberArtistTitle withArtist(String artist) {
    return new NumberArtistTitle(numberish, artistTitle.withArtist(artist), ext);
  }

  public NumberArtistTitle withTitle(String title) {
    return new NumberArtistTitle(numberish, artistTitle.withTitle(title), ext);
  }

  public NumberArtistTitle withArtistTitle(@NonNull ArtistTitle artistTitle) {
    return new NumberArtistTitle(numberish, artistTitle, ext);
  }

  public NumberArtistTitle withExt(String ext) {
    return new NumberArtistTitle(numberish, artistTitle, ext);
  }


  public String toNumberArtistTitle() {
    StringBuilder sb = new StringBuilder();
    if(!numberish.isEmpty()) {
      sb.append(numberish.text());
    }

    String at = artistTitle.toArtistTitle();
    if(!at.isEmpty()) {
      if(!sb.isEmpty()) {
        sb.append(NUMBER_ARTIST_SEPARATOR_DEFAULT);
      }
      sb.append(at);
    }

    return sb.toString();
  }

  public String toFileName() {
    return new FileNameExt(sanitizeFileNameGeneric(toNumberArtistTitle()), ext).toFileName();
  }


  /**
   * Transforms any string into a filesystem compatible one, with an opinionated replacement scheme
   * Eg: ":abc/def* *ghi: jkl- mno \\/ pqr " => "-abc-def ghi - jkl- mno -- pqr"
   * There is a different algorithm on tools/FileUtils#escapeForFileName with a similar result except the ?>! substitution
   */
  private static String sanitizeFileName(CharSequence cs, Pattern pattern) {
    var sb = new StringBuilder();

    var matcher = pattern.matcher(cs);
    while(matcher.find()) {
      var repl = new StringBuilder();
      boolean spaced = false;
      for (char c : matcher.group().toCharArray()) {
        if(c == ' ') {
          if(!spaced) {
            spaced = true;
            repl.append(FILENAME_REPLACEMENT_OTHER);
          }
        } else if(c == ':') {
          if(!spaced) {
            repl.append(FILENAME_REPLACEMENT_OTHER);
          }
          spaced = false;
          repl.append(FILENAME_REPLACEMENT_SEPARATOR);
        } else if(c == '*') {
          spaced = false;
          repl.append(FILENAME_REPLACEMENT_ASTERISK);
        } else if(c == '?') {
          spaced = false;
          repl.append(FILENAME_REPLACEMENT_QUESTION_MARK);
        } else if(c == '/' || c == '\\') {
          spaced = false;
          repl.append(FILENAME_REPLACEMENT_SEPARATOR);
        } else if(c == '"') {
          spaced = false;
          repl.append(FILENAME_REPLACEMENT_QUOTE);
        } else if(!spaced) {
          spaced = true;
          repl.append(FILENAME_REPLACEMENT_OTHER);
        }
      }
      matcher.appendReplacement(sb, repl.toString());
    }
    matcher.appendTail(sb);
    return sb.toString().trim();
  }

  public static String sanitizeFileNameGeneric(CharSequence cs) {
    return sanitizeFileName(cs, FileNameExt.TO_FILENAME_PATTERN_GENERIC);
  }

  public static String sanitizeFileNameUnix(CharSequence cs) {
    return sanitizeFileName(cs, FileNameExt.TO_FILENAME_PATTERN_UNIX);
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NumberArtistTitle n = (NumberArtistTitle) o;
    return Objects.equals(ext, n.ext)
        && Objects.equals(numberish, n.numberish)
        && Objects.equals(artistTitle, n.artistTitle);
  }

  @Override
  public int hashCode() {
    return Objects.hash(numberish, artistTitle, ext);
  }

  @Override
  public int compareTo(NumberArtistTitle nat) {
    int result = numberish.compareTo(nat.numberish);
    if(result == 0) {
      result = artistTitle.compareTo(nat.artistTitle);
    }
    return result;
  }

  public int compareToIgnoreCase(NumberArtistTitle nat) {
    int result = numberish.compareToIgnoreCase(nat.numberish);
    if(result == 0) {
      result = artistTitle.compareToIgnoreCase(nat.artistTitle);
    }
    return result;
  }
}
