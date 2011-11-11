package jetbrains.buildServer.tools.errors;

import jetbrains.buildServer.tools.Arguments;
import jetbrains.buildServer.tools.ErrorReporting;
import jetbrains.buildServer.tools.MultiMap;
import jetbrains.buildServer.tools.ScanFile;
import jetbrains.buildServer.tools.rules.VersionRule;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 08.11.11 16:28
 */
public class ErrorsCollection implements ErrorReporting {
  private final Arguments myArguments;
  private final Map<String, String> myFileToMessage = new TreeMap<String, String>();
  private final Map<String, String> myGenericErrors = new TreeMap<String, String>();
  private final String myScanPath;

  private final Map<String, String> myShortErrors = new TreeMap<String, String>();

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
      if (myShortErrors.put(path, error) == null) {
        System.out.print("F");
      }
    }
  }

  public void postCheckError(@NotNull ScanFile file, @NotNull String error) {
    updateShortErrors(file, error);
    synchronized (myFileToMessage) {
      myFileToMessage.put(path(file), error);
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
      myGenericErrors.put(path(file), error);
    }
  }

  public void ruleNotVisited(@NotNull VersionRule rule) {
    final String path = path(rule.getPath());
    final String msg = "Path was not found in the distribution";

    synchronized (myGenericErrors) {
      myGenericErrors.put(path, msg);
    }
    synchronized (myShortErrors) {
      myShortErrors.put(path, msg);
    }
  }

  public boolean hasErrors() {
    return !myFileToMessage.isEmpty() || !myGenericErrors.isEmpty();
  }

  public int getNumberOfErrors() {
    return myGenericErrors.size() + myFileToMessage.size();
  }

  public void dumpShortReport(@NotNull final PrintStream pw) {
    printMap(pw, myShortErrors, "Errors");
  }

  public void createReportFile() {
    final File reportFile = myArguments.getReport();
    //noinspection ResultOfMethodCallIgnored
    reportFile.getParentFile().mkdirs();
    try {
      final PrintStream pw = new PrintStream(new BufferedOutputStream(new FileOutputStream(reportFile)), true, "utf-8");
      dumpShortReport(pw);

      printMap(pw, myGenericErrors, "General Errors");
      printMap(pw, myFileToMessage, "Class File Errors");
    } catch (IOException e) {
      System.err.println("Failed to create report file: " + reportFile + ". " + e.getMessage());
    }
  }

  private void printMap(@NotNull final PrintStream pw, @NotNull final Map<String, String> map, @NotNull final String title) {
    if (map.size() == 0) {
      pw.println("No " + title + " were found");
      pw.println();
      return;
    }

    final MultiMap<String, String> errorToFile = new MultiMap<String, String>();
    for (Map.Entry<String, String> e : map.entrySet()) {
      errorToFile.putValue(e.getValue(), e.getKey());
    }

    pw.println(title + " were found in: ");

    for (Map.Entry<String, List<String>> e : errorToFile.entrySet()) {
      pw.println("  Error: " + e.getKey());
      for (String s : new TreeSet<String>(e.getValue())) {
        pw.println("    " + s);
      }
      pw.println();
    }
    pw.println();
  }
}
