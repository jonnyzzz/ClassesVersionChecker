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
import jetbrains.buildServer.tools.ErrorReporting;
import jetbrains.buildServer.tools.MultiMap;
import jetbrains.buildServer.tools.ScanFile;
import jetbrains.buildServer.tools.rules.PathRule;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.*;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 08.11.11 16:28
 */
public class ErrorsCollection implements ErrorReporting {
  private final Arguments myArguments;
  private final MultiMap<String, String> myFileToMessage = new MultiMap<String, String>();
  private final MultiMap<String, String> myGenericErrors = new MultiMap<String, String>();
  private final String myScanPath;

  private final MultiMap<String, String> myShortErrors = new MultiMap<String, String>();

  public ErrorsCollection(@NotNull final Arguments arguments) {
    myArguments = arguments;
    myScanPath = myArguments.getScanHome().getPath();
  }

  private void updateShortErrors(@NotNull final ScanFile file, @NotNull final String error) {
    synchronized (myShortErrors){
      String path = path(file);
      int idx = path.lastIndexOf('!');
      if (idx > 0) {
        path = path.substring(0, idx);
      }
      if (!myShortErrors.containsKey(path)) {
        System.out.print("F");
      }
      myShortErrors.putValue(path, error);
    }
  }

  public void postCheckError(@NotNull ScanFile file, @NotNull String error) {
    updateShortErrors(file, error);
    synchronized (myFileToMessage) {
      myFileToMessage.putValue(path(file), error);
    }
  }

  private String path(@NotNull ScanFile file) {
    return path(file.getName());
  }

  private String path(@NotNull String str) {
    if (str.startsWith(myScanPath)) {
      str = str.substring(myScanPath.length());
    }
    while (str.length() > 0 && (str.charAt(0) == '/' || str.charAt(0) == '\\')) str = str.substring(1);
    return str;
  }

  public void postError(@NotNull ScanFile file, @NotNull String error) {
    updateShortErrors(file, error);
    synchronized (myGenericErrors) {
      myGenericErrors.putValue(path(file), error);
    }
  }

  public void ruleNotVisited(@NotNull PathRule rule) {
    final String path = path(rule.getPath());
    final String msg = "Path was not found in the distribution";

    synchronized (myGenericErrors) {
      myGenericErrors.putValue(path, msg);
    }
    synchronized (myShortErrors) {
      myShortErrors.putValue(path, msg);
    }
  }

  public boolean hasErrors() {
    return !myFileToMessage.isEmpty() || !myGenericErrors.isEmpty();
  }

  public int getNumberOfErrors() {
    return myGenericErrors.size() + myFileToMessage.getValuesSize();
  }

  public void dumpShortReport(@NotNull final PrintStream pw) {
    printMap(pw, myShortErrors, "Errors");
  }

  @NotNull
  public File createReportFile() {
    final File reportFile = myArguments.getReport();
    //noinspection ResultOfMethodCallIgnored
    reportFile.getParentFile().mkdirs();
    PrintStream pw = null;
    try {
      pw = new PrintStream(new BufferedOutputStream(new FileOutputStream(reportFile)), true, "utf-8");
      dumpShortReport(pw);

      printMap(pw, myGenericErrors, "General Errors");
      printMap(pw, myFileToMessage, "Class File Errors");
    } catch (IOException e) {
      System.err.println("Failed to create report file: " + reportFile + ". " + e.getMessage());
    } finally {
      if (pw != null) pw.close();
    }

    return reportFile;
  }

  private void printMap(@NotNull final PrintStream pw, @NotNull final MultiMap<String, String> map, @NotNull final String title) {
    if (map.isEmpty()) {
      pw.println("No " + title + " were found");
      pw.println();
      return;
    }

    final MultiMap<String, String> errorToFile = new MultiMap<String, String>();
    for (Map.Entry<String, List<String>> e : map.entrySet()) {
      final String file = e.getKey();
      for (String val : e.getValue()) {
        errorToFile.putValue(val, file);
      }
    }

    pw.println(title + " were found in: ");

    List<Map.Entry<String,List<String>>> keys = new ArrayList<Map.Entry<String,List<String>>>(errorToFile.entrySet());
    Collections.sort(keys, new Comparator<Map.Entry<String, List<String>>>() {
      public int compare(Map.Entry<String, List<String>> o1, Map.Entry<String, List<String>> o2) {
        return o1.getKey().compareToIgnoreCase(o2.getKey());
      }
    });

    for (Map.Entry<String, List<String>> e : keys) {
      pw.println("  Error: " + e.getKey());
      for (String s : new TreeSet<String>(e.getValue())) {
        pw.println("    " + s);
      }
      pw.println();
    }
    pw.println();
  }
}
