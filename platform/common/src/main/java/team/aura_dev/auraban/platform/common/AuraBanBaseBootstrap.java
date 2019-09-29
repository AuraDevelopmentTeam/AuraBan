package team.aura_dev.auraban.platform.common;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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

  private final Object plugin;
  private final Class<?> pluginClass;

  public AuraBanBaseBootstrap(Object caller, Object... params) {
    plugin = initializePlugin(caller, params);
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
    } catch (IllegalAccessException
        | IllegalArgumentException
        | InvocationTargetException
        | NoSuchMethodException
        | SecurityException e) {
      throw new IllegalStateException("Calling " + name + " failed", e);
    }
  }

  private static Object initializePlugin(Object bootstrapPlugin, Object... params) {
    return initializePlugin(bootstrapPlugin.getClass().getName().replace("Bootstrap", ""), params);
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
    } catch (Exception e) {
      // Catch all checked and unchecked exceptions
      throw new IllegalStateException("Loading the plugin class failed", e);
    }
  }
}
