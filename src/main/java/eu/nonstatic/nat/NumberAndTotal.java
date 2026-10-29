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
import java.util.function.BiFunction;
import java.util.regex.Pattern;
import lombok.NonNull;

public abstract class NumberAndTotal implements Comparable<NumberAndTotal> {

  public static final char TOTAL_SEPARATOR = '/';
  public static final int DEFAULT_DIGITS = 1;
  public static final boolean DEFAULT_LENIENT = true;
  protected static final String UNIT_NUMBER = "Number";

  private final @NonNull Numberish number;
  private final @NonNull Numberish total;


  protected NumberAndTotal(@NonNull Numberish number, @NonNull Numberish total) {
    switch (number.getType()) {
      case NUMERIC -> {
        if (number.isNegative()) { // 0 allows a "hidden track"
          throw new IllegalArgumentException("Cannot have negative " + unit());
        }
      }
      case LITERAL -> {
        if (number.text().indexOf(TOTAL_SEPARATOR) >= 0) {
          throw new IllegalArgumentException(unit() + " cannot contain '/'");
        }
      }
    }

    switch (total.getType()) {
      case NUMERIC -> {
        if(total.isZero() || total.isNegative()) {
          throw new IllegalArgumentException("Cannot have zero or negative Total");
        }
        switch (number.getType()) {
          case NULL -> throw new IllegalArgumentException("Cannot have a Total without a " + unit());
          case LITERAL -> throw new IllegalArgumentException("Cannot have a Total when " + unit() + " is literal (%s)".formatted(number));
          case NUMERIC -> {
            if (number.numeric() > total.numeric()) {
              throw new IllegalArgumentException("Cannot have " + unit() + " (%s) > Total (%s)".formatted(number, total));
            }
          }
        }
      }
      case LITERAL -> throw new IllegalArgumentException("Total can only be numeric or null (%s)".formatted(total));
    }
    this.number = number;
    this.total = total;
  }

  protected NumberAndTotal(String number) {
    this(number, null);
  }

  protected NumberAndTotal(String number, String total) {
    this(new Numberish(number), new Numberish(total));
  }

  protected String unit() {
    return UNIT_NUMBER;
  }

  public Numberish number() {
    return number;
  }

  public Numberish total() {
    return total;
  }

  public boolean isNumber(int num) {
    return number.is(num);
  }

  public boolean isNumber(String txt) {
    return number.is(txt);
  }

  public boolean isTotal(int num) {
    return total.is(num);
  }

  public boolean isNumberAndTotal(int num, int tot) {
    return isNumber(num) && isTotal(tot);
  }

  protected static <N extends NumberAndTotal> N parse(String raw, Pattern pattern, N empty, BiFunction<String, String, N> creator) {
    String text = trimToNull(raw);
    if (text == null) {
      return empty;
    } else if(pattern.matcher(raw).matches()) { // raw: includes leading/trailing spaces
      int slash = text.indexOf(TOTAL_SEPARATOR);
      if (slash < 0) {
        return creator.apply(text, null);
      } else {
        return creator.apply(
            text.substring(0, slash),
            text.substring(slash + 1)
        );
      }
    } else {
      throw new IllegalArgumentException("Cannot parse: " + raw);
    }
  }

  public boolean isEmpty() {
    return number.isEmpty();
  }

  public static String format(Numberish num, int digits) {
    return switch (num.getType()) {
      case NUMERIC -> formater(digits).formatted(num.numeric());
      case LITERAL -> num.text();
      case NULL -> null;
    };
  }

  private static String formater(int digits) {
    return "%0" + digits + 'd';
  }


  public String formattedNum() {
    return formattedNum(DEFAULT_LENIENT);
  }

  public String formattedNum(boolean lenient) {
    return formattedNum(DEFAULT_DIGITS, lenient);
  }

  public String formattedNum(int digits, boolean lenient) {
    if (number.isEmpty() && !lenient) {
      throw new NumberFormatException(unit() + " is unavailable");
    } else {
      return format(number, digits);
    }
  }

  public String formattedNumAndTotal() {
    return formattedNumAndTotal(DEFAULT_LENIENT);
  }

  public String formattedNumAndTotal(boolean lenient) {
    return formattedNumAndTotal(DEFAULT_DIGITS, lenient);
  }

  public String formattedNumAndTotal(int digits, boolean lenient) {
    if (total.isEmpty()) {
      if (lenient) {
        return formattedNum(digits, lenient);
      } else {
        throw new NumberFormatException("Total is unavailable");
      }
    } else { // number cannot be null if total exists
      return formattedNum(digits, false) + TOTAL_SEPARATOR + format(total, digits);
    }
  }

  @Override
  public int compareTo(NumberAndTotal nt) {
    int result = total.compareTo(nt.total);
    if(result != 0) {
      throw new IllegalArgumentException("Cannot compare NumberAndTotal with different Totals: " + this + " vs " + nt);
    }
    return number.compareTo(nt.number);
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NumberAndTotal that = (NumberAndTotal) o;
    return Objects.equals(number, that.number) && Objects.equals(total, that.total);
  }

  @Override
  public int hashCode() {
    return Objects.hash(number, total);
  }

  @Override
  public String toString() {
    return formattedNumAndTotal(true);
  }
}
