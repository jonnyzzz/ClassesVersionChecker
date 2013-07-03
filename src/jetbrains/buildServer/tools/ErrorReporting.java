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

package jetbrains.buildServer.tools;

import jetbrains.buildServer.tools.rules.PathRule;
import org.jetbrains.annotations.NotNull;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 08.11.11 14:37
 */
public interface ErrorReporting {
  void postCheckError(@NotNull ScanFile file, @NotNull ErrorKind kind, @NotNull final String error);

  void postCheckError(@NotNull ScanFile file,
                      @NotNull ErrorKind kind,
                      @NotNull String shortError,
                      @NotNull String detailedError);


  void postError(@NotNull final ScanFile file, @NotNull final String error);

  void ruleNotVisited(@NotNull final PathRule rule);
}
