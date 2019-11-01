package team.aura_dev.auraban.platform.common.storage.engine;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import lombok.RequiredArgsConstructor;
import team.aura_dev.auraban.platform.common.AuraBanBase;
import team.aura_dev.auraban.platform.common.storage.StorageEngine;

@RequiredArgsConstructor
public class H2StorageEngine implements StorageEngine {
  private static final String URLFormat = "jdbc:h2:%s;AUTO_SERVER=TRUE";

  private final Path databasePath;

  private Connection connection = null;

  @Override
  public void initialize() throws SQLException {
    connect();
    createTables();
  }

  private void connect() throws SQLException {
    final String connectionURL = String.format(URLFormat, databasePath.toFile());

    AuraBanBase.logger.debug("Connecting to \"" + connectionURL + '"');

    connection = DriverManager.getConnection(connectionURL);
  }

  private void createTables() {
    // TODO
  }

  @Override
  public void close() throws Exception {
    if (connection != null) connection.close();
  }
}
