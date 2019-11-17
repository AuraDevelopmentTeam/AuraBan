package team.aura_dev.auraban.platform.common.storage;

import java.sql.SQLException;
import org.slf4j.Logger;
import team.aura_dev.auraban.platform.common.AuraBanBase;

public interface StorageEngine extends AutoCloseable {
  // Global logger
  public static final Logger logger = AuraBanBase.logger;

  void initialize() throws SQLException;
  // TODO: Methods to store and retrieve data
}
