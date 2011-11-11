package jetbrains.buildServer.tools.step;

import jetbrains.buildServer.tools.Continuation;
import jetbrains.buildServer.tools.ScanFile;
import jetbrains.buildServer.tools.ScanStep;
import jetbrains.buildServer.tools.fs.FSScanFile;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 04.03.11 16:46
 */
public class DirectoryScanStep implements ScanStep {
  public void process(@NotNull ScanFile file, @NotNull Continuation c) {
    if (file instanceof FSScanFile) {
      final Collection<ScanFile> scanFiles = ((FSScanFile) file).listFiles();
      if (!scanFiles.isEmpty()) {
        for (ScanFile scanFile : scanFiles) {
          c.postTask(scanFile);
        }
      }
    }
  }
}
