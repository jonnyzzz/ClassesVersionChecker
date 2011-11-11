package jetbrains.buildServer.tools.fs;

import jetbrains.buildServer.tools.ScanFile;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 04.03.11 16:51
 */
public class ZipScanFile implements ScanFile {
  private final ScanFile myParent;
  private final ZipInputStream myZip;
  private final ZipEntry myEntry;
  private final AtomicBoolean myStreamUsed = new AtomicBoolean(false);

  public ZipScanFile(ScanFile parent, ZipInputStream zip, ZipEntry entry) {
    myParent = parent;
    myZip = zip;
    myEntry = entry;
  }

  @NotNull
  public InputStream openStream() throws IOException {
    if (!myStreamUsed.compareAndSet(false, true)) {
      throw new IOException("Failed to open stream twice for " + getName());
    }

    final AtomicBoolean isClosed = new AtomicBoolean();
    return new BufferedInputStream(new InputStream() {
      @Override
      public int read() throws IOException {
        if (isClosed.get()) return -1;
        return myZip.read();
      }

      @Override
      public int read(byte[] b) throws IOException {
        if (isClosed.get()) return 0;
        return myZip.read(b);
      }

      @Override
      public int read(byte[] b, int off, int len) throws IOException {
        if (isClosed.get()) return 0;
        return myZip.read(b, off, len);
      }

      @Override
      public void close() throws IOException {
        isClosed.set(true);
      }
    });
  }

  @NotNull
  public String getName() {
    return myParent.getName() + "!" + myEntry.getName();
  }

  public boolean isFile() {
    return !myEntry.isDirectory();
  }
}
