package jetbrains.buildServer.tools;

import org.jetbrains.annotations.NotNull;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 04.03.11 16:46
 */
public interface Continuation {
  void postTask(@NotNull ScanFile file);
}
