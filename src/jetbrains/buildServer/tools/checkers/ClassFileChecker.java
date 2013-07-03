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

package jetbrains.buildServer.tools.checkers;

import jetbrains.buildServer.tools.CheckAction;
import jetbrains.buildServer.tools.ErrorReporting;
import jetbrains.buildServer.tools.java.JavaVersion;
import jetbrains.buildServer.tools.ScanFile;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 08.11.11 14:37
 */
public class ClassFileChecker implements CheckAction {
  private final JavaVersion myVersion;

  public ClassFileChecker(@NotNull final JavaVersion version) {
    myVersion = version;
  }

  public void process(@NotNull ScanFile file, @NotNull ErrorReporting reporting) throws IOException {
    checkVersion(file, reporting);
  }

  private void checkVersion(@NotNull final ScanFile file, @NotNull final ErrorReporting reporting) throws IOException {
    final InputStream stream = new BufferedInputStream(file.openStream());
    try {
      if (!checkCAFEBABE(file, stream, reporting)) {
        return;
      }
      //noinspection ResultOfMethodCallIgnored
      stream.read();
      //noinspection ResultOfMethodCallIgnored
      stream.read();

      final int v = stream.read() * 256 + stream.read();
      final JavaVersion version = JavaVersion.find(v);

      if (version == null) {
        reporting.postCheckError(file, "version is unknown ( " + v + ")");
        return;
      }

      if (!version.canRunOn(myVersion)) {
        reporting.postCheckError(file, "version is " + version + " but expected to be <= " + myVersion);
      }

    } finally {
      stream.close();
    }
  }

  private boolean checkCAFEBABE(@NotNull ScanFile file, @NotNull InputStream stream, @NotNull final ErrorReporting reporting) throws IOException {
    int[] data = new int[4];
    for (int i = 0; i < data.length; i++) {
      data[i] = stream.read();
    }

    if (data[0] != 0xCA || data[1] != 0xFE || data[2] != 0xBA || data[3] != 0xBE) {
      reporting.postCheckError(file, "Class file must start with 0xCAFEBABE, but was: " + byteToHex(data));
      return false;
    }

    return true;
  }

  private String byteToHex(int[] data) {
    StringBuilder sb = new StringBuilder();
    for (int b : data) {
      sb.append(toHex(b));
    }
    return sb.toString();
  }

  private String toHex(int v) {
    if (v == 0) return "0";
    String s = "";
    if (v < 0) {
      s = "-";
      v = -v;
    }
    while (v > 0) {
      int h = v % 16;
      v /= 16;
      if (h < 10) {
        s = h + s;
      } else {
        s = (char) (h - 10 + 'a') + s;
      }
    }
    return s;
  }

}
