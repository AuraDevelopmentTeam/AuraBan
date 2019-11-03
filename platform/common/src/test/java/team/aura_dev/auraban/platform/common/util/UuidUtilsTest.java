package team.aura_dev.auraban.platform.common.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.Random;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;

public class UuidUtilsTest {
  private static final int testRuns = 1_000_000;

  private Random rand;

  @Before
  public void setUp() {
    // Static seed for consistency
    rand = new Random(0);
  }

  @Test
  public void asBytesTest() {
    byte[] uuidBytes = new byte[16];

    for (int i = 0; i < testRuns; ++i) {
      rand.nextBytes(uuidBytes);

      assertArrayEquals(uuidBytes, UuidUtils.asBytes(UuidUtils.asUuid(uuidBytes)));
    }
  }

  @Test
  public void asUuidTest() {
    UUID uuid;

    for (int i = 0; i < testRuns; ++i) {
      uuid = new UUID(rand.nextLong(), rand.nextLong());

      assertEquals(uuid, UuidUtils.asUuid(UuidUtils.asBytes(uuid)));
    }
  }
}
