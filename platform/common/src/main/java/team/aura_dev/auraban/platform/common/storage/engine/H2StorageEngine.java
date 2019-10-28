package team.aura_dev.auraban.platform.common.storage.engine;

import com.google.common.annotations.VisibleForTesting;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import lombok.RequiredArgsConstructor;
import team.aura_dev.auraban.platform.common.storage.StorageEngine;
import team.aura_dev.auraban.platform.common.util.StringUtilities;

@RequiredArgsConstructor
public class H2StorageEngine implements StorageEngine {
  private static final String URLFormat = "jdbc:h2:%s;AUTO_SERVER=TRUE";

  private final Path databasePath;

  private Connection connection = null;

  @Override
  public void initialize() throws SQLException {
    connection = DriverManager.getConnection(getConnectingURLString());
  }

  @Override
  public void close() throws Exception {
    if (connection != null) connection.close();
  }

  @VisibleForTesting
  String getConnectingURLString() {
    return String.format(URLFormat, StringUtilities.urlEncodePath(databasePath));
  }
}
