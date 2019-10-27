package team.aura_dev.auraban.platform.common.storage.engine;

import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import team.aura_dev.auraban.platform.common.storage.StorageEngine;

@RequiredArgsConstructor
public class H2StorageEngine implements StorageEngine {
  private final Path databasePath;

  @Override
  public void initialize() {
    // TODO Auto-generated method stub

  }

  @Override
  public void close() throws Exception {
    // TODO Auto-generated method stub

  }
}
