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

import jetbrains.buildServer.tools.*;
import jetbrains.buildServer.tools.rules.CheckHolder;
import jetbrains.buildServer.tools.rules.PathSettings;
import org.jetbrains.annotations.NotNull;

import java.util.*;

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

  @NotNull
  public Collection<? extends CheckAction> getFileCheckMode(@NotNull ScanFile file, @NotNull final ErrorReporting error) {
    if (!file.getName().endsWith(".class")) return Collections.emptyList();

    Collection<? extends CheckHolder> modes = myRules.getFileCheckMode(file);
    if (modes.isEmpty()) {
      error.postCheckError(file, ErrorKind.PATTERN, "No rule for file");
      return Collections.emptyList();
    }

    final List<CheckAction> actions = new ArrayList<CheckAction>();
    for (CheckHolder holder : modes) {
      actions.add(holder.getCheckAction());
    }

    return actions;
  }

}
