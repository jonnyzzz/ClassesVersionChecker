package jetbrains.buildServer.tools.rules;

import jetbrains.buildServer.tools.ErrorReporting;
import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;
import java.util.Collection;

/**
* @author Eugene Petrenko (eugene.petrenko@gmail.com)
*         Date: 08.11.11 18:00
*/
public class PathSettings {
  private final Collection<PathRule> myExcludes;
  private final Collection<VersionRule> myVersions;

  public PathSettings(Collection<PathRule> excludes, Collection<VersionRule> versions) {
    myExcludes = excludes;
    myVersions = versions;
  }

  public Collection<PathRule> getExcludes() {
    return myExcludes;
  }

  public Collection<VersionRule> getVersions() {
    return myVersions;
  }

  public boolean validateRules(@NotNull final PrintStream ps) {
    boolean failed = true;
    for (PathRule exclude : myExcludes) {
      if (!exclude.getBaseFile().exists()) {
        ps.println("Exclude rule file " + exclude.getPath() + " does not exist");
      }
    }

    for (PathRule exclude : myVersions) {
      if (!exclude.getBaseFile().exists()) {
        ps.println("Version rule " + exclude.getPath() + " does not match existing file");
        failed = false;
      }
    }
    return failed;
  }


  public void assertVisited(@NotNull final ErrorReporting reporting) {
    for (VersionRule rule : myVersions) {
      if (!rule.isVisited()) {
        reporting.ruleNotVisited(rule);
      }
    }
  }


  public void dumpTotalRules(@NotNull final PrintStream ps) {
    ps.println("Excludes: ");
    for (PathRule e : myExcludes) {
      ps.println("  " + e.getPath());
    }
    ps.println();
    ps.println("Versions to check: ");
    for (VersionRule e : myVersions) {
      ps.println("  " + e.getVersion() + " => " + e.getPath());
    }
    ps.println();
    ps.println();
  }

}
