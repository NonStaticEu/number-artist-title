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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

class NumberAndTotalTest {

  private static final Pattern ANY = Pattern.compile(".*");

  private static NumberAndTotal nt(String number, String total) {
    return new NumberAndTotal(number, total) {};
  }

  private static NumberAndTotal nt(String number) {
    return nt(number, null);
  }

  @Test
  void shouldCreate() {
    NumberAndTotal nt = nt("03", "12");
    assertEquals(new Numberish(3), nt.number());
    assertEquals(new Numberish(12), nt.total());
    assertTrue(nt.isNumber(3));
    assertTrue(nt.isNumber("3"));
    assertTrue(nt.isNumber("03"));
    assertFalse(nt.isNumber(4));
    assertTrue(nt.isTotal(12));
    assertFalse(nt.isTotal(3));
    assertTrue(nt.isNumberAndTotal(3, 12));
    assertFalse(nt.isNumberAndTotal(3, 11));
    assertFalse(nt.isNumberAndTotal(2, 12));
    assertFalse(nt.isEmpty());
  }

  @Test
  void shouldCreateWithoutTotal() {
    NumberAndTotal nt = nt("7");
    assertTrue(nt.isNumber(7));
    assertTrue(nt.total().isEmpty());
    assertFalse(nt.isTotal(7));
  }

  @Test
  void shouldCreateLiteral() {
    NumberAndTotal nt = nt("A1", null);
    assertTrue(nt.isNumber("A1"));
    assertFalse(nt.isNumber(1));
  }

  @Test
  void shouldCreateEmpty() {
    assertTrue(nt(null, null).isEmpty());
    assertTrue(nt(" ", "").isEmpty());
  }

  @Test
  void shouldAllowZero() {
    assertTrue(nt("0").isNumber(0)); // hidden track
    assertTrue(nt("0", "5").isNumberAndTotal(0, 5));
  }

  @Test
  void shouldAllowNumberEqualToTotal() {
    assertTrue(nt("5", "5").isNumberAndTotal(5, 5));
  }

  @Test
  void shouldRejectInvalidNumber() {
    assertThrows(IllegalArgumentException.class, () -> nt("-1"));
    assertThrows(IllegalArgumentException.class, () -> nt("A/B"));
  }

  @Test
  void shouldRejectInvalidTotal() {
    assertThrows(IllegalArgumentException.class, () -> nt("1", "0"));
    assertThrows(IllegalArgumentException.class, () -> nt("1", "-2"));
    assertThrows(IllegalArgumentException.class, () -> nt("1", "x"));
    assertThrows(IllegalArgumentException.class, () -> nt(null, "2"));
    assertThrows(IllegalArgumentException.class, () -> nt("A1", "2"));
    assertThrows(IllegalArgumentException.class, () -> nt("3", "2"));
  }

  @Test
  void shouldRejectNulls() {
    assertThrows(NullPointerException.class, () -> new NumberAndTotal(null, Numberish.EMPTY) {});
    assertThrows(NullPointerException.class, () -> new NumberAndTotal(Numberish.EMPTY, null) {});
  }

  @Test
  void shouldMentionUnitInErrors() {
    IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> nt("-1"));
    assertEquals("Cannot have negative Number", e.getMessage());
  }

  @Test
  void shouldFormat() {
    assertEquals("7", NumberAndTotal.format(new Numberish(7), 1));
    assertEquals("007", NumberAndTotal.format(new Numberish(7), 3));
    assertEquals("1234", NumberAndTotal.format(new Numberish(1234), 2));
    assertEquals("7", NumberAndTotal.format(new Numberish("007"), 1));
    assertEquals("A1", NumberAndTotal.format(new Numberish("A1"), 3));
    assertNull(NumberAndTotal.format(Numberish.EMPTY, 2));
  }

  @Test
  void shouldFormatNum() {
    NumberAndTotal nt = nt("3", "12");
    assertEquals("3", nt.formattedNum());
    assertEquals("3", nt.formattedNum(false));
    assertEquals("03", nt.formattedNum(2, false));

    NumberAndTotal empty = nt(null);
    assertNull(empty.formattedNum());
    assertNull(empty.formattedNum(true));
    assertThrows(NumberFormatException.class, () -> empty.formattedNum(false));
    assertThrows(NumberFormatException.class, () -> empty.formattedNum(2, false));
  }

  @Test
  void shouldFormatNumAndTotal() {
    NumberAndTotal nt = nt("3", "12");
    assertEquals("3/12", nt.formattedNumAndTotal());
    assertEquals("3/12", nt.formattedNumAndTotal(false));
    assertEquals("03/12", nt.formattedNumAndTotal(2, false));
    assertEquals("003/012", nt.formattedNumAndTotal(3, true));

    NumberAndTotal noTotal = nt("3");
    assertEquals("3", noTotal.formattedNumAndTotal());
    assertEquals("03", noTotal.formattedNumAndTotal(2, true));
    assertThrows(NumberFormatException.class, () -> noTotal.formattedNumAndTotal(false));

    NumberAndTotal empty = nt(null);
    assertNull(empty.formattedNumAndTotal());
    assertThrows(NumberFormatException.class, () -> empty.formattedNumAndTotal(false));
  }

  @Test
  void shouldToString() {
    assertEquals("3/12", nt("03", "12").toString());
    assertEquals("A1", nt("A1").toString());
    assertNull(nt(null).toString());
  }

  @Test
  void shouldCompare() {
    NumberAndTotal three = nt("3");
    NumberAndTotal threeOverTwelve = nt("3", "12");
    NumberAndTotal threeOverThirteen = nt("3", "13");

    assertEquals(0, threeOverTwelve.compareTo(nt("03", "12")));
    assertTrue(threeOverTwelve.compareTo(nt("4", "12")) < 0);
    assertTrue(nt("4", "12").compareTo(threeOverTwelve) > 0);
    assertTrue(three.compareTo(nt("A1")) < 0);
    assertThrows(IllegalArgumentException.class, () -> threeOverTwelve.compareTo(threeOverThirteen));
    assertThrows(IllegalArgumentException.class, () -> threeOverTwelve.compareTo(three));
  }

  @Test
  void shouldEquals() {
    NumberAndTotal nt = nt("3", "12");
    assertEquals(nt, nt("03", "12"));
    assertEquals(nt.hashCode(), nt("3", "12").hashCode());
    assertNotEquals(nt, nt("3"));
    assertNotEquals(nt, nt("4", "12"));
    assertNotEquals(nt, nt("3", "13"));
    assertNotEquals("3/12", nt);
    assertNotEquals(null, nt);
  }

  @Test
  void shouldParse() {
    NumberAndTotal empty = nt(null);
    assertSame(empty, NumberAndTotal.parse(null, ANY, empty, NumberAndTotalTest::nt));
    assertSame(empty, NumberAndTotal.parse("  ", ANY, empty, NumberAndTotalTest::nt));
    assertEquals(nt("3"), NumberAndTotal.parse(" 3 ", ANY, empty, NumberAndTotalTest::nt));
    assertEquals(nt("3", "12"), NumberAndTotal.parse("3/12", ANY, empty, NumberAndTotalTest::nt));
    assertEquals(nt("3", "12"), NumberAndTotal.parse(" 3 / 12 ", ANY, empty, NumberAndTotalTest::nt));
    assertThrows(IllegalArgumentException.class, () -> NumberAndTotal.parse("3/12/14", ANY, empty, NumberAndTotalTest::nt));
  }
}
