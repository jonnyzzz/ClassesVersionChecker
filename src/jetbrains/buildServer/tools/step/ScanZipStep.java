package jetbrains.buildServer.tools.step;

import jetbrains.buildServer.tools.Continuation;
import jetbrains.buildServer.tools.ScanFile;
import jetbrains.buildServer.tools.ScanStep;
import jetbrains.buildServer.tools.fs.ZipScanFile;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 04.03.11 16:49
 */
public class ScanZipStep implements ScanStep {
  public void process(@NotNull final ScanFile file, @NotNull final Continuation c) throws IOException {
    final String name = file.getName();
    if (!(name.endsWith(".zip") || name.endsWith(".jar"))) return;

    final ZipInputStream zip = openZip(file, name);
    try {
      for(ZipEntry ze = zip.getNextEntry(); ze != null; ze = zip.getNextEntry()) {
        c.postTask(new ZipScanFile(file, zip, ze));
      }
    } finally {
      zip.close();
    }
  }

  private ZipInputStream openZip(ScanFile file, String name) throws IOException {
    ZipInputStream zip;
    if (name.endsWith(".zip")) {
      zip = new ZipInputStream(new BufferedInputStream(file.openStream()));
    } else {
      zip = new JarInputStream(new BufferedInputStream(file.openStream()));
    }
    return zip;
  }
}
