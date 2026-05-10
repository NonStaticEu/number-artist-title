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

public final class TrackNumberAndTotal extends NumberAndTotal {

  // Keep ~aligned with NumberArtistTitle
  private static final Pattern PATTERN = Pattern.compile("^([A-H]{1,2}\\d{0,2})|(\\d{1,3}(/\\d{1,3})?)$", Pattern.CASE_INSENSITIVE); // supports 01/08, that's OK

  public static final TrackNumberAndTotal EMPTY = new TrackNumberAndTotal(null);

  public TrackNumberAndTotal(@NonNull Numberish number, @NonNull Numberish total) {
    super(number, total);
  }

  public TrackNumberAndTotal(String number) {
    super(number);
  }

  public TrackNumberAndTotal(String number, String total) {
    super(number, total);
  }

  public static boolean isValid(String raw) {
    return NumberAndTotal.isValid(PATTERN, raw);
  }

  public static TrackNumberAndTotal parse(String raw) {
    return NumberAndTotal.parse(raw, EMPTY, TrackNumberAndTotal::new);
  }
}
