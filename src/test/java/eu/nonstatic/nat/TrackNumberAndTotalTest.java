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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TrackNumberAndTotalTest {

  @Test
  void shouldCreate() {
    TrackNumberAndTotal tnt = new TrackNumberAndTotal(new Numberish(3), new Numberish(12));
    assertTrue(tnt.isNumberAndTotal(3, 12));
    assertTrue(new TrackNumberAndTotal("3", "12").isNumberAndTotal(3, 12));
    assertTrue(new TrackNumberAndTotal("B2").isNumber("B2"));
    assertTrue(new TrackNumberAndTotal("0").isNumber(0)); // hidden track
  }

  @Test
  void shouldMentionUnitInErrors() {
    IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> new TrackNumberAndTotal("-1"));
    assertEquals("Cannot have negative Number", e.getMessage());
  }

  @Test
  void shouldEmpty() {
    assertTrue(TrackNumberAndTotal.EMPTY.isEmpty());
    assertTrue(TrackNumberAndTotal.EMPTY.total().isEmpty());
  }

  @Test
  void shouldValidate() {
    assertTrue(TrackNumberAndTotal.isValid(null));
    assertTrue(TrackNumberAndTotal.isValid(""));
    assertTrue(TrackNumberAndTotal.isValid("0"));
    assertTrue(TrackNumberAndTotal.isValid("7"));
    assertTrue(TrackNumberAndTotal.isValid("007"));
    assertTrue(TrackNumberAndTotal.isValid("01/08"));
    assertTrue(TrackNumberAndTotal.isValid("A"));
    assertTrue(TrackNumberAndTotal.isValid("b2"));
    assertTrue(TrackNumberAndTotal.isValid("AA12"));

    assertFalse(TrackNumberAndTotal.isValid("1234"));
    assertFalse(TrackNumberAndTotal.isValid("1/1234"));
    assertFalse(TrackNumberAndTotal.isValid("I1"));
    assertFalse(TrackNumberAndTotal.isValid("A123"));
    assertFalse(TrackNumberAndTotal.isValid("A1/2"));
    assertFalse(TrackNumberAndTotal.isValid("9/8"));
    assertFalse(TrackNumberAndTotal.isValid("1/0"));
    assertFalse(TrackNumberAndTotal.isValid(" 1"));
  }

  @Test
  void shouldParse() {
    assertSame(TrackNumberAndTotal.EMPTY, TrackNumberAndTotal.parse(null));
    assertSame(TrackNumberAndTotal.EMPTY, TrackNumberAndTotal.parse(" "));

    TrackNumberAndTotal tnt = TrackNumberAndTotal.parse("01/08");
    assertTrue(tnt.isNumberAndTotal(1, 8));
    assertEquals("01/08", tnt.formattedNumAndTotal(2, false));

    assertTrue(TrackNumberAndTotal.parse("A1").isNumber("A1"));
    assertThrows(IllegalArgumentException.class, () -> TrackNumberAndTotal.parse("9/8"));
  }

  @Test
  void shouldNotCompareDifferentTotals() {
    TrackNumberAndTotal oneOverEight = TrackNumberAndTotal.parse("1/8");
    TrackNumberAndTotal oneOverNine = TrackNumberAndTotal.parse("1/9");
    TrackNumberAndTotal twoOverEight = TrackNumberAndTotal.parse("2/8");

    assertThrows(IllegalArgumentException.class, () -> oneOverEight.compareTo(oneOverNine));
    assertTrue(oneOverEight.compareTo(twoOverEight) < 0);
  }

  @Test
  void shouldNotEqualDisc() {
    assertNotEquals(new TrackNumberAndTotal("1", "2"), new DiscNumberAndTotal("1", "2"));
    assertNotEquals(new DiscNumberAndTotal("1", "2"), new TrackNumberAndTotal("1", "2"));
  }
}
