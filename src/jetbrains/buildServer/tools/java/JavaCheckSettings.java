package jetbrains.buildServer.tools.java;

import jetbrains.buildServer.tools.CheckAction;
import jetbrains.buildServer.tools.CheckSettings;
import jetbrains.buildServer.tools.ErrorReporting;
import jetbrains.buildServer.tools.ScanFile;
import jetbrains.buildServer.tools.rules.PathRule;
import jetbrains.buildServer.tools.rules.PathRules;
import jetbrains.buildServer.tools.rules.PathSettings;
import jetbrains.buildServer.tools.rules.VersionRule;
import jetbrains.buildServer.tools.step.ClassFileChecker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 08.11.11 14:49
 */
public class JavaCheckSettings implements CheckSettings {
  private final PathRules<PathRule> myExcludes;
  private final PathRules<VersionRule> myVersionRules;

  public JavaCheckSettings(@NotNull final PathSettings rules) {
    myExcludes = new PathRules<PathRule>(rules.getExcludes());
    myVersionRules = new PathRules<VersionRule>(rules.getVersions());
  }

  public boolean isDebugMode() {
    final String debug = System.getenv("debug");
    return debug != null && ("enabled".equalsIgnoreCase(debug) || "true".equalsIgnoreCase(debug));
  }

  public boolean isPathExcluded(@NotNull ScanFile file) {
    return myExcludes.findRule(file) != null;
  }

  @Nullable
  public CheckAction getFileCheckMode(@NotNull ScanFile file, @NotNull final ErrorReporting error) {
    if (!file.getName().endsWith(".class")) return null;

    final VersionRule rule = myVersionRules.findRule(file);
    if (rule == null) {
      error.postCheckError(file, "No rule for file");
      return null;
    }
    return new ClassFileChecker(rule.getVersion());
  }

}
