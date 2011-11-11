package jetbrains.buildServer.tools;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 04.03.11 16:39
 */
public interface ScanFile {
  @NotNull
  InputStream openStream() throws IOException;

  @NotNull
  String getName();

  boolean isFile();
}
