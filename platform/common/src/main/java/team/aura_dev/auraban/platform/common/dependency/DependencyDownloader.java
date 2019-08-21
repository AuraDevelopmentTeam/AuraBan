package team.aura_dev.auraban.platform.common.dependency;

import eu.mikroskeem.picomaven.DownloadResult;
import eu.mikroskeem.picomaven.PicoMaven;
import eu.mikroskeem.picomaven.artifact.Dependency;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import team.aura_dev.auraban.platform.common.AuraBanBase;

@UtilityClass
public class DependencyDownloader {
  private static final URLClassLoader classLoader;
  private static final Method method;

  static {
    try {
      classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
      method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
      method.setAccessible(true);
    } catch (NoSuchMethodException | SecurityException e) {
      // TODO: Properly log exception.
      e.printStackTrace();

      // Rethrow so leaving the fields uninitialized is no error
      throw new IllegalStateException(
          "Error while trying to make adding new URLs to the classloader possible", e);
    }
  }

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
          .parallelStream()
          .forEach(DependencyDownloader::processDownload);

      // TODO: Process the dependencies, add them to the classpath and relocate them
    }
  }

  private static void processDownload(Future<DownloadResult> future) {
    try {
      final DownloadResult result = future.get();

      AuraBanBase.logger.info(result.toString());

      if (!result.isSuccess()) {
        final Dependency dependency = result.getDependency();

        throw new IllegalArgumentException(
            "Downloading the dependency "
                + dependency.getGroupId()
                + ':'
                + dependency.getArtifactId()
                + ':'
                + dependency.getVersion()
                + " failed");
      }

      result.getAllDownloadedFiles().forEach(DependencyDownloader::injectInClasspath);
    } catch (InterruptedException | ExecutionException e) {
      // TODO: Properly log exception.
      e.printStackTrace();

      // Rethrow because we rely on this working
      throw new IllegalArgumentException("Error while trying to download a dependency", e);
    }
  }

  private static void injectInClasspath(Path jarFile) {
    try {
      URL jarFileUrl = new URL("jar", "", "file:" + jarFile.toAbsolutePath().toString() + "!/");

      method.invoke(classLoader, jarFileUrl);
    } catch (MalformedURLException
        | IllegalAccessException
        | IllegalArgumentException
        | InvocationTargetException e) {
      // TODO: Properly log exception.
      e.printStackTrace();

      // Rethrow because we rely on this working
      throw new IllegalArgumentException(
          "Error while trying to inject a dependency in the classloader", e);
    }
  }
}
