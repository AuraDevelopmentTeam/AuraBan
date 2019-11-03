package team.aura_dev.auraban.platform.common.storage.engine;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import team.aura_dev.auraban.platform.common.AuraBanBase;
import team.aura_dev.auraban.platform.common.storage.sql.SQLStorageEngine;

@RequiredArgsConstructor
public class H2StorageEngine extends SQLStorageEngine {
  private static final String URLFormat = "jdbc:h2:%s;AUTO_SERVER=TRUE";

  private final Path databasePath;

  private Connection connection = null;

  @Override
  @SneakyThrows(ClassNotFoundException.class)
  protected void connect() throws SQLException {
    final String connectionURL = String.format(URLFormat, databasePath.toFile());

    AuraBanBase.logger.debug("Connecting to \"" + connectionURL + '"');

    // Make sure driver is loaded
    Class.forName("org.h2.Driver"); // This should never throw
    connection = DriverManager.getConnection(connectionURL);
  }

  @Override
  protected void createTables() {
    // TODO
  }

  @Override
  protected Connection getConnection() {
    return connection;
  }
}
