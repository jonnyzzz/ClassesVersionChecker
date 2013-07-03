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
    if (!(name.endsWith(".war") || name.endsWith(".zip") || name.endsWith(".jar"))) return;

    final ZipInputStream zip = openZip(file, name);
    try {
      for(ZipEntry ze = zip.getNextEntry(); ze != null; ze = zip.getNextEntry()) {
        c.postTask(new ZipScanFile(file, zip, ze));
      }
    } finally {
      zip.close();
    }
  }

  private ZipInputStream openZip(@NotNull final ScanFile file,
                                 @NotNull final String name) throws IOException {
    if (name.endsWith(".zip")) {
      return new ZipInputStream(new BufferedInputStream(file.openStream()));
    }

    if (name.endsWith(".war")) {
      return new ZipInputStream(new BufferedInputStream(file.openStream()));
    }

    if (name.endsWith(".jar")) {
      return new JarInputStream(new BufferedInputStream(file.openStream()));
    }

    throw new IOException("Unexpected archive type: " + name);
  }
}
