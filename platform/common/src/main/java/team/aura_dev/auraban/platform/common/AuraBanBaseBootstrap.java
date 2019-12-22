package team.aura_dev.auraban.platform.common;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import lombok.Getter;
import team.aura_dev.auraban.platform.common.dependency.DependencyClassLoader;

public class AuraBanBaseBootstrap {
  public static final String ID = "@id@";
  public static final String NAME = "@name@";
  public static final String VERSION = "@version@";
  public static final String DESCRIPTION = "@description@";
  public static final String URL = "https://github.com/AuraDevelopmentTeam/AuraBan";
  public static final String AUTHOR = "The_BrainStone";

  @Getter
  private static final DependencyClassLoader dependencyClassLoader =
      AccessController.doPrivileged(
          (PrivilegedAction<DependencyClassLoader>) DependencyClassLoader::new);

  private Object plugin;
  private Class<?> pluginClass;

  /**
   * Checks if SLF4J is present and loads it if not.
   *
   * @param libsPath Where to unpack the jar files to
   * @param version which version of the slf4j-plugin-xxx to use
   */
  public void checkAndLoadSLF4J(Path libsPath, String version) {
    try {
      Class.forName("org.slf4j.impl.StaticLoggerBinder");

      // Class is present, we don't need to load SLF4J
      return;
    } catch (ClassNotFoundException e) {
      // Ignore and continue. We need to load SLF4J
    }

    try {
      extractAndInjectSLF4JLib(libsPath, "api");
      extractAndInjectSLF4JLib(libsPath, "plugin-" + version + "-@slf4jPluginVersion@");
    } catch (IOException e) {
      throw new IllegalStateException("Unexpected IOException while trying to load SLF4J", e);
    }
  }

  /**
   * Bootstraps the actual plugin class.
   *
   * @param bootstrapPlugin the instance of the bootstrap class. Used to determine the actual plugin
   *     name. Also gets prepended to the other parameters
   * @param params parameters forwarded to the plugin class constructor
   */
  public void initializePlugin(Object bootstrapPlugin, Object... params) {
    // Add plugin instance as first parameter
    final Object[] mergedParams = new Object[params.length + 1];
    mergedParams[0] = bootstrapPlugin;
    System.arraycopy(params, 0, mergedParams, 1, params.length);

    plugin =
        initializePlugin(
            bootstrapPlugin.getClass().getName().replace("Bootstrap", ""), mergedParams);
    pluginClass = plugin.getClass();
  }

  public void preInitPlugin() {
    callMethod("preInitPlugin");
  }

  public void initPlugin() {
    callMethod("initPlugin");
  }

  private void callMethod(String name) {
    callMethod(name, new Class<?>[0]);
  }

  private void callMethod(String name, Class<?>[] types, Object... params) {
    try {
      pluginClass.getMethod(name, types).invoke(plugin, params);
    } catch (InvocationTargetException e) {
      // Properly unwrap the InvocationTargetException
      throw new IllegalStateException(
          "Calling " + name + " resulted in an exception", e.getTargetException());
    } catch (IllegalAccessException
        | IllegalArgumentException
        | NoSuchMethodException
        | SecurityException e) {
      throw new IllegalStateException("Calling " + name + " failed", e);
    }
  }

  private static Object initializePlugin(String pluginClassName, Object... params) {
    try {
      final Class<?> pluginClass = dependencyClassLoader.loadClass(pluginClassName);
      // Checking if the parameter count matches is good enough of a way to find the matching
      // constructor in this case
      // 10/10 parameter matching
      final Constructor<?> constructor =
          Arrays.stream(pluginClass.getConstructors())
              .filter(con -> con.getParameterCount() == params.length)
              .findFirst()
              .orElseThrow(NoSuchMethodException::new);
      return constructor.newInstance(params);
    } catch (InvocationTargetException e) {
      // Properly unwrap the InvocationTargetException
      throw new IllegalStateException(
          "Loading the plugin class resulted in an exception ", e.getTargetException());
    } catch (Exception e) {
      // Catch all checked and unchecked exceptions
      throw new IllegalStateException("Loading the plugin class failed", e);
    }
  }

  @SuppressFBWarnings(
      value = "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE",
      justification = "We're never getting null here")
  private static void extractAndInjectSLF4JLib(Path libsPath, String libName) throws IOException {
    final String JAR_PREFIX = "org/slf4j/slf4j-";
    final String JAR_SUFFIX = "-@slf4jVersion@.zip";
    final String FILE_PREFIX = "org/slf4j/";
    final String FILE_MIDDLE = "/slf4j-";
    final String FILE_SUFFIX = "-@slf4jVersion@.jar";

    Path outFile = libsPath.resolve(FILE_PREFIX + libName + FILE_MIDDLE + libName + FILE_SUFFIX);
    Files.createDirectories(outFile.getParent());

    if (!Files.exists(outFile)) {
      try (InputStream libStream =
          dependencyClassLoader.getResourceAsStream(JAR_PREFIX + libName + JAR_SUFFIX)) {
        Files.copy(libStream, outFile);
      }
    }

    dependencyClassLoader.addURL(outFile.toUri().toURL());
  }
}
