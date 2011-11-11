package jetbrains.buildServer.tools.step;

import jetbrains.buildServer.tools.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 04.03.11 16:43
 */
public class ClassFileScanStep implements ScanStep {
  private final CheckSettings mySettings;
  private final ErrorReporting myErrors;

  public ClassFileScanStep(@NotNull final CheckSettings settings,
                           @NotNull final ErrorReporting errors) {
    mySettings = settings;
    myErrors = errors;
  }

  public void process(@NotNull final ScanFile file, @NotNull final Continuation c) throws IOException {
    if (!file.isFile()) return;

    final CheckAction mode = mySettings.getFileCheckMode(file, myErrors);
    if (mode != null) {
      mode.process(file, myErrors);
    }
  }
}
