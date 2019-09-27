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
    super(parent.getURLs(), parent.getParent());
  }

  @Override
  public void addURL(URL url) {
    super.addURL(url);
  }
}
