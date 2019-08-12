package team.aura_dev.auraban.platform.common.dependency;

import eu.mikroskeem.picomaven.PicoMaven;
import java.io.File;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import team.aura_dev.auraban.platform.common.AuraBanBase;

@UtilityClass
public class DependencyDownloader {
  public static void downloadAndInjectInClasspath(
      Collection<RuntimeDependency> dependencies, File libPath) {
    if (!(libPath.exists() || libPath.mkdirs())) {
      throw new RuntimeException("Can't create the dirs");
    }

    PicoMaven.Builder picoMavenBase =
        new PicoMaven.Builder()
            .withDownloadPath(libPath.toPath())
            .withRepositoryURLs(
                dependencies
                    .stream()
                    .map(RuntimeDependency::getMaven)
                    .distinct()
                    .map(RuntimeDependency.Maven::getUrl)
                    .collect(Collectors.toList()))
            .withDependencies(
                dependencies
                    .stream()
                    .map(RuntimeDependency::getDependency)
                    .collect(Collectors.toList()));

    try (PicoMaven picoMaven = picoMavenBase.build()) {
      picoMaven
          .downloadAllArtifacts()
          .values()
          .forEach(
              future -> {
                try {
                  AuraBanBase.logger.info(future.get().toString());
                } catch (InterruptedException | ExecutionException e) {
                  e.printStackTrace();
                }
              });

      // TODO: Process the dependencies, add them to the classpath and relocate them
    }
  }
}
