package team.aura_dev.auraban.platform.common.config;

import static org.junit.Assert.assertEquals;

import java.nio.file.Paths;
import org.junit.Test;
import team.aura_dev.auraban.platform.common.AuraBanBase;

public class ConfigLoaderTest {
  @Test
  public void getBannerTest() {
    // Mock plugin
    final AuraBanBase plugin =
        new AuraBanBase(Paths.get("")) {
          @Override
          public String getBasePlatform() {
            return "Testing";
          }

          @Override
          public String getPlatformVariant() {
            return "Unittests";
          }
        };

    final ConfigLoader loader = new ConfigLoader(plugin);

    assertEquals(
        "|                                         __                                                                            | #\n"
            + "|                             /\\     _ _ |__) _  _    AuraBan v0.0.1.77-SNAPSHOT                                        | #\n"
            + "|                            /--\\|_|| (_||__)(_|| )   Proudly running on Testing - Unittests                            | #\n"
            + "|                                                                                                                       | #\n",
        loader.getBanner());
  }
}
