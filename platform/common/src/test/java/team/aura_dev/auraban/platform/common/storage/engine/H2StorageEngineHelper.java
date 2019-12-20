package team.aura_dev.auraban.platform.common.storage.engine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import lombok.SneakyThrows;
import team.aura_dev.auraban.platform.common.storage.sql.NamedPreparedStatement;

/** Used to make the query methods public */
public class H2StorageEngineHelper extends H2StorageEngine {
  public H2StorageEngineHelper(Path databasePath) {
    super(databasePath, false);
  }

  @Override
  public void connect() throws SQLException {
    super.connect();
  }

  @Override
  protected void createTables() throws SQLException {
    super.createTables();
  }

  @Override
  public NamedPreparedStatement prepareStatement(String query) throws SQLException {
    return super.prepareStatement(query);
  }

  @Override
  public int executeUpdateQuery(String query) throws SQLException {
    return super.executeUpdateQuery(query);
  }

  @SneakyThrows({IOException.class, InterruptedException.class})
  @Override
  public void close() throws SQLException {
    super.close();

    final Path databaseFilePath = H2StorageEngineTest.getDatabaseFilePath(databasePath);

    try {
      Files.delete(databaseFilePath);
    } catch (IOException e) {
      Thread.sleep(100);

      Files.delete(databaseFilePath);
    }
  }
}
