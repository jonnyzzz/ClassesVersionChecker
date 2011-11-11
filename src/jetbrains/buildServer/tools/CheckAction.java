package jetbrains.buildServer.tools;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 08.11.11 13:52
 */
public interface CheckAction {
  void process(@NotNull ScanFile file, @NotNull ErrorReporting reporting) throws IOException;
}
