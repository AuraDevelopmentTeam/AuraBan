package team.aura_dev.auraban.platform.common.storage.engine;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import team.aura_dev.auraban.platform.common.util.StringUtilitiesTest;

public class H2StorageEngineTest {
  @Test
  public void getConnectingURLStringTest() throws Exception {
    try (final H2StorageEngine simple = new H2StorageEngine(StringUtilitiesTest.SIMPLE_PATH);
        final H2StorageEngine complicated =
            new H2StorageEngine(StringUtilitiesTest.COMPLICATED_PATH)) {
      // Replacing the backslash if we are on Windows
      assertEquals(
          "jdbc:h2:foo/bar/foobar;AUTO_SERVER=TRUE",
          simple.getConnectingURLString().replace('\\', '/'));
      assertEquals(
          "jdbc:h2:test/%25%7E%3B%C3%A4%C3%B6%C3%A9%E6%BC%A2;AUTO_SERVER=TRUE",
          complicated.getConnectingURLString().replace('\\', '/'));
    }
  }
}
