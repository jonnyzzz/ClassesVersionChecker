/*
 * Copyright 2000-2011 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
