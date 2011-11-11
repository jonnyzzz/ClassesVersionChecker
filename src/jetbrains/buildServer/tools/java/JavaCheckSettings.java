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

package jetbrains.buildServer.tools.java;

import jetbrains.buildServer.tools.CheckAction;
import jetbrains.buildServer.tools.CheckSettings;
import jetbrains.buildServer.tools.ErrorReporting;
import jetbrains.buildServer.tools.ScanFile;
import jetbrains.buildServer.tools.rules.PathSettings;
import jetbrains.buildServer.tools.step.ClassFileChecker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 08.11.11 14:49
 */
public class JavaCheckSettings implements CheckSettings {
  @NotNull
  private final PathSettings myRules;

  public JavaCheckSettings(@NotNull final PathSettings rules) {
    myRules = rules;
  }

  public boolean isDebugMode() {
    final String debug = System.getenv("debug");
    return debug != null && ("enabled".equalsIgnoreCase(debug) || "true".equalsIgnoreCase(debug));
  }

  public boolean isPathExcluded(@NotNull ScanFile file) {
    return myRules.isPathExcluded(file);
  }

  @Nullable
  public CheckAction getFileCheckMode(@NotNull ScanFile file, @NotNull final ErrorReporting error) {
    if (!file.getName().endsWith(".class")) return null;

    final JavaVersion version = myRules.getFileCheckMode(file);
    if (version == null) {
      error.postCheckError(file, "No rule for file");
      return null;
    }
    return new ClassFileChecker(version);
  }

}
