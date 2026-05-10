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

import static eu.nonstatic.util.StringUtils.trimToNull;

import java.util.Objects;
import lombok.Getter;

public class Numberish implements Comparable<Numberish> {

  private static final boolean STRICT_DEFAULT = false;
  public static final Numberish EMPTY = new Numberish(null);

  private final Integer numeric;
  private final String text; // if numeric, then text exists too
  @Getter
  private boolean strict; // tell whether Numberish("1") equals Numberish("01")

  public Numberish(int numeric) {
    this(numeric, STRICT_DEFAULT);
  }

  public Numberish(int numeric, boolean strict) {
    this.numeric = numeric;
    this.text = Integer.toString(numeric);
    this.strict = strict;
  }

  public Numberish(String text) {
    this(text, STRICT_DEFAULT);
  }

  public Numberish(String text, boolean strict) {
    this.strict = strict;
    this.text = strict ? text : trimToNull(text);
    this.numeric = parseIntSafe(this.text);
  }

  private static Integer parseIntSafe(String text) {
    try {
      return Integer.valueOf(text);
    } catch (NumberFormatException e) { // just a literal
      return null;
    }
  }

  public String text() {
    return text;
  }

  public int numeric() throws NullPointerException {
    return numeric; // may throw NPE when unboxing
  }

  public boolean isEmpty() {
    return text == null; // simpler than getType() == Type.NULL
  }

  public boolean isNegative() {
    return numeric != null && numeric < 0;
  }

  public boolean isZero() {
    return is(0);
  }

  public boolean isPositive() {
    return numeric != null && numeric > 0;
  }

  public boolean is(int num) {
    return numeric != null && numeric == num;
  }

  public boolean is(String txt) {
    // addressing the case where you try to compare "1" and "01"
    Integer num;
    if(!strict && getType() == Type.NUMERIC && (num = parseIntSafe(txt)) != null) {
      return is(num);
    } else {
      return Objects.equals(text, txt);
    }
  }

  public int getWidth() {
    return isEmpty() ? 0 : text.length();
  }

  public Type getType() {
    if(numeric != null) {
      return Type.NUMERIC;
    } else {
      return text != null ? Type.LITERAL : Type.NULL;
    }
  }

  @Override
  public String toString() {
    if(!strict && getType() == Type.NUMERIC) {
      return Integer.toString(numeric);
    } else {
      return text;
    }
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Numberish that)) {
      return false;
    }
    if(getType() == Type.NUMERIC && that.getType() == Type.NUMERIC) {
      return is(that.numeric);
    } else {
      return Objects.equals(text, that.text);
    }
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(text);
  }

  @Override
  public int compareTo(Numberish n) {
    Type type = getType();
    int result = type.compareTo(n.getType());
    if(result == 0) {
      result = switch (type) {
        case NULL -> 0;
        case NUMERIC -> numeric - n.numeric;
        case LITERAL -> text.compareTo(n.text);
      };
    }
    return result;
  }

  public int compareToIgnoreCase(Numberish n) {
    if(getType() == Type.LITERAL && n.getType() == Type.LITERAL) {
      return String.CASE_INSENSITIVE_ORDER.compare(text, n.text);
    } else {
      return compareTo(n);
    }
  }

  public enum Type {
    // In that order for compareTo
    NUMERIC,
    LITERAL,
    NULL
  }
}
