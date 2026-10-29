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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class ArtistTitleTest {

  @Test
  void shouldSplit() {
    String dirName = "Matter-Energy-Space-Time  -\t M.E.S.T";
    ArtistTitle at = ArtistTitle.of(dirName);
    assertEquals("Matter-Energy-Space-Time", at.artist());
    assertEquals("M.E.S.T", at.title());
    assertEquals("  -\t ", at.artistTitleSep());
    assertEquals(dirName, at.toString());
  }

  @Test
  void shouldNotSplit() {
    String dirName = "Albert Hoffman Likes Biking";
    ArtistTitle at = ArtistTitle.of(dirName);
    assertNull(at.artist());
    assertEquals(dirName, at.title());
    assertNull(at.artistTitleSep());
    assertEquals(dirName, at.toString());
  }

  @Test
  void shouldSplitRegex() {
    String dirName = "Matter-Energy-Space-Time  -\t M.E.S.T";
    ArtistTitle at = ArtistTitle.of(dirName, "-\t");
    assertEquals("Matter-Energy-Space-Time", at.artist());
    assertEquals("M.E.S.T", at.title());
    assertEquals("  -\t ", at.artistTitleSep());
    assertEquals("-", at.sepTrimmed());
    assertEquals(dirName, at.toString());
  }

  @Test
  void shouldSplitRegexTrimmable() {
    String dirName = "Matter-Energy-Space-Time  -\t M.E.S.T";
    ArtistTitle at = ArtistTitle.of(dirName, "\t");
    assertEquals("Matter-Energy-Space-Time  -", at.artist());
    assertEquals(" M.E.S.T", at.title());
    assertEquals("\t", at.artistTitleSep());
    assertTrue(at.sepTrimmed().isEmpty());
    assertEquals(dirName, at.toString());
  }

  @Test
  void shouldSplitRegexSpaced() {
    String dirName = "         ";
    ArtistTitle at = ArtistTitle.of(dirName, "€");
    assertNull(at.artist());
    assertNull(at.title());
    assertNull(at.artistTitleSep());
    assertNull(at.sepTrimmed());
    assertTrue(at.toString().isEmpty());
  }

  @Test
  void shouldNotSplitRegex() {
    String dirName = "Albert Hoffman Likes Biking";
    ArtistTitle at = ArtistTitle.of(dirName, "€");
    assertNull(at.artist());
    assertEquals(dirName, at.title());
    assertNull(at.artistTitleSep());
    assertNull(at.sepTrimmed());
    assertEquals(dirName, at.toString());
  }

  @Test
  void shouldSplitExact() {
    String dirName = "Matter-Energy-Space-Time  -\t M.E.S.T";
    ArtistTitle at = ArtistTitle.ofExact(dirName, " -");
    assertEquals("Matter-Energy-Space-Time ", at.artist());
    assertEquals("\t M.E.S.T", at.title());
    assertEquals(" -", at.artistTitleSep());
    assertEquals("-", at.sepTrimmed());
    assertEquals(dirName, at.toString());
  }

  @Test
  void shouldSplitExactTrimmable() {
    String dirName = "Matter-Energy-Space-Time\t  ";
    ArtistTitle at = ArtistTitle.ofExact(dirName, "$");
    assertNull(at.artist());
    assertEquals("Matter-Energy-Space-Time\t  ", at.title());
    assertNull(at.artistTitleSep());
    assertNull(at.sepTrimmed());
    assertEquals(dirName, at.toString());
  }

  @Test
  void shouldSplitExactSpaced() {
    String dirName = "         ";
    ArtistTitle at = ArtistTitle.ofExact(dirName, "$");
    assertNull(at.artist());
    assertEquals("         ", at.title());
    assertNull(at.artistTitleSep());
    assertNull(at.sepTrimmed());
    assertEquals(dirName, at.toString());
  }

  @Test
  void shouldNotSplitExact() {
    String dirName = "Albert Hoffman Likes Biking";
    ArtistTitle at = ArtistTitle.ofExact(dirName, "-");
    assertNull(at.artist());
    assertEquals(dirName, at.title());
    assertNull(at.artistTitleSep());
    assertNull(at.sepTrimmed());
    assertEquals(dirName, at.toString());
  }

  @Test
  void shouldPreferDash() {
    ArtistTitle at = ArtistTitle.of("Artist - Name – Title");
    assertEquals("Artist - Name", at.artist());
    assertEquals(" – ", at.artistTitleSep());
    assertEquals("Title", at.title());
    assertFalse(at.multi());
  }

  @Test
  void shouldDetectMulti() {
    ArtistTitle at = ArtistTitle.of("Artist - Title - Remix");
    assertEquals("Artist", at.artist());
    assertEquals("Title - Remix", at.title());
    assertTrue(at.multi());

    at = ArtistTitle.of("Artist – Title – Remix", "–");
    assertEquals("Title – Remix", at.title());
    assertTrue(at.multi());

    at = ArtistTitle.ofExact("Artist-Title-Remix", "-");
    assertEquals("Artist", at.artist());
    assertEquals("Title-Remix", at.title());
    assertTrue(at.multi());
  }

  @Test
  void shouldSplitCustomSeparator() {
    ArtistTitle at = ArtistTitle.of("Artist / Title", "/");
    assertEquals("Artist", at.artist());
    assertEquals(" / ", at.artistTitleSep());
    assertEquals("Title", at.title());
  }

  @Test
  void shouldSplitWithoutArtistOrTitle() {
    ArtistTitle at = ArtistTitle.ofExact("-Title", "-");
    assertNull(at.artist());
    assertEquals("Title", at.title());
    assertFalse(at.split());
    assertEquals("Title", at.toString());

    at = ArtistTitle.ofExact("Artist-", "-");
    assertEquals("Artist", at.artist());
    assertNull(at.title());
    assertEquals("Artist", at.toString());
  }

  @Test
  void shouldHandleNullAndEmpty() {
    for (ArtistTitle at : new ArtistTitle[]{ArtistTitle.of(null), ArtistTitle.of(null, "-"), ArtistTitle.ofExact(null, "-"), ArtistTitle.of(""), ArtistTitle.ofExact("", "-")}) {
      assertNull(at.artist());
      assertNull(at.artistTitleSep());
      assertNull(at.title());
      assertFalse(at.split());
      assertFalse(at.multi());
      assertEquals("", at.toString());
    }
    assertThrows(NullPointerException.class, () -> ArtistTitle.of("a", null));
    assertThrows(NullPointerException.class, () -> ArtistTitle.ofExact("a", null));
  }

  @Test
  void shouldRejectArtistOrMultiWithoutSeparator() {
    assertThrows(IllegalArgumentException.class, () -> new ArtistTitle("Artist", null, "Title", false));
    assertThrows(IllegalArgumentException.class, () -> new ArtistTitle(null, null, "Title", true));
    assertEquals("Title", new ArtistTitle(null, null, "Title", false).toString());
  }

  @Test
  void shouldBuild() {
    ArtistTitle at = ArtistTitle.builder()
        .artist("Artist")
        .artistTitleSep(" – ")
        .title("Title - Remix")
        .multi(true)
        .build();
    assertEquals("Artist", at.artist());
    assertEquals(" – ", at.artistTitleSep());
    assertEquals("Title - Remix", at.title());
    assertTrue(at.multi());
    assertEquals("Artist – Title - Remix", at.toString());
  }

  @Test
  void shouldBuildWithDefaultSeparator() {
    ArtistTitle at = ArtistTitle.builder().artist("Artist").title("Title").build();
    assertEquals(ArtistTitle.ARTIST_TITLE_SEPARATOR_DEFAULT, at.artistTitleSep());
    assertFalse(at.multi());
    assertEquals("Artist - Title", at.toString());
    assertEquals(at, ArtistTitle.of("Artist - Title"));
  }

  @Test
  void shouldBuildWithoutSeparator() {
    ArtistTitle at = ArtistTitle.builder().artistTitleSep(null).title("Title").build();
    assertFalse(at.split());
    assertEquals("Title", at.toString());
    Executable noTitleGen = () -> ArtistTitle.builder().artistTitleSep(null).artist("Artist").build();
    assertThrows(IllegalArgumentException.class, noTitleGen);
    Executable noSepGen = () -> ArtistTitle.builder().artistTitleSep(null).multi(true).build();
    assertThrows(IllegalArgumentException.class, noSepGen);
  }

  @Test
  void shouldWith() {
    ArtistTitle at = ArtistTitle.of("Artist - Title");
    assertEquals("Artist - Other", at.withTitle("Other").toString());
    assertEquals("Other - Title", at.withArtist("Other").toString());
    assertEquals("Artist – Title", at.withSep(" – ").toString());
    assertEquals("Artist - Title", at.toString()); // unchanged
    assertThrows(IllegalArgumentException.class, () -> at.withSep(null));
  }

  @Test
  void shouldEquals() {
    ArtistTitle at = ArtistTitle.of("Artist - Title");
    assertEquals(at, at);
    assertEquals(at, ArtistTitle.of("Artist - Title"));
    assertEquals(at.hashCode(), ArtistTitle.of("Artist - Title").hashCode());
    assertNotEquals(at, ArtistTitle.of("Artist  -  Title"));
    assertNotEquals(at, ArtistTitle.of("Artist - Other"));
    assertNotEquals(at, ArtistTitle.of("Other - Title"));
    assertNotEquals("Artist - Title", at);
    assertNotEquals(null, at);
  }

  @Test
  void shouldCompare() {
    ArtistTitle at = ArtistTitle.of("B - B");
    assertEquals(0, at.compareTo(ArtistTitle.of("B - B")));
    assertTrue(at.compareTo(ArtistTitle.of("A - Z")) > 0);
    assertTrue(at.compareTo(ArtistTitle.of("B - C")) < 0);
    assertTrue(at.compareTo(ArtistTitle.of("b - b")) < 0);

    assertEquals(0, at.compareToIgnoreCase(ArtistTitle.of("b - b")));
    assertTrue(at.compareToIgnoreCase(ArtistTitle.of("a - z")) > 0);
    assertTrue(at.compareToIgnoreCase(ArtistTitle.of("b - c")) < 0);
  }

  @Test
  void shouldSplitRegexMetaCharSeparator() {
    ArtistTitle at = ArtistTitle.of("Artist + Title", "+");
    assertEquals("Artist", at.artist());
    assertEquals(" + ", at.artistTitleSep());
    assertEquals("Title", at.title());

    at = ArtistTitle.of("Artist ( Title", "(");
    assertEquals("Artist", at.artist());
    assertEquals("Title", at.title());

    assertFalse(ArtistTitle.of("Artist x Title", ".").split());
  }

  @Test
  void shouldBuildWithoutArtist() {
    ArtistTitle at = ArtistTitle.builder().title("Title").artistTitleSep(null).build();
    assertNull(at.artistTitleSep());
    assertFalse(at.split());
    assertEquals("Title", at.toString());
    assertEquals(at, ArtistTitle.of("Title"));
  }

  @Test
  void shouldBuildWithoutArtistNotKeepingExplicitSeparator() {
    ArtistTitle at = ArtistTitle.builder().artistTitleSep("-").title("Title").build();
    assertEquals("Title", at.toString());
    assertNotEquals(at, ArtistTitle.ofExact("Title", "-"));
  }

  @Test
  void shouldBuildMultiWithoutArtistUsingDefaultSeparator() {
    ArtistTitle at = ArtistTitle.builder().title("Title - Remix").multi(true).build();
    assertEquals(ArtistTitle.ARTIST_TITLE_SEPARATOR_DEFAULT, at.artistTitleSep());
    assertTrue(at.multi());
  }

  @Test
  void shouldToBuilder() {
    ArtistTitle at = ArtistTitle.of("Artist - Title - Remix");
    ArtistTitle copy = at.toBuilder().build();
    assertEquals(at, copy);
    assertTrue(copy.multi());

    ArtistTitle noArtist = ArtistTitle.ofExact("-Title", "-");
    assertEquals(noArtist, noArtist.toBuilder().build());
  }
}
