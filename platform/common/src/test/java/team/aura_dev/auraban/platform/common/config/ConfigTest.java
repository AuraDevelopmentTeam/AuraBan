package team.aura_dev.auraban.platform.common.config;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ConfigTest {
  @Test
  public void urlEncodeTest() {
    assertEquals(
        "test%40%3A%C3%A4%C3%B6%C3%BC%26%3F+%40test",
        Config.Storage.MySQL.urlEncode("test@:äöü&? @test"));
  }

  @Test
  public void allowedValuesTest() {
    assertEquals("H2, MySQL", Config.Storage.StorageEngine.allowedValues);
  }
}
