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

import jetbrains.buildServer.tools.errors.ErrorsCollection;
import jetbrains.buildServer.tools.fs.FSScanFile;
import jetbrains.buildServer.tools.java.JavaCheckSettings;
import jetbrains.buildServer.tools.rules.PathSettings;
import jetbrains.buildServer.tools.rules.RulesParser;
import jetbrains.buildServer.tools.step.ClassFileScanStep;
import jetbrains.buildServer.tools.step.DirectoryScanStep;
import jetbrains.buildServer.tools.step.ScanZipStep;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.concurrent.ExecutionException;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 04.03.11 16:42
 */
public class Program {
  public static void main(String[] _args) throws ExecutionException, InterruptedException, IOException {
    System.out.println();
    System.out.println("Usage: java -jar classVersionChecker.jar <scan path> configFile.txt");
    System.out.println();

    if (_args.length != 2) {
      System.err.println("Invalid arguments. ");
      System.exit(1);
      return;
    }

    final File start = new File(_args[0]);
    System.out.println("Scanning started from: " + start);

    final File config = new File(_args[1]);
    System.out.println("Use configuration from: " + config);

    final Arguments args = new Arguments(start, config, new File(System.getProperty("java.io.tmpdir"), "classVersionChecker-report.txt"));
    args.dumpTotalRules(System.out);

    processFiles(args);
  }

  private static void processFiles(@NotNull final Arguments args) {
    final PathSettings rules = parseRules(args);
    rules.dumpTotalRules(System.out);

    if (!rules.validateRules(System.err)) {
      System.exit(2);
    }

    final ErrorsCollection reporting = new ErrorsCollection(args);
    processFiles(args.getScanHome(), rules, reporting);
    rules.assertVisited(reporting);

    System.out.println();
    System.out.println();
    System.out.println();

    final File reportFile = reporting.createReportFile();
    reporting.dumpShortReport(System.out);

    ///publish report as artifact
    System.out.println(" ##teamcity[publishArtifacts '" + reportFile.getPath() + "=>classVersionChecker-report.zip'] ");
    System.out.flush();

    if (reporting.hasErrors()) {
      System.err.println("There were " + reporting.getNumberOfErrors() + " class version errors detected");
      System.err.flush();
      System.err.flush();
      System.err.println("##teamcity[buildStatus status='FAILURE' text='" + reporting.getNumberOfErrors() + " class version errors. {build.status.text}']");
      System.err.flush();
      System.exit(1);
      return;
    }
    System.exit(0);
  }

  @NotNull
  private static PathSettings parseRules(@NotNull Arguments args)  {
    Reader rdr = null;
    try {
      rdr = new InputStreamReader(new FileInputStream(args.getConfigFile()), "utf-8");
      return RulesParser.parseConfig(args, rdr);
    } catch(IOException e) {
      System.err.println("Failed to parse settings. " + e.getMessage());
      throw new RuntimeException("No settings found");
    } finally {
      try {
        if (rdr != null) rdr.close();
      } catch (IOException e) {
        //NOP
      }
    }
  }

  public static void processFiles(@NotNull final File scanHome,
                                  @NotNull final PathSettings rules,
                                  @NotNull final ErrorReporting reporting) {
    final JavaCheckSettings settings = new JavaCheckSettings(rules);
    final Continuation c = new Continuation() {
      private final ScanStep[] steps = new ScanStep[]{
              new ClassFileScanStep(settings, reporting),
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

    c.postTask(new FSScanFile(scanHome));
  }

}
