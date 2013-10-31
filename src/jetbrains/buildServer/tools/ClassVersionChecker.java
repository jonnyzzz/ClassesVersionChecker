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

import jetbrains.buildServer.tools.java.JavaCheckSettings;
import jetbrains.buildServer.tools.rules.PathSettings;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 11.11.11 17:20
 */
public class ClassVersionChecker {

  public static void checkClasses(@NotNull final File scanHome,
                                  @NotNull final PathSettings rules,
                                  @NotNull final ErrorReporting reporting) {
    FilesProcessor.processFiles(scanHome, new JavaCheckSettings(rules), reporting);
    rules.assertVisited(reporting);

    System.out.println();
  }
}
