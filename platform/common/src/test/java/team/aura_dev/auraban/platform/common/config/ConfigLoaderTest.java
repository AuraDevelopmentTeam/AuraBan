package team.aura_dev.auraban.platform.common.config;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import team.aura_dev.auraban.api.AuraBanApi;
import team.aura_dev.auraban.platform.common.AuraBanBase;
import team.aura_dev.auraban.platform.common.AuraBanTest;

public class ConfigLoaderTest {
  @Test
  public void getBannerTest() {
    final AuraBanBase plugin = new AuraBanTest();
    final ConfigLoader loader = new ConfigLoader(plugin);

    assertEquals(
        "|                                         __                                                                            | #\n"
            + "|                             /\\     _ _ |__) _  _    AuraBan v"
            + AuraBanApi.VERSION
            + repeat(' ', 29 - AuraBanApi.VERSION.length())
            + "                            | #\n"
            + "|                            /--\\|_|| (_||__)(_|| )   Proudly running on Testing - Unittests                            | #\n"
            + "|                                                                                                                       | #\n",
        loader.getBanner());
  }

  private static String repeat(char character, int count) {
    final StringBuilder build = new StringBuilder(count);

    for (int i = 0; i < count; ++i) {
      build.append(character);
    }

    return build.toString();
  }
}
