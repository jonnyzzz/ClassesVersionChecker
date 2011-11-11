package jetbrains.buildServer.tools.java;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 08.11.11 13:58
 *
 */
public enum JavaVersion {
  ///see http://en.wikipedia.org/wiki/Java_class_file

  Java_1_7(51, "Java 7", "1.7"),
  Java_1_6(50, "Java 1.6", "1.6"),
  Java_1_5(49, "Java 1.5", "1.5"),
  Java_1_4(48, "Java 1.4", "1.4"),
  Java_1_3(47, "Java 1.3", "1.3"),
  Java_1_2(46, "Java 1.2", "1.2"),
  ;

  private final int myClassVersion;
  private final String myName;
  private final String myShortName;

  JavaVersion(int classVersion, String name, String shortName) {
    myClassVersion = classVersion;
    myName = name;
    myShortName = shortName;
  }

  public int getClassVersion() {
    return myClassVersion;
  }

  @NotNull
  public String getName() {
    return myName;
  }

  @NotNull
  public String getShortName() {
    return myShortName;
  }

  @Override
  public String toString() {
    return getName() + " (" + getClassVersion() + ")";
  }

  public boolean canRunOn(@NotNull final JavaVersion jvm) {
    return this.getClassVersion() <= jvm.getClassVersion();
  }

  @Nullable
  public static JavaVersion find(int v) {
    for (JavaVersion version : values()) {
      if (version.getClassVersion() == v) return version;
    }
    if (v < Java_1_2.getClassVersion()) return Java_1_2;
    return null;
  }
}
