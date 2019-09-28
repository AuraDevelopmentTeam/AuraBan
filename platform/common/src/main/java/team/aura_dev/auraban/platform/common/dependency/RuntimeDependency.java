package team.aura_dev.auraban.platform.common.dependency;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import eu.mikroskeem.picomaven.artifact.ArtifactChecksums;
import eu.mikroskeem.picomaven.artifact.Dependency;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.Value;

@Value
@Builder
@SuppressFBWarnings(
  value = {"JLM_JSR166_UTILCONCURRENT_MONITORENTER", "RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE"},
  justification = "Code is generated by lombok which means I don't have any influence on it."
)
public class RuntimeDependency {
  public static final RuntimeDependency HIKARI_CP =
      RuntimeDependency.builder(
              "com.zaxxer",
              "HikariCP",
              "2.7.9",
              "2002335357f6c75336692f93004004e3",
              "a83113d2c091d0d0f853dad3217bd7df3beb6ae3")
          .relocate()
          .build();
  public static final RuntimeDependency CONFIGURATE_HOCON =
      RuntimeDependency.builder(
              "org.spongepowered",
              "configurate-hocon",
              "3.6.1",
              "6395403afce7b9bbf4e26ef74c13da9a",
              "e3f199dbd91de753a70f63606f530fdb8644bbd5")
          .maven(Maven.SPONGE)
          .relocate()
          .transitive()
          .build();

  private final String groupId;
  private final String artifactId;
  private final String version;
  @Builder.Default private final String classifier = null;
  private final String md5Hash;
  private final String sha1Hash;
  @Builder.Default private final Maven maven = Maven.MAVEN_CENTRAL;
  @Builder.Default private final boolean transitive = false;
  @Builder.Default private final boolean relocate = false;

  @Getter(lazy = true)
  private final Dependency dependency = generateDependency();

  private final Dependency generateDependency() {
    return new Dependency(
        groupId,
        artifactId,
        version,
        null,
        transitive,
        Arrays.asList(
            ArtifactChecksums.md5HexSumOf(md5Hash), ArtifactChecksums.sha1HexSumOf(sha1Hash)));
  }

  public static RuntimeDependencyBuilder builder(
      String groupId, String artifactId, String version, String md5Hash, String sha1Hash) {
    return new RuntimeDependencyBuilder(groupId, artifactId, version, md5Hash, sha1Hash);
  }

  public static class RuntimeDependencyBuilder {
    RuntimeDependencyBuilder(
        String groupId, String artifactId, String version, String md5Hash, String sha1Hash) {
      this.groupId(groupId);
      this.artifactId(artifactId);
      this.version(version);
      this.md5Hash(md5Hash);
      this.sha1Hash(sha1Hash);
    }

    public RuntimeDependencyBuilder transitive() {
      transitive = true;
      transitive$set = true;

      return this;
    }

    public RuntimeDependencyBuilder relocate() {
      relocate = true;
      relocate$set = true;

      return this;
    }
  }

  @RequiredArgsConstructor
  @Getter
  public static enum Maven {
    MAVEN_CENTRAL("https://repo1.maven.org/maven2"),
    SPONGE("https://repo.spongepowered.org/maven");

    private final String urlString;

    @Getter(lazy = true)
    private final URL url = generateUrl();

    @SneakyThrows(MalformedURLException.class)
    private final URL generateUrl() {
      return new URL(urlString);
    }
  }
}
