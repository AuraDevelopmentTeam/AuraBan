package team.aura_dev.auraban.platform.common.config;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ConfigTest {
  @Test
  public void allowedValuesTest() {
    assertEquals("H2, MySQL", Config.Storage.StorageEngineType.allowedValues);
  }
}
