package jetbrains.buildServer.tools.checkers;

import jetbrains.buildServer.tools.CheckAction;
import jetbrains.buildServer.tools.ErrorReporting;
import jetbrains.buildServer.tools.ScanFile;
import jetbrains.buildServer.tools.rules.StaticRuleSettings;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.*;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created 15.05.13 13:28
 *
 * @author Eugene Petrenko (eugene.petrenko@jetbrains.com)
 */
public class StaticFieldsChecker implements CheckAction {
  private final StaticRuleSettings mySettings;

  public StaticFieldsChecker(@NotNull final StaticRuleSettings settings) {
    mySettings = settings;
  }

  public void process(@NotNull final ScanFile file, @NotNull final ErrorReporting reporting) throws IOException {
    if (!file.getName().endsWith(".class")) return;

    final ClassReader reader = createReader(file);

    reader.accept(new ClassVisitor(Opcodes.ASM4) {
      private String myClassName = "<none>";

      @Override
      public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        myClassName = name.replace("/", ".");
        super.visit(version, access, name, signature, superName, interfaces);
      }

      @Override
      public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        checkFieldModifiers(access, name, desc, signature, value);
        return super.visitField(access, name, desc, signature, value);
      }

      private void checkFieldModifiers(int access, String name, String desc, String signature, Object value) {
        if ((Opcodes.ACC_NATIVE & access) != 0) return;
        if ((Opcodes.ACC_ENUM & access) != 0) return;
        if ((Opcodes.ACC_STATIC & access) == 0) return;

        if ((Opcodes.ACC_FINAL & access) == 0) {
          reporting.postCheckError(file, "Class '" + myClassName + "' contains non-final static field '" + name + "'");
          return;
        }

        final Type type = Type.getType(desc);
        final int sort = type.getSort();
        //allow primitive type constants
        if (sort != Type.OBJECT && sort != Type.METHOD) return;
        if (sort == Type.METHOD) return;
        if (type.getClassName().equals(String.class.getName())) return;
        if (mySettings.isClassAllowed(type.getClassName())) return;

        reporting.postCheckError(file, "Class '" + myClassName + "' contains final static field '" + name + "' of type '" + type.getClassName() + "'");
      }
    }, 0);
  }

  @NotNull
  private ClassReader createReader(@NotNull final ScanFile file) throws IOException {
    final InputStream inputStream = file.openStream();
    try {
      return new ClassReader(inputStream);
    } finally {
      inputStream.close();
    }
  }
}
