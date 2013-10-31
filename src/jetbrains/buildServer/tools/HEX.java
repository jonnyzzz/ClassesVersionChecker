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

/**
 * Created 31.10.13 11:23
 *
 * @author Eugene Petrenko (eugene.petrenko@jetbrains.com)
 */
public class HEX {
  private static final String HEXES = "0123456789abcdef";

  public static String hex(@NotNull final byte[] raw) {
    final StringBuilder hex = new StringBuilder(2 * raw.length);
    for (byte b : raw) {
      hex
              .append(HEXES.charAt((b & 0xF0) >> 4))
              .append(HEXES.charAt((b & 0x0F)));
    }
    return hex.toString();
  }
}
