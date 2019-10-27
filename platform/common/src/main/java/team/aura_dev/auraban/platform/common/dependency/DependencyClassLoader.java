package team.aura_dev.auraban.platform.common.dependency;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/** A custom {@link ClassLoader} implementation that allows adding URLs. */
public class DependencyClassLoader extends URLClassLoader {
  private static final Method findLoadedClassMethod = getFindLoadedClassMethod();

  protected final ClassLoader parent;

  public DependencyClassLoader() {
    this(DependencyClassLoader.class.getClassLoader());
  }

  public DependencyClassLoader(ClassLoader parent) {
    // Steal the plugin {@link ClassLoader}'s URLs and parent.
    // Because else the plugin class would be loaded with the plugin {@link ClassLoader} and classes
    // below that wouldn't be loaded at all.
    super(getAuraBanURL(), parent);

    this.parent = parent;
  }

  @Override
  public void addURL(URL url) {
    super.addURL(url);
  }

  @Override
  protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
    // Is the Class loaded already?
    Class<?> loadedClass = findLoadedClass(name);

    // Reuse existing instances of own Classes if loaded in parent ClassLoader
    // (Like the bootstrap Classes or this Class(Loader))
    if ((loadedClass == null) && name.startsWith("@group@")) {
      try {
        loadedClass = (Class<?>) findLoadedClassMethod.invoke(parent, name);
      } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
        // Ignore
      }
    }

    // Never load API classes with this ClassLoader
    if ((loadedClass == null) && !name.startsWith("@group@.api")) {
      try {
        // Find the Class from given jar URLs
        loadedClass = findClass(name);
      } catch (ClassNotFoundException e) {
        // Ignore
      }
    }

    // The Class hasn't been found yet
    // Let's try finding it in our parent ClassLoader
    // This'll throw ClassNotFoundException in failure
    if (loadedClass == null) {
      loadedClass = super.loadClass(name, resolve);
    }

    // Marked to resolve
    if (resolve) {
      resolveClass(loadedClass);
    }

    return loadedClass;
  }

  private static URL[] getAuraBanURL() {
    return new URL[] {
      DependencyClassLoader.class.getProtectionDomain().getCodeSource().getLocation()
    };
  }

  private static Method getFindLoadedClassMethod() {
    try {
      final Method method = ClassLoader.class.getDeclaredMethod("findLoadedClass", String.class);
      method.setAccessible(true);

      return method;
    } catch (NoSuchMethodException | SecurityException e) {
      // Can't continue
      throw new IllegalStateException("Exception while trying to prepare the ClassLoader", e);
    }
  }
}
