package jetbrains.buildServer.tools.errors;

import jetbrains.buildServer.tools.ErrorKind;
import jetbrains.buildServer.tools.LazyMap;
import jetbrains.buildServer.tools.ScanFile;
import org.jetbrains.annotations.NotNull;

/**
* Created 03.07.13 20:59
*
* @author Eugene Petrenko (eugene.petrenko@jetbrains.com)
*/
public class ReportKindError {
  private final PathsCalculator myPaths;
  private final ErrorKind myKind;
  private final LazyMap<String, ReportShortFileError> myShortErrors = new LazyMap<String, ReportShortFileError>() {
    @NotNull
    @Override
    protected ReportShortFileError computeValue(@NotNull String shortFile) {
      return new ReportShortFileError(myPaths, shortFile);
    }
  };

  public ReportKindError(@NotNull final PathsCalculator paths, @NotNull final ErrorKind kind) {
    myPaths = paths;
    myKind = kind;
  }

  public void addCheckError(@NotNull ScanFile file, @NotNull String message) {
    myShortErrors.get(myPaths.shortPath(file)).addCheckError(file, message);
  }

  public void render(@NotNull RenderMode mode, @NotNull LogWriter writer) {
    if (myShortErrors.isEmpty()) return;

    writer.println(myKind);
    final LogWriter offset = writer.offset();

    for (ReportShortFileError error : myShortErrors.values()) {
      error.render(mode, offset);
    }
    writer.println();
  }

  public int getNumberOfErrors() {
    int sz = 0;
    for (ReportShortFileError error : myShortErrors.values()) {
      sz += error.getNumberOfErrors();
    }
    return sz;
  }
}
