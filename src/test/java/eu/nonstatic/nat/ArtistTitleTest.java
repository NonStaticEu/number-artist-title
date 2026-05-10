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
