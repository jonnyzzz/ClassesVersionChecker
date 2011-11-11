package jetbrains.buildServer.tools;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 04.03.11 16:42
 */
public interface ScanStep {
  void process(@NotNull final ScanFile file, @NotNull final Continuation c) throws IOException;
}
