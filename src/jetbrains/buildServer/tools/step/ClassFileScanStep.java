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

import jetbrains.buildServer.tools.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 04.03.11 16:43
 */
public class ClassFileScanStep implements ScanStep {
  private final CheckSettings mySettings;
  private final ErrorReporting myErrors;

  public ClassFileScanStep(@NotNull final CheckSettings settings,
                           @NotNull final ErrorReporting errors) {
    mySettings = settings;
    myErrors = errors;
  }

  public void process(@NotNull final ScanFile file, @NotNull final Continuation c) throws IOException {
    if (!file.isFile()) return;

    final CheckAction mode = mySettings.getFileCheckMode(file, myErrors);
    if (mode != null) {
      mode.process(file, myErrors);
    }
  }
}
