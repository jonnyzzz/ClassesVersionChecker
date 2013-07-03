package jetbrains.buildServer.tools.errors;

import jetbrains.buildServer.tools.ErrorKind;
import jetbrains.buildServer.tools.LazyMap;
import jetbrains.buildServer.tools.MultiMap;
import jetbrains.buildServer.tools.ScanFile;
import jetbrains.buildServer.tools.rules.PathRule;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * Created 03.07.13 21:00
 *
 * @author Eugene Petrenko (eugene.petrenko@jetbrains.com)
 */
public class ReportErrors {
  private final PathsCalculator myPaths;
  private final MultiMap<String, String> myGenericErrorsMessageToFile = new MultiMap<String, String>();
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
                            @NotNull final String message) {
    myCheckErrors.get(kind).addCheckError(file, message);
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
    if (!myGenericErrorsMessageToFile.isEmpty()) {
      writer.println("Generic errors (" + myGenericErrorsMessageToFile.getValuesSize() + "):");
      renderGenericErrors(writer.offset());
      writer.println();
    }

    if (!myCheckErrors.isEmpty()) {
      writer.println("Check errors (" + getNumberOfCheckErrors() + "):");
      renderCheckErrors(mode, writer.offset());
      writer.println();
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
