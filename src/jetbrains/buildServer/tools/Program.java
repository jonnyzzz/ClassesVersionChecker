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
import jetbrains.buildServer.tools.rules.PathSettings;
import jetbrains.buildServer.tools.rules.RulesParser;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static jetbrains.buildServer.tools.ClassVersionChecker.checkClasses;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 04.03.11 16:42
 */
public class Program {
  public static void main(String[] _args) throws ExecutionException, InterruptedException, IOException {
    if (_args.length != 2) {
      System.err.println("Invalid arguments. ");
      printUsageAndExit(1);
    }

    final File start = new File(_args[0]);
    System.out.println("Scanning started from: " + start);

    final File config = new File(_args[1]);
    System.out.println("Use configuration from: " + config);

    if (!start.exists()) {
      System.err.println("Search directory does not exits: " + start);
      printUsageAndExit(1);
    }

    if (!config.isFile()) {
      System.err.println("Configuration file not found: " + config);
      printUsageAndExit(1);
    }

    final File tempDir = new File(System.getProperty("java.io.tmpdir"));
    final Arguments args = new Arguments(start, config, File.createTempFile("ClassVersionChecker", "txt", tempDir));
    args.dumpTotalRules(System.out);

    processFiles(args);
  }

  private static void printUsageAndExit(int code) {
    System.out.println();
    System.out.println("Usage: java -jar classVersionChecker.jar <scan path> configFile.txt");
    System.out.println();
    System.exit(code);
  }

  private static void processFiles(@NotNull final Arguments args) {
    final ErrorsCollection reporting = new ErrorsCollection(args);

    final PathSettings rules = parseRules(args);

    final String settings_hash = rules.computeHash();

    rules.dumpTotalRules(System.out);

    checkClasses(args.getScanHome(), rules, reporting);
    System.out.println();
    System.out.println();

    final File reportFile = reporting.createReportFile();
    reporting.dumpShortReport(System.out);

    ///publish report as artifact
    System.out.println();
    System.out.println(" ##teamcity[publishArtifacts '" + reportFile.getPath() + "=>classVersionChecker-report.zip'] ");
    System.out.flush();

    if (reporting.hasErrors()) {
      int errors = reporting.getNumberOfErrors();
      System.err.println("There were " + errors + " class check errors detected");
      System.err.flush();
      System.err.flush();
      System.err.println("##teamcity[buildProblem identity='jcvc_" + settings_hash + "' description='" + errors + " class check errors detected']");
      System.err.println("##teamcity[buildStatisticValue key='ClassChecker.Errors' value='" + errors + "']");
      System.err.flush();
      System.exit(1);
      return;
    }
    System.exit(0);
  }

  @NotNull
  private static PathSettings parseRules(@NotNull Arguments args)  {
    return new RulesParser().parseConfig(args.getConfigFile()).build();
  }
}
