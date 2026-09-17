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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ArtistTitleTest {

  @Test
  void shouldSplit() {
    String dirName = "Matter-Energy-Space-Time  -\t M.E.S.T";
    ArtistTitle at = ArtistTitle.of(dirName);
    assertEquals("Matter-Energy-Space-Time", at.artist());
    assertEquals("M.E.S.T", at.title());
    assertEquals("  -\t ", at.sep());
    assertEquals(dirName, at.toArtistTitle());
  }

  @Test
  void shouldNotSplit() {
    String dirName = "Albert Hoffman Likes Biking";
    ArtistTitle at = ArtistTitle.of(dirName);
    assertNull(at.artist());
    assertEquals(dirName, at.title());
    assertNull(at.sep());
    assertEquals(dirName, at.toArtistTitle());
  }

  @Test
  void shouldSplitRegex() {
    String dirName = "Matter-Energy-Space-Time  -\t M.E.S.T";
    ArtistTitle at = ArtistTitle.of(dirName, "-\t");
    assertEquals("Matter-Energy-Space-Time", at.artist());
    assertEquals("M.E.S.T", at.title());
    assertEquals("  -\t ", at.sep());
    assertEquals("-", at.sepTrimmed());
    assertEquals(dirName, at.toArtistTitle());
  }

  @Test
  void shouldSplitRegexTrimmable() {
    String dirName = "Matter-Energy-Space-Time  -\t M.E.S.T";
    ArtistTitle at = ArtistTitle.of(dirName, "\t");
    assertEquals("Matter-Energy-Space-Time  -", at.artist());
    assertEquals(" M.E.S.T", at.title());
    assertEquals("\t", at.sep());
    assertTrue(at.sepTrimmed().isEmpty());
    assertEquals(dirName, at.toArtistTitle());
  }

  @Test
  void shouldSplitRegexSpaced() {
    String dirName = "         ";
    ArtistTitle at = ArtistTitle.of(dirName, "€");
    assertNull(at.artist());
    assertEquals(dirName, at.title());
    assertNull(at.sep());
    assertNull(at.sepTrimmed());
    assertEquals(dirName, at.toArtistTitle());
  }

  @Test
  void shouldNotSplitRegex() {
    String dirName = "Albert Hoffman Likes Biking";
    ArtistTitle at = ArtistTitle.of(dirName, "€");
    assertNull(at.artist());
    assertEquals(dirName, at.title());
    assertNull(at.sep());
    assertNull(at.sepTrimmed());
    assertEquals(dirName, at.toArtistTitle());
  }

  @Test
  void shouldSplitExact() {
    String dirName = "Matter-Energy-Space-Time  -\t M.E.S.T";
    ArtistTitle at = ArtistTitle.ofExact(dirName, " -");
    assertEquals("Matter-Energy-Space-Time ", at.artist());
    assertEquals("\t M.E.S.T", at.title());
    assertEquals(" -", at.sep());
    assertEquals("-", at.sepTrimmed());
    assertEquals(dirName, at.toArtistTitle());
  }

  @Test
  void shouldSplitExactTrimmable() {
    String dirName = "Matter-Energy-Space-Time\t  ";
    ArtistTitle at = ArtistTitle.ofExact(dirName, "$");
    assertNull(at.artist());
    assertEquals("Matter-Energy-Space-Time\t  ", at.title());
    assertNull(at.sep());
    assertNull(at.sepTrimmed());
    assertEquals(dirName, at.toArtistTitle());
  }

  @Test
  void shouldSplitExactSpaced() {
    String dirName = "         ";
    ArtistTitle at = ArtistTitle.ofExact(dirName, "$");
    assertNull(at.artist());
    assertEquals("         ", at.title());
    assertNull(at.sep());
    assertNull(at.sepTrimmed());
    assertEquals(dirName, at.toArtistTitle());
  }

  @Test
  void shouldNotSplitExact() {
    String dirName = "Albert Hoffman Likes Biking";
    ArtistTitle at = ArtistTitle.ofExact(dirName, "-");
    assertNull(at.artist());
    assertEquals(dirName, at.title());
    assertNull(at.sep());
    assertNull(at.sepTrimmed());
    assertEquals(dirName, at.toArtistTitle());
  }
}
