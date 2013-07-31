package jetbrains.buildServer.tools;
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

import jetbrains.buildServer.tools.fs.FSScanFileBase;
import jetbrains.buildServer.tools.step.CheckFileScanStep;
import jetbrains.buildServer.tools.step.DirectoryScanStep;
import jetbrains.buildServer.tools.step.ScanZipStep;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 11.11.11 16:07
 */
public class FilesProcessor {

  public static void processFiles(@NotNull final File scanHome,
                                  @NotNull final CheckSettings settings,
                                  @NotNull final ErrorReporting reporting) {
    final Continuation c = new Continuation() {
      private final ScanStep[] steps = new ScanStep[]{
              new CheckFileScanStep(settings, reporting),
              new ScanZipStep(),
              new DirectoryScanStep(),
      };

      private int cnt = 0;

      public void postTask(@NotNull ScanFile file) {
        if (settings.isDebugMode()) {
          System.out.println("scanning: " + file.getName());
        } else {
          cnt++;
          if (cnt % 100 == 0) {
            System.out.print(".");
          }
          if (cnt % 4000 == 0) {
            System.out.println();
          }
        }

        if (settings.isPathExcluded(file)) {
          System.out.print("S");
          return;
        }

        for (ScanStep step : steps) {
          try {
            step.process(file, this);
          } catch (Throwable e) {
            reporting.postError(file, e.toString());
          }
        }
      }
    };

    c.postTask(new FSScanFileBase(scanHome){
      @NotNull
      @Override
      public String getName() {
        return "";
      }
    });
  }
}
