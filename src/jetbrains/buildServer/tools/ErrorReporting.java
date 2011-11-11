package jetbrains.buildServer.tools;

import jetbrains.buildServer.tools.rules.VersionRule;
import org.jetbrains.annotations.NotNull;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 08.11.11 14:37
 */
public interface ErrorReporting {
  void postCheckError(@NotNull final ScanFile file, @NotNull final String error);
  void postError(@NotNull final ScanFile file, @NotNull final String error);

  void ruleNotVisited(@NotNull final VersionRule rule);
}
