package team.aura_dev.auraban.platform.common.util;

import java.nio.ByteBuffer;
import java.util.UUID;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UuidUtils {
  public static UUID asUuid(byte[] bytes) {
    final ByteBuffer bb = ByteBuffer.wrap(bytes);
    final long firstLong = bb.getLong();
    final long secondLong = bb.getLong();

    return new UUID(firstLong, secondLong);
  }

  public static byte[] asBytes(UUID uuid) {
    final ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
    bb.putLong(uuid.getMostSignificantBits());
    bb.putLong(uuid.getLeastSignificantBits());

    return bb.array();
  }
}
