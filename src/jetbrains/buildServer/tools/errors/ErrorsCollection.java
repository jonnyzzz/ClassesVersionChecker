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

package jetbrains.buildServer.tools.errors;

import jetbrains.buildServer.tools.Arguments;
import jetbrains.buildServer.tools.ErrorKind;
import jetbrains.buildServer.tools.ErrorReporting;
import jetbrains.buildServer.tools.ScanFile;
import jetbrains.buildServer.tools.rules.PathRule;
import org.jetbrains.annotations.NotNull;

import java.io.*;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 08.11.11 16:28
 */
public class ErrorsCollection implements ErrorReporting {
  private final Arguments myArguments;
  private final ReportErrors myReport;

  public ErrorsCollection(@NotNull final Arguments arguments) {
    myArguments = arguments;
    myReport = new ReportErrors(new PathsCalculator(arguments));
  }

  public void postCheckError(@NotNull ScanFile file, @NotNull ErrorKind kind, @NotNull String error) {
    myReport.addCheckError(file, kind, error, error);
  }

  public synchronized void postCheckError(@NotNull ScanFile file,
                                          @NotNull ErrorKind kind,
                                          @NotNull String shortError,
                                          @NotNull String detailedError) {
    myReport.addCheckError(file, kind, shortError, detailedError);
  }

  public synchronized void postError(@NotNull ScanFile file, @NotNull String error) {
    myReport.addGenericError(file, error);
  }

  public synchronized void ruleNotVisited(@NotNull PathRule rule) {
    myReport.addRuleError(rule, "Path was not found in the distribution");
  }

  public boolean hasErrors() {
    return myReport.hasErrors();
  }

  public int getNumberOfErrors() {
    return myReport.getNumberOfErrors();
  }

  public void dumpShortReport(@NotNull final PrintStream pw) {
    myReport.render(RenderMode.SHORT, new LogWriter(pw, ""));
  }

  @NotNull
  public File createReportFile() {
    final File reportFile = myArguments.getReport();
    //noinspection ResultOfMethodCallIgnored
    reportFile.getParentFile().mkdirs();
    PrintStream pw = null;
    try {
      pw = new PrintStream(new BufferedOutputStream(new FileOutputStream(reportFile)), true, "utf-8");

      final LogWriter writer = new LogWriter(pw, "");
      writer.println("Short description errors:");
      myReport.render(RenderMode.SHORT, writer.offset());

      writer.println();
      writer.println();

      writer.println("Full dump description errors:");
      myReport.render(RenderMode.FULL, writer.offset());

    } catch (IOException e) {
      System.err.println("Failed to create report file: " + reportFile + ". " + e.getMessage());
    } finally {
      if (pw != null) pw.close();
    }

    return reportFile;
  }
}
