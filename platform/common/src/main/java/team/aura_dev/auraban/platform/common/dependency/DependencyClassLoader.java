package team.aura_dev.auraban.platform.common.dependency;

import java.net.URL;
import java.net.URLClassLoader;

/** A custom {@link ClassLoader} implementation that allows adding URLs. */
public class DependencyClassLoader extends URLClassLoader {
  public DependencyClassLoader() {
    this((URLClassLoader) DependencyClassLoader.class.getClassLoader());
  }

  public DependencyClassLoader(URLClassLoader parent) {
    super(parent.getURLs(), parent);
  }

  @Override
  public void addURL(URL url) {
    super.addURL(url);
  }
}
