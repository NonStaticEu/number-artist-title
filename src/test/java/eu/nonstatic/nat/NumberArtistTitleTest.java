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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import eu.nonstatic.nat.NumberArtistTitle.NumberArtistTitleBuilder;
import eu.nonstatic.utils.file.FileNameExt;
import org.junit.jupiter.api.Test;

class NumberArtistTitleTest {

  private static NumberArtistTitle nat(String number, String artist, String title) {
    return NumberArtistTitle.builder()
        .number(number)
        .artistTitle(ArtistTitle.builder().artist(artist).title(title).build())
        .build();
  }

  @Test
  void shouldParseNumberArtistTitle() {
    String fileName = "01 Matter-Energy-Space-Time - M.E.S.T.flac";
    NumberArtistTitle nat = NumberArtistTitle.of(fileName);
    assertEquals("01", nat.number());
    assertEquals(1, nat.numberish().numeric());
    assertEquals(" ", nat.numberArtistSep());
    assertEquals("Matter-Energy-Space-Time", nat.artist());
    assertEquals("M.E.S.T", nat.title());
    assertFalse(nat.multi());
    assertEquals("flac", nat.ext());
    assertEquals("01 Matter-Energy-Space-Time - M.E.S.T", nat.toString());
    assertEquals(fileName, nat.toFileName());
  }

  @Test
  void shouldParseFromFileNameExt() {
    NumberArtistTitle nat = NumberArtistTitle.of(new FileNameExt("042 Artist - Title", "mp3"));
    assertEquals("042", nat.number());
    assertEquals("Artist", nat.artist());
    assertEquals("Title", nat.title());
    assertEquals("mp3", nat.ext());
  }

  @Test
  void shouldTrimNumberArtistSeparators() {
    for (String fileName : new String[]{"01 - Artist - Title.mp3", "01. Artist - Title.mp3", "01 .-_ Artist - Title.mp3"}) {
      NumberArtistTitle nat = NumberArtistTitle.of(fileName);
      assertEquals("01", nat.number(), fileName);
      assertEquals("Artist", nat.artist(), fileName);
      assertEquals("Title", nat.title(), fileName);
    }
  }

  @Test
  void shouldParseVinylSideNumber() {
    NumberArtistTitle nat = NumberArtistTitle.of("B2 Artist - Title.mp3");
    assertEquals("B2", nat.number());
    assertEquals(Numberish.Type.LITERAL, nat.numberish().getType());
    assertEquals("Artist", nat.artist());
    assertEquals("Title", nat.title());
  }

  @Test
  void shouldParseWithoutNumber() {
    NumberArtistTitle nat = NumberArtistTitle.of("Artist – Title");
    assertTrue(nat.numberish().isEmpty());
    assertNull(nat.number());
    assertEquals("Artist", nat.artist());
    assertEquals("Title", nat.title());
    assertNull(nat.ext());
    assertEquals("Artist – Title", nat.toString());
    assertEquals("Artist – Title", nat.toFileName());
  }

  @Test
  void shouldParseWithoutArtist() {
    NumberArtistTitle nat = NumberArtistTitle.of("07 Title.ogg");
    assertEquals("07", nat.number());
    assertNull(nat.artist());
    assertEquals("Title", nat.title());
    assertEquals("07 Title", nat.toString());
  }

  @Test
  void shouldParseNumberOnly() {
    NumberArtistTitle nat = NumberArtistTitle.of("01 - .mp3");
    assertEquals("01", nat.number());
    assertNull(nat.artist());
    assertNull(nat.title());
    assertEquals("01", nat.toString());
  }

  @Test
  void shouldParseMulti() {
    NumberArtistTitle nat = NumberArtistTitle.of("03 Artist - Title - Remix.wav");
    assertEquals("Artist", nat.artist());
    assertEquals("Title - Remix", nat.title());
    assertTrue(nat.multi());
  }

  @Test
  void shouldParseNameNotMatchingPattern() {
    // '.' doesn't match line terminators, so the number pattern fails as a whole
    NumberArtistTitle nat = NumberArtistTitle.of("01 Artist - Title\nmore.mp3");
    assertTrue(nat.numberish().isEmpty());
    assertEquals("01 Artist", nat.artist());
    assertEquals("Title\nmore", nat.title());
    assertEquals("mp3", nat.ext());
  }

  @Test
  void shouldRejectNegativeNumber() {
    ArtistTitle at = ArtistTitle.of("Artist - Title");
    Numberish minusOne = new Numberish(-1);
    assertThrows(IllegalArgumentException.class, () -> new NumberArtistTitle(minusOne, " ", at, null));
    assertThrows(IllegalArgumentException.class, () -> natMinusOne(at));
  }

  private static NumberArtistTitle natMinusOne(ArtistTitle at) {
    return NumberArtistTitle.builder().number(-1).artistTitle(at).build();
  }

  @Test
  void shouldRejectNulls() {
    ArtistTitle at = ArtistTitle.of("Artist - Title");
    assertThrows(NullPointerException.class, () -> new NumberArtistTitle(null, " ", at, null));
    assertThrows(NullPointerException.class, () -> new NumberArtistTitle(Numberish.EMPTY, " ", null, null));
    assertThrows(NullPointerException.class, NumberArtistTitleTest::natNullArtistTitle);
  }

  private static NumberArtistTitleBuilder natNullArtistTitle() {
    return NumberArtistTitle.builder().artistTitle(null);
  }

  @Test
  void shouldBuild() {
    NumberArtistTitle nat = NumberArtistTitle.builder()
        .number(5)
        .numberArtistSeparator(". ")
        .artistTitle(ArtistTitle.of("Artist - Title"))
        .artist("Other Artist")
        .title("Other Title")
        .artistTitleSeparator(" – ")
        .ext("mp3")
        .build();
    assertEquals("5", nat.number());
    assertEquals(". ", nat.numberArtistSep());
    assertEquals("Other Artist", nat.artist());
    assertEquals("Other Title", nat.title());
    assertEquals(" – ", nat.artistTitle().artistTitleSep());
    assertEquals("mp3", nat.ext());
    assertEquals("5. Other Artist – Other Title", nat.toString());
    assertEquals("5. Other Artist – Other Title.mp3", nat.toFileName());
  }

  @Test
  void shouldBuildWithDefaults() {
    NumberArtistTitle nat = NumberArtistTitle.builder()
        .artistTitle(ArtistTitle.of("Title"))
        .build();
    assertTrue(nat.numberish().isEmpty());
    assertEquals(NumberArtistTitle.NUMBER_ARTIST_SEPARATOR_DEFAULT, nat.numberArtistSep());
    assertNull(nat.ext());
    assertEquals("Title", nat.toString());
  }

  @Test
  void shouldBuildNumbers() {
    ArtistTitle at = ArtistTitle.of("Title");
    assertEquals("12", NumberArtistTitle.builder().number(12).artistTitle(at).build().number());
    assertEquals("A1", NumberArtistTitle.builder().number("A1").artistTitle(at).build().number());
    assertEquals("012", NumberArtistTitle.builder().number(new Numberish("012")).artistTitle(at).build().number());
    assertThrows(NullPointerException.class, NumberArtistTitleTest::natNullNumberish);
  }

  private static NumberArtistTitleBuilder natNullNumberish() {
    return NumberArtistTitle.builder().number((Numberish) null);
  }

  @Test
  void shouldFormatNumberOnly() {
    NumberArtistTitle nat = NumberArtistTitle.builder()
        .number(3)
        .artistTitle(new ArtistTitle(null, null, null, false))
        .ext("mp3")
        .build();
    assertEquals("3", nat.toString());
    assertEquals("3.mp3", nat.toFileName());
  }

  @Test
  void shouldSanitizeFileName() {
    NumberArtistTitle nat = NumberArtistTitle.builder()
        .number(1)
        .artistTitle(ArtistTitle.builder().artist("AC/DC").title("Who: Are You? \"Live\"").build())
        .ext("mp3")
        .build();
    assertEquals("1 AC-DC - Who - Are You! 'Live'.mp3", nat.toFileName());
  }

  @Test
  void shouldWith() {
    NumberArtistTitle nat = NumberArtistTitle.of("01. Artist - Title.mp3");

    NumberArtistTitle n1 = nat.withNumber("02");
    assertEquals("02", n1.number());
    assertEquals(nat.numberArtistSep(), n1.numberArtistSep());
    assertEquals(nat.artistTitle(), n1.artistTitle());
    assertEquals(nat.ext(), n1.ext());

    assertEquals("B1", nat.withNumber(new Numberish("B1")).number());
    assertThrows(NullPointerException.class, () -> nat.withNumber((Numberish) null));

    NumberArtistTitle n2 = nat.withArtist("Other");
    assertEquals("Other", n2.artist());
    assertEquals("Title", n2.title());
    assertEquals(nat.numberish(), n2.numberish());

    NumberArtistTitle n3 = nat.withTitle("Other");
    assertEquals("Artist", n3.artist());
    assertEquals("Other", n3.title());

    ArtistTitle at = ArtistTitle.of("X – Y");
    assertEquals(at, nat.withArtistTitle(at).artistTitle());
    assertThrows(NullPointerException.class, () -> nat.withArtistTitle(null));

    NumberArtistTitle n4 = nat.withExt("flac");
    assertEquals("flac", n4.ext());
    assertEquals("01 Artist - Title.flac", n4.toFileName());

    assertEquals("01 Artist - Title.mp3", nat.toFileName()); // unchanged
  }

  @Test
  void shouldSanitizeFileNameGeneric() {
    assertEquals("a -b-c.d!e'f g h-i", NumberArtistTitle.sanitizeFileNameGeneric("a:b/c*d?e\"f<g|h\\i"));
    assertEquals("Artist - Title", NumberArtistTitle.sanitizeFileNameGeneric("Artist: Title"));
    assertEquals("a b", NumberArtistTitle.sanitizeFileNameGeneric("a<>b"));
    assertEquals("a - -b", NumberArtistTitle.sanitizeFileNameGeneric("a::b"));
    assertEquals("a -b", NumberArtistTitle.sanitizeFileNameGeneric("a<:b"));
    assertEquals("a b", NumberArtistTitle.sanitizeFileNameGeneric("a\tb"));
    assertEquals("-abc", NumberArtistTitle.sanitizeFileNameGeneric(":abc  "));
    assertEquals("Nothing to do", NumberArtistTitle.sanitizeFileNameGeneric("Nothing to do"));
  }

  @Test
  void shouldSanitizeFileNameUnix() {
    assertEquals("a:b-c d*e?", NumberArtistTitle.sanitizeFileNameUnix("a:b/c\0d*e?"));
    assertEquals("a--b", NumberArtistTitle.sanitizeFileNameUnix("a//b"));
  }

  @Test
  void shouldEquals() {
    NumberArtistTitle nat = NumberArtistTitle.of("01 Artist - Title.mp3");
    assertEquals(nat, nat);
    assertEquals(nat, NumberArtistTitle.of("01 Artist - Title.mp3"));
    assertEquals(nat.hashCode(), NumberArtistTitle.of("01 Artist - Title.mp3").hashCode());
    assertEquals(nat, nat.withNumber("1"));
    assertEquals(nat, NumberArtistTitle.builder().number("01").numberArtistSeparator(". ").artistTitle(nat.artistTitle()).ext("mp3").build());

    assertNotEquals(null, nat);
    assertNotEquals("01 Artist - Title.mp3", nat);
    assertNotEquals(nat, nat.withNumber("02"));
    assertNotEquals(nat, nat.withArtist("Other"));
    assertNotEquals(nat, nat.withTitle("Other"));
    assertNotEquals(nat, nat.withExt("flac"));
  }

  @Test
  void shouldCompare() {
    NumberArtistTitle nat = nat("2", "B", "B");
    assertEquals(0, nat.compareTo(nat("02", "B", "B")));
    assertTrue(nat.compareTo(nat("1", "Z", "Z")) > 0);
    assertTrue(nat.compareTo(nat("3", "A", "A")) < 0);
    assertTrue(nat.compareTo(nat("2", "A", "Z")) > 0);
    assertTrue(nat.compareTo(nat("2", "C", "A")) < 0);
    assertTrue(nat.compareTo(nat("2", "b", "b")) < 0);
    assertTrue(nat.compareTo(nat(null, "A", "A")) < 0); // numbered first
  }

  @Test
  void shouldCompareIgnoreCase() {
    NumberArtistTitle nat = nat("a1", "B", "B");
    assertEquals(0, nat.compareToIgnoreCase(nat("A1", "b", "b")));
    assertTrue(nat.compareToIgnoreCase(nat("A2", "A", "A")) < 0);
    assertTrue(nat.compareToIgnoreCase(nat("a1", "a", "z")) > 0);
    assertTrue(nat.compareToIgnoreCase(nat("a1", "c", "a")) < 0);
  }

  @Test
  void shouldBuildArtistAndTitleWithoutArtistTitle() {
    NumberArtistTitle nat = NumberArtistTitle.builder().number(1).title("Title").artist("Artist").ext("mp3").build();
    assertEquals("Artist", nat.artist());
    assertEquals("Title", nat.title());
    assertEquals("1 Artist - Title.mp3", nat.toFileName());
  }

  @Test
  void shouldBuildSeparatorBeforeArtist() {
    NumberArtistTitle nat = NumberArtistTitle.builder().artistTitleSeparator(" – ").artist("Artist").title("Title").build();
    assertEquals("Artist – Title", nat.toString());
  }

  @Test
  void shouldBuildTitleOnly() {
    assertEquals("1 Title", NumberArtistTitle.builder().number(1).title("Title").build().toString());
  }

  @Test
  void shouldBuildEmpty() {
    NumberArtistTitle nat = NumberArtistTitle.builder().build();
    assertTrue(nat.numberish().isEmpty());
    assertNull(nat.artist());
    assertNull(nat.title());
    assertEquals("", nat.toString());
  }
}
