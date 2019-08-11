package team.aura_dev.auraban.platform.common.dependency;

import eu.mikroskeem.picomaven.PicoMaven;
import java.io.File;
import java.util.Collection;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DependencyDownloader {
  public static void downloadAndInjectInClasspath(
      Collection<RuntimeDependency> dependencies, File libPath) {
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
      picoMaven.downloadAllArtifacts();

      // TODO: Process the dependencies, add them to the classpath and relocate them
    }
  }
}
