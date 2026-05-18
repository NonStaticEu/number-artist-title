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

import org.junit.jupiter.api.Test;

class ArtistTitleTest {

  @Test
  void shouldSplit() {
    String dirName = "Matter-Energy-Space-Time  -\t M.E.S.T";
    ArtistTitle at = ArtistTitle.of(dirName);
    assertEquals("Matter-Energy-Space-Time", at.artist());
    assertEquals("M.E.S.T", at.title());
  }

  @Test
  void shouldNotSplit() {
    String dirName = "Albert Hoffman Likes Biking";
    ArtistTitle at = ArtistTitle.of(dirName);
    assertNull(at.artist());
    assertEquals(dirName, at.title());
  }
}
