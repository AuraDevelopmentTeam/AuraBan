package team.aura_dev.auraban.platform.common.config;

import static org.junit.Assert.assertEquals;

import org.codehaus.plexus.util.StringUtils;
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
            + StringUtils.repeat(" ", 29 - AuraBanApi.VERSION.length())
            + "                            | #\n"
            + "|                            /--\\|_|| (_||__)(_|| )   Proudly running on Testing - Unittests                            | #\n"
            + "|                                                                                                                       | #\n",
        loader.getBanner());
  }
}
