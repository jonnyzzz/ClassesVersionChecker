/*
 * Copyright 2000-2013 JetBrains s.r.o.
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

package jetbrains.buildServer.tools;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created 15.05.13 13:20
 *
 * @author Eugene Petrenko (eugene.petrenko@jetbrains.com)
 */
public class CachedScanFile implements ScanFile {
  private static final int BUFF_SIZE = 128 * 1024;
  private final ScanFile myHost;
  private volatile byte[] myCache = null;

  public CachedScanFile(@NotNull ScanFile host) {
    if (!host.isFile()) throw new RuntimeException("Only files are supported");
    myHost = host;
  }

  @NotNull
  public InputStream openStream() throws IOException {
    byte[] cache = myCache;
    if (cache != null) return new ByteArrayInputStream(cache);

    synchronized (this) {
      cache = myCache;
      if (cache != null) return new ByteArrayInputStream(cache);

      final ByteArrayOutputStream bos = new ByteArrayOutputStream(BUFF_SIZE);
      final byte[] buff = new byte[BUFF_SIZE];
      final InputStream is = myHost.openStream();
      try {
        int x;
        while((x = is.read(buff)) > 0) {
          bos.write(buff, 0, x);
        }
      } finally {
        is.close();
      }
      bos.close();
      return new ByteArrayInputStream(myCache = bos.toByteArray());
    }
  }

  @NotNull
  public String getName() {
    return myHost.getName();
  }

  public boolean isFile() {
    return myHost.isFile();
  }

  @Override
  public String toString() {
    return myHost.toString();
  }
}
