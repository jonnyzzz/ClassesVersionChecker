package jetbrains.buildServer.tools;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 08.11.11 13:52
 */
public interface CheckSettings {

  boolean isDebugMode();

  boolean isPathExcluded(@NotNull final ScanFile file);

  /**
   * returns the way file must be checked
   * @param file fill file path with ! to split archive
   * @param error error reporter if needed
   * @return check action or null
   */
  @Nullable CheckAction getFileCheckMode(@NotNull final ScanFile file,
                                         @NotNull final ErrorReporting error);
}
