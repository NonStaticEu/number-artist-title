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

import java.util.regex.Pattern;
import lombok.NonNull;

public final class DiscNumberAndTotal extends NumberAndTotal {

  private static final Pattern PATTERN = Pattern.compile("^(\\d+)(/\\d+)?$", Pattern.CASE_INSENSITIVE); // supports 01/02, that's OK
  public static final DiscNumberAndTotal EMPTY = new DiscNumberAndTotal(null);
  private static final String UNIT_DISC = "Disc";

  public DiscNumberAndTotal(@NonNull Numberish number, @NonNull Numberish total) {
    super(validateNumber(number), total);
  }

  public DiscNumberAndTotal(String number) {
    this(number, null);
  }

  public DiscNumberAndTotal(String number, String total) {
    this(new Numberish(number), new Numberish(total));
  }

  private static Numberish validateNumber(Numberish number) {
    if(number.isZero()) {
      throw new IllegalArgumentException("Cannot have a " + UNIT_DISC + " 0");
    }
    return number;
  }

  @Override
  protected String getUnit() {
    return UNIT_DISC;
  }

  public static boolean isValid(String raw) {
    return NumberAndTotal.isValid(PATTERN, raw);
  }

  public static DiscNumberAndTotal parse(String raw) {
    return NumberAndTotal.parse(raw, EMPTY, DiscNumberAndTotal::new);
  }
}
