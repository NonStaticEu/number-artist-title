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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DiscNumberAndTotalTest {

  @Test
  void shouldCreate() {
    assertTrue(new DiscNumberAndTotal(new Numberish(1), new Numberish(2)).isNumberAndTotal(1, 2));
    assertTrue(new DiscNumberAndTotal("1", "2").isNumberAndTotal(1, 2));
    assertTrue(new DiscNumberAndTotal("2").isNumber(2));
  }

  @Test
  void shouldRejectZero() {
    IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> new DiscNumberAndTotal("0"));
    assertEquals("Cannot have a Disc 0", e.getMessage());
    assertThrows(IllegalArgumentException.class, () -> new DiscNumberAndTotal("0", "2"));
  }

  @Test
  void shouldRejectNulls() {
    assertThrows(NullPointerException.class, () -> new DiscNumberAndTotal(null, Numberish.EMPTY));
    assertThrows(NullPointerException.class, () -> new DiscNumberAndTotal(Numberish.EMPTY, null));
  }

  @Test
  void shouldMentionUnitInErrors() {
    IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> new DiscNumberAndTotal("-1"));
    assertEquals("Cannot have negative Disc", e.getMessage());
    e = assertThrows(IllegalArgumentException.class, () -> new DiscNumberAndTotal("3", "2"));
    assertEquals("Cannot have Disc (3) > Total (2)", e.getMessage());
  }

  @Test
  void shouldEmpty() {
    assertTrue(DiscNumberAndTotal.EMPTY.isEmpty());
    assertTrue(DiscNumberAndTotal.EMPTY.total().isEmpty());
  }

  @Test
  void shouldValidate() {
    assertTrue(DiscNumberAndTotal.isValid(null));
    assertTrue(DiscNumberAndTotal.isValid(""));
    assertTrue(DiscNumberAndTotal.isValid("1"));
    assertTrue(DiscNumberAndTotal.isValid("01/02"));
    assertTrue(DiscNumberAndTotal.isValid("12/1234"));

    assertFalse(DiscNumberAndTotal.isValid("A"));
    assertFalse(DiscNumberAndTotal.isValid("1/"));
    assertFalse(DiscNumberAndTotal.isValid("/2"));
    assertFalse(DiscNumberAndTotal.isValid("3/2"));
    assertFalse(DiscNumberAndTotal.isValid("-1"));
  }

  @Test
  void shouldNotValidateZero() {
    assertFalse(DiscNumberAndTotal.isValid("0"));
    assertFalse(DiscNumberAndTotal.isValid("0/2"));
  }

  @Test
  void shouldParse() {
    assertSame(DiscNumberAndTotal.EMPTY, DiscNumberAndTotal.parse(null));
    assertSame(DiscNumberAndTotal.EMPTY, DiscNumberAndTotal.parse(""));
    assertTrue(DiscNumberAndTotal.parse("1/2").isNumberAndTotal(1, 2));
    assertTrue(DiscNumberAndTotal.parse("3").isNumber(3));
    assertThrows(IllegalArgumentException.class, () -> DiscNumberAndTotal.parse("0/2"));
  }
}
