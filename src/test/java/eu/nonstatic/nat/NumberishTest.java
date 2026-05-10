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

class NumberishTest {

  @Test
  void testConstructorWithInt() {
    Numberish n = new Numberish(42);
    assertEquals(42, n.numeric());
    assertEquals("42", n.text());
    assertEquals(Numberish.Type.NUMERIC, n.getType());
    assertFalse(n.isEmpty());
  }

  @Test
  void testConstructorWithIntStrict() {
    Numberish n = new Numberish(42, true);
    assertEquals(42, n.numeric());
    assertEquals("42", n.text());
    assertEquals(Numberish.Type.NUMERIC, n.getType());
    assertFalse(n.isEmpty());
  }

  @Test
  void testConstructorWithNumericString() {
    Numberish n = new Numberish("123");
    assertEquals(123, n.numeric());
    assertEquals("123", n.text());
    assertEquals(Numberish.Type.NUMERIC, n.getType());
    assertFalse(n.isEmpty());
  }

  @Test
  void testConstructorWithNumericStringStrict() {
    Numberish n = new Numberish("123", true);
    assertEquals(123, n.numeric());
    assertEquals("123", n.text());
    assertEquals(Numberish.Type.NUMERIC, n.getType());
    assertFalse(n.isEmpty());
  }

  @Test
  void testConstructorWithLiteralString() {
    Numberish n = new Numberish("abc");
    assertEquals("abc", n.text());
    assertThrows(NullPointerException.class, n::numeric);
    assertEquals(Numberish.Type.LITERAL, n.getType());
    assertFalse(n.isEmpty());
  }

  @Test
  void testConstructorWithLiteralStringStrict() {
    Numberish n = new Numberish("abc", true);
    assertEquals("abc", n.text());
    assertThrows(NullPointerException.class, n::numeric);
    assertEquals(Numberish.Type.LITERAL, n.getType());
    assertFalse(n.isEmpty());
  }

  @Test
  void testConstructorWithNullString() {
    Numberish n = new Numberish(null);
    assertNull(n.text());
    assertThrows(NullPointerException.class, n::numeric);
    assertEquals(Numberish.Type.NULL, n.getType());
    assertTrue(n.isEmpty());
  }

  @Test
  void testConstructorWithNullStringStrict() {
    Numberish n = new Numberish(null, true);
    assertNull(n.text());
    assertThrows(NullPointerException.class, n::numeric);
    assertEquals(Numberish.Type.NULL, n.getType());
    assertTrue(n.isEmpty());
  }

  @Test
  void testConstructorWithEmptyString() {
    Numberish n = new Numberish("");
    assertNull(n.text());
    assertEquals(Numberish.Type.NULL, n.getType());
    assertTrue(n.isEmpty());
  }

  @Test
  void testConstructorWithEmptyStringStrict() {
    Numberish n = new Numberish("", true);
    assertEquals("", n.text());
    assertEquals(Numberish.Type.LITERAL, n.getType());
    assertFalse(n.isEmpty());
  }

  @Test
  void testConstructorWithWhitespaceString() {
    Numberish n = new Numberish("   ");
    assertNull(n.text());
    assertEquals(Numberish.Type.NULL, n.getType());
    assertTrue(n.isEmpty());
  }

  @Test
  void testConstructorWithWhitespaceStringStrict() {
    Numberish n = new Numberish("   ", true);
    assertEquals("   ", n.text());
    assertEquals(Numberish.Type.LITERAL, n.getType());
    assertFalse(n.isEmpty());
  }

  @Test
  void testEmpty() {
    assertTrue(Numberish.EMPTY.isEmpty());
    assertEquals(Numberish.Type.NULL, Numberish.EMPTY.getType());
  }

  @Test
  void testIsNegative() {
    assertTrue(new Numberish(-5).isNegative());
    assertFalse(new Numberish(0).isNegative());
    assertFalse(new Numberish(5).isNegative());
    assertFalse(new Numberish("abc").isNegative());
  }

  @Test
  void testIsZero() {
    assertTrue(new Numberish(0).isZero());
    assertTrue(new Numberish("0").isZero());
    assertFalse(new Numberish(1).isZero());
    assertFalse(new Numberish("abc").isZero());
  }

  @Test
  void testIsPositive() {
    assertTrue(new Numberish(5).isPositive());
    assertFalse(new Numberish(0).isPositive());
    assertFalse(new Numberish(-5).isPositive());
    assertFalse(new Numberish("abc").isPositive());
  }

  @Test
  void testIsWithInt() {
    assertTrue(new Numberish(42).is(42));
    assertTrue(new Numberish("42").is(42));
    assertFalse(new Numberish(42).is(43));
    assertFalse(new Numberish("abc").is(42));
  }

  @Test
  void testIsWithString() {
    assertTrue(new Numberish("abc").is("abc"));
    assertTrue(new Numberish(42).is("42"));
    assertTrue(new Numberish("1").is("01")); // numeric comparison
    assertFalse(new Numberish("abc").is("def"));
  }

  @Test
  void testIsWithStringStrict() {
    assertTrue(new Numberish("abc", true).is("abc"));
    assertTrue(new Numberish(42, true).is("42"));
    assertFalse(new Numberish("1", true).is("01")); // strict text comparison
    assertFalse(new Numberish("abc", true).is("def"));
  }

  @Test
  void testGetWidth() {
    assertEquals(0, new Numberish(null).getWidth());
    assertEquals(2, new Numberish(42).getWidth());
    assertEquals(2, new Numberish("03").getWidth());
    assertEquals(3, new Numberish("abc").getWidth());
  }

  @Test
  void testToString() {
    assertEquals("42", new Numberish(42).toString());
    assertEquals("42", new Numberish("042").toString());
    assertEquals("042", new Numberish("042", true).toString());

    assertEquals("-7", new Numberish(-7).toString());
    assertEquals("-7", new Numberish("-07").toString());
    assertEquals("-07", new Numberish("-07", true).toString());

    assertEquals("abc", new Numberish("abc").toString());
    assertNull(new Numberish(null).toString());
  }

  @Test
  void testEquals() {
    assertNotEquals(new Numberish(42), new Object());
    assertEquals(new Numberish(42), new Numberish("42"));
    assertEquals(new Numberish(1), new Numberish("01"));
    assertEquals(new Numberish("abc"), new Numberish("abc"));
    assertEquals(new Numberish(null), new Numberish(""));
    assertNotEquals(new Numberish(42), new Numberish(43));
    assertNotEquals(new Numberish("abc"), new Numberish("def"));
  }

  @Test
  void testHashCode() {
    assertEquals(new Numberish(42).hashCode(), new Numberish("42").hashCode());
    assertEquals(new Numberish("abc").hashCode(), new Numberish("abc").hashCode());
  }

  @Test
  void testCompareTo() {
    // NUMERIC < LITERAL < NULL
    assertTrue(new Numberish(5).compareTo(new Numberish("abc")) < 0);
    assertTrue(new Numberish("abc").compareTo(new Numberish(null)) < 0);

    // Same type comparisons
    assertTrue(new Numberish(1).compareTo(new Numberish(2)) < 0);
    assertTrue(new Numberish(2).compareTo(new Numberish(1)) > 0);
    assertEquals(0, new Numberish(5).compareTo(new Numberish("5")));

    assertTrue(new Numberish("a").compareTo(new Numberish("b")) < 0);
    assertTrue(new Numberish("b").compareTo(new Numberish("a")) > 0);
    assertEquals(0, new Numberish("abc").compareTo(new Numberish("abc")));

    assertEquals(0, new Numberish(null).compareTo(new Numberish("")));
  }

  @Test
  void testCompareToIgnoreCase() {
    assertEquals(0, new Numberish("abc").compareToIgnoreCase(new Numberish("ABC")));
    assertTrue(new Numberish("a").compareToIgnoreCase(new Numberish("B")) < 0);

    // Non-literal types use regular compareTo
    assertTrue(new Numberish(1).compareToIgnoreCase(new Numberish(2)) < 0);
    assertEquals(0, new Numberish(null).compareToIgnoreCase(new Numberish("")));
  }

  @Test
  void testNegativeNumbers() {
    Numberish n = new Numberish(-42);
    assertEquals(-42, n.numeric());
    assertEquals("-42", n.text());
    assertTrue(n.isNegative());
    assertEquals(Numberish.Type.NUMERIC, n.getType());
  }

  @Test
  void testLeadingZeros() {
    Numberish n = new Numberish("007");
    assertEquals(7, n.numeric());
    assertEquals("007", n.text());
    assertTrue(n.is("7"));
    assertTrue(n.is(7));
  }
}
