package team.aura_dev.auraban.platform.common.storage.engine;

import lombok.RequiredArgsConstructor;
import team.aura_dev.auraban.platform.common.storage.StorageEngine;

@RequiredArgsConstructor
public class MySQLStorageEngine implements StorageEngine {
  private final String host;
  private final int port;
  private final String database;
  private final String user;
  private final String password;

  @Override
  public void initialize() {
    // TODO Auto-generated method stub

  }

  @Override
  public void close() throws Exception {
    // TODO Auto-generated method stub

  }
}
