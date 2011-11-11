package jetbrains.buildServer.tools.rules;

import jetbrains.buildServer.tools.java.JavaVersion;
import org.jetbrains.annotations.NotNull;

/**
* @author Eugene Petrenko (eugene.petrenko@gmail.com)
*         Date: 08.11.11 15:41
*/
public class VersionRule extends PathRule {
  private final JavaVersion myVersion;

  public VersionRule(@NotNull final String path, @NotNull final JavaVersion version) {
    super(path);
    myVersion = version;
  }

  @NotNull
  public JavaVersion getVersion() {
    return myVersion;
  }
}
