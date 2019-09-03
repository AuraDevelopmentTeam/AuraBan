package team.aura_dev.auraban.platform.common.dependency;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.Remapper;

public class DynamicLibraryReferenceTransformer implements ClassFileTransformer {
  private final String packageToProcess;
  private final String originalPackage;
  private final String resolvedPackage;

  DynamicLibraryReferenceTransformer(
      String packageToProcess, String originalPackage, String resolvedPackage) {
    this.packageToProcess = packageToProcess.replace('.', '/');
    this.originalPackage = originalPackage.replace('.', '/');
    this.resolvedPackage = resolvedPackage.replace('.', '/');
  }

  @Override
  public byte[] transform(
      ClassLoader loader,
      String className,
      Class<?> classBeingRedefined,
      ProtectionDomain protectionDomain,
      byte[] classfileBuffer) {
    if (!className.startsWith(this.packageToProcess)) {
      return null; // return null if you don't want to perform any changes
    }
    Remapper remapper =
        new Remapper() {
          @Override
          public String map(String typeName) {
            return typeName.replace(originalPackage, resolvedPackage);
          }
        };
    ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
    ClassRemapper classRemapper = new ClassRemapper(cw, remapper);
    ClassReader classReader = new ClassReader(classfileBuffer);
    classReader.accept(classRemapper, 0);
    return cw.toByteArray();
  }
}
