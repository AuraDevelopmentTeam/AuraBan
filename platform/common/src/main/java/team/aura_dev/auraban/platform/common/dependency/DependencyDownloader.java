package team.aura_dev.auraban.platform.common.dependency;

import eu.mikroskeem.picomaven.DownloadResult;
import eu.mikroskeem.picomaven.PicoMaven;
import eu.mikroskeem.picomaven.artifact.Dependency;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;
import me.lucko.jarrelocator.JarRelocator;
import me.lucko.jarrelocator.Relocation;
import team.aura_dev.auraban.platform.common.AuraBanBase;
import team.aura_dev.auraban.platform.common.AuraBanBaseBootstrap;

// TODO: Logging!
@UtilityClass
public class DependencyDownloader {
  private static final DependencyClassLoader classLoader =
      AuraBanBaseBootstrap.getDependencyClassLoader();
  private static final List<Relocation> relocationRules = new LinkedList<>();

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
      Set<Dependency> relocationDependencies =
          Collections.synchronizedSet(
              dependencies
                  .stream()
                  .filter(RuntimeDependency::isRelocate)
                  .map(RuntimeDependency::getDependency)
                  .collect(Collectors.toCollection(HashSet::new)));

      List<DownloadResult> downloads =
          picoMaven
              .downloadAllArtifacts()
              .values()
              .parallelStream()
              .flatMap(future -> processDownload(future, relocationDependencies))
              .peek(DependencyDownloader::checkDownload)
              .collect(Collectors.toList());

      relocationRules.addAll(
          downloads
              .stream()
              .map(DownloadResult::getDependency)
              .filter(relocationDependencies::contains)
              .map(DependencyDownloader::toRelocationRule)
              .collect(Collectors.toSet()));

      downloads.forEach(
          download -> processDownloadResult(download, relocationDependencies, relocationRules));
    }

    // TODO: Relocate own classes during runtime

    checkClass("com.zaxxer.hikari.HikariDataSource");
    checkClass("@group@.shadow.com.zaxxer.hikari.HikariDataSource");
    checkClass("team.aura_dev.auraban.shadow.com.zaxxer.hikari.HikariDataSource");
  }

  private static Stream<DownloadResult> processDownload(
      Future<DownloadResult> future, Set<Dependency> relocationDependencies) {
    try {
      final DownloadResult result = future.get();

      final List<DownloadResult> allDownloads =
          new LinkedList<>(result.getTransitiveDependencies());

      if (relocationDependencies.contains(result.getDependency())) {
        relocationDependencies.addAll(
            allDownloads.stream().map(DownloadResult::getDependency).collect(Collectors.toList()));
      }

      allDownloads.add(0, result);

      return allDownloads.stream();
    } catch (InterruptedException | ExecutionException e) {
      // Rethrow because we rely on this working
      throw new DependencyDownloadException("Error while trying to download a dependency", e);
    }
  }

  private static void checkDownload(DownloadResult result) {
    AuraBanBase.logger.info(result.toString());

    if (!result.isSuccess()) {
      throw new DependencyDownloadException(
          "Downloading the dependency " + getDependencyName(result.getDependency()) + " failed",
          result.getDownloadException());
    }
  }

  private static Relocation toRelocationRule(Dependency dependency) {
    final String group = getDependencyPackage(dependency);

    return new Relocation(group, "@group@.shadow." + group);
  }

  private static void processDownloadResult(
      DownloadResult result,
      Set<Dependency> relocationDependencies,
      List<Relocation> relocationRules) {
    List<Path> downloadedFiles = result.getAllDownloadedFiles();

    if (relocationDependencies.contains(result.getDependency())) {
      List<Path> originalDownloadedFiles = downloadedFiles;
      downloadedFiles = new LinkedList<>();

      for (Path inputPath : originalDownloadedFiles) {
        final File input = inputPath.toFile();
        final File output =
            new File(input.getParentFile(), input.getName().replace(".jar", "") + "_relocated.jar");

        if (!output.exists()) {
          JarRelocator relocator = new JarRelocator(input, output, relocationRules);

          try {
            relocator.run();
          } catch (IOException e) {
            // Rethrow because we rely on this working
            throw new DependencyDownloadException("Unable to relocate", e);
          }
        }

        downloadedFiles.add(output.toPath());
      }
    }

    downloadedFiles.forEach(DependencyDownloader::injectInClasspath);
  }

  private static void injectInClasspath(Path jarFile) {
    try {
      URL jarFileUrl = new URL("jar", "", "file:" + jarFile.toAbsolutePath().toString() + "!/");

      classLoader.addURL(jarFileUrl);
    } catch (MalformedURLException | IllegalArgumentException e) {
      // Rethrow because we rely on this working
      throw new DependencyDownloadException(
          "Error while trying to inject a dependency in the classloader", e);
    }
  }

  private static String getDependencyPackage(Dependency dependency) {
    return dependency.getGroupId();
  }

  private static String getDependencyName(Dependency dependency) {
    return dependency.getGroupId()
        + ':'
        + dependency.getArtifactId()
        + ':'
        + dependency.getVersion();
  }

  private static void checkClass(String className) {
    AuraBanBase.logger.info(className + ": " + doesClassExist(className));
  }

  private static boolean doesClassExist(String className) {
    try {
      // Class.forName(className);
      Class.forName(className, true, classLoader);

      return true;
    } catch (ClassNotFoundException e) {
      return false;
    }
  }
}
