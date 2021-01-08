package team.aura_dev.auraban.platform.common.dependency;

import lombok.experimental.UtilityClass;
import team.aura_dev.lib.multiplatformcore.dependency.RuntimeDependency;
import team.aura_dev.lib.multiplatformcore.dependency.RuntimeDependency.Maven;

@UtilityClass
public class RuntimeDependencies {
  ////////////////////////////////////////////////////////
  // Config
  ////////////////////////////////////////////////////////
  public static final RuntimeDependency CONFIGURATE_HOCON =
      RuntimeDependency.builder(
              "org.spongepowered",
              "configurate-hocon",
              "3.6.1",
              "6395403afce7b9bbf4e26ef74c13da9a",
              "e3f199dbd91de753a70f63606f530fdb8644bbd5")
          .maven(Maven.SPONGE)
          .transitive()
          .exclusion("com.google.code.findbugs:jsr305")
          .exclusion("com.google.errorprone:error_prone_annotations")
          .exclusion("com.google.j2objc:j2objc-annotations")
          // org.codehaus.mojo gets relocated. That's why we need to make sure we don't have a
          // literal "org.codehaus.mojo" in any strings
          .exclusion("org.Codehaus.mojo:animal-sniffer-annotations".toLowerCase())
          .build();

  ////////////////////////////////////////////////////////
  // Databases/Storage Engines
  ////////////////////////////////////////////////////////
  public static final RuntimeDependency H2_DATABASE =
      RuntimeDependency.builder(
              "com.h2database",
              "h2",
              "1.4.200",
              "18c05829a03b92c0880f22a3c4d1d11d",
              "f7533fe7cb8e99c87a43d325a77b4b678ad9031a")
          .build();
  public static final RuntimeDependency HIKARI_CP =
      RuntimeDependency.builder(
              "com.zaxxer",
              "HikariCP",
              "3.4.1",
              "930d5942faeb0d1e82173cb19aa85be6",
              "842894380a73b72c2ecd9e483403e4d5ef7d8b76")
          .build();
  public static final RuntimeDependency MARIADB_CLIENT =
      RuntimeDependency.builder(
              "org.mariadb.jdbc",
              "mariadb-java-client",
              "2.5.1",
              "f9b182e09039ee33917e78d2d3900aa0",
              "eeddfc676ca7abb49d052c904bd310e07705c7ac")
          .build();

  ////////////////////////////////////////////////////////
  // Utilities
  ////////////////////////////////////////////////////////
  public static final RuntimeDependency CAFFEINE =
      RuntimeDependency.builder(
              "com.github.ben-manes.caffeine",
              "caffeine",
              "2.8.0",
              "d6dbff7e409b1c2ad88930e2c220ea13",
              "6000774d7f8412ced005a704188ced78beeed2bb")
          .build();
}
