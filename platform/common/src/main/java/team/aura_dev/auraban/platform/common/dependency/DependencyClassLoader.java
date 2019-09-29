package team.aura_dev.auraban.platform.common.dependency;

import java.net.URL;
import java.net.URLClassLoader;

/** A custom {@link ClassLoader} implementation that allows adding URLs. */
public class DependencyClassLoader extends URLClassLoader {
  public DependencyClassLoader() {
    this((URLClassLoader) DependencyClassLoader.class.getClassLoader());
  }

  public DependencyClassLoader(URLClassLoader parent) {
    // Steal the plugin {@link ClassLoader}'s URLs and parent.
    // Because else the plugin class would be loaded with the plugin {@link ClassLoader} and classes
    // below that wouldn't be loaded at all.
    super(getAuraBanURL(), parent);
  }

  @Override
  public void addURL(URL url) {
    super.addURL(url);
  }

  @Override
  protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
    // has the class loaded already?
    Class<?> loadedClass = findLoadedClass(name);

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
}
