package team.aura_dev.auraban.platform.common.storage;

import java.sql.SQLException;

public interface StorageEngine extends AutoCloseable {
  void initialize() throws SQLException;
  // TODO: Methods to store and retrieve data
}
