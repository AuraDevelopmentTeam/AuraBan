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
    // has the class loaded already?
    Class<?> loadedClass = findLoadedClass(name);

    // Reuse existing instances of own classes
    if ((loadedClass == null) && (name.startsWith("@group@"))) {
      try {
        loadedClass = (Class<?>) findLoadedClassMethod.invoke(parent, name);
      } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
        // Ignore
      }
    }

    if (loadedClass == null) {
      try {
        // find the class from given jar urls
        loadedClass = findClass(name);
      } catch (ClassNotFoundException e) {
        // class does not exist in the given urls.
        // Let's try finding it in our parent classloader.
        // this'll throw ClassNotFoundException in failure.
        loadedClass = super.loadClass(name, resolve);
      }
    }

    if (resolve) { // marked to resolve
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
