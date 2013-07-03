package jetbrains.buildServer.tools.errors;

import jetbrains.buildServer.tools.ErrorKind;
import jetbrains.buildServer.tools.LazyMap;
import jetbrains.buildServer.tools.MultiMap;
import jetbrains.buildServer.tools.ScanFile;
import jetbrains.buildServer.tools.rules.PathRule;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Created 03.07.13 21:00
 *
 * @author Eugene Petrenko (eugene.petrenko@jetbrains.com)
 */
public class ReportErrors {
  private final PathsCalculator myPaths;
  private final MultiMap<String, String> myGenericErrorsMessageToFile = new MultiMap<String, String>();
  private final MultiMap<String, String> myCheckErrorToFile = new MultiMap<String, String>();
  private final LazyMap<ErrorKind, ReportKindError> myCheckErrors = new LazyMap<ErrorKind, ReportKindError>() {
    @NotNull
    @Override
    protected ReportKindError computeValue(@NotNull ErrorKind errorKind) {
      return new ReportKindError(myPaths, errorKind);
    }
  };

  public ReportErrors(@NotNull PathsCalculator paths) {
    myPaths = paths;
  }

  public void addCheckError(@NotNull final ScanFile file,
                            @NotNull final ErrorKind kind,
                            @NotNull final String shortError,
                            @NotNull final String message) {
    myCheckErrors.get(kind).addCheckError(file, message);
    myCheckErrorToFile.putValue(shortError, myPaths.path(file));
  }

  public void addGenericError(@NotNull final ScanFile file,
                              @NotNull final String message) {
    myGenericErrorsMessageToFile.putValue(message, myPaths.path(file));
  }

  public void addRuleError(@NotNull final PathRule rule,
                           @NotNull final String message) {
    myGenericErrorsMessageToFile.putValue(message, myPaths.path(rule.getPath()));
  }

  public void render(@NotNull RenderMode mode, @NotNull final LogWriter writer) {
    writer.println("Total errors: " + getNumberOfErrors());
    writer.println();
    writer.println();

    if (!myGenericErrorsMessageToFile.isEmpty()) {
      writer.println("Generic errors (" + myGenericErrorsMessageToFile.getValuesSize() + "):");
      renderGenericErrors(writer.offset());
      writer.println();
    }

    if (mode == RenderMode.SHORT && !myCheckErrorToFile.isEmpty()) {
      final int top = 100;
      writer.println("Top " + top + " errors: ");
      renderTopErrors(writer, top);
      writer.println();
    }

    if (!myCheckErrors.isEmpty()) {
      writer.println("Check errors (" + getNumberOfCheckErrors() + "):");
      renderCheckErrors(mode, writer.offset());
      writer.println();
    }

  }

  private void renderTopErrors(@NotNull final LogWriter writer, final int top) {
    final LogWriter offset = writer.offset();
    final List<String> keys = new ArrayList<String>(myCheckErrorToFile.keySet());
    Collections.sort(keys, new Comparator<String>() {
      public int compare(@NotNull String o1, @NotNull String o2) {
        int c1 = myCheckErrorToFile.get(o1).size();
        int c2 = myCheckErrorToFile.get(o2).size();

        if (c1 < c2) return 1;
        if (c1 > c2) return -1;
        return o1.compareToIgnoreCase(o2);
      }
    });

    final Iterator<String> it = keys.iterator();
    for(int i = 0; i < top && it.hasNext(); i++) {
      String err = it.next();
      offset.println(err + " (" + myCheckErrorToFile.get(err).size() + ")");
    }
  }

  private void renderCheckErrors(@NotNull final RenderMode mode, @NotNull final LogWriter writer) {
    for (ReportKindError e : myCheckErrors.values()) {
      e.render(mode, writer);
    }
  }

  private void renderGenericErrors(@NotNull final LogWriter writer) {
    for (Map.Entry<String, List<String>> e : myGenericErrorsMessageToFile.entrySet()) {
      writer.println(e.getKey());
      final LogWriter offset = writer.offset();
      for (String err : e.getValue()) {
        offset.println(err);
      }
    }
  }

  public boolean hasErrors() {
    return !myGenericErrorsMessageToFile.isEmpty() || myCheckErrors.isEmpty();
  }

  public int getNumberOfErrors() {
    return myGenericErrorsMessageToFile.getValuesSize() + getNumberOfCheckErrors();
  }

  public int getNumberOfCheckErrors() {
    int sz = 0;
    for (ReportKindError error : myCheckErrors.values()) {
      sz += error.getNumberOfErrors();
    }
    return sz;
  }
}
