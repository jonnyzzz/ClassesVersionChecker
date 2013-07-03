package jetbrains.buildServer.tools.errors;

import jetbrains.buildServer.tools.LazyMap;
import jetbrains.buildServer.tools.ScanFile;
import org.jetbrains.annotations.NotNull;

/**
* Created 03.07.13 20:59
*
* @author Eugene Petrenko (eugene.petrenko@jetbrains.com)
*/
public class ReportShortFileError {
  private final PathsCalculator myPaths;
  private final String myShortFile;
  private final LazyMap<String, ReportLongFileError> myLongErrors = new LazyMap<String, ReportLongFileError>() {
    @NotNull
    @Override
    protected ReportLongFileError computeValue(@NotNull String s) {
      return new ReportLongFileError(s);
    }
  };

  public ReportShortFileError(@NotNull final PathsCalculator paths, @NotNull final String shortFile) {
    myPaths = paths;
    myShortFile = shortFile;
  }

  public void addCheckError(@NotNull final ScanFile file, @NotNull final String message) {
    myLongErrors.get(myPaths.path(file)).addCheckError(message);
  }

  public void render(@NotNull RenderMode mode, @NotNull final LogWriter writer) {
    if (myLongErrors.isEmpty()) return;

    writer.println(myShortFile);

    if (mode != RenderMode.FULL) return;
    for (ReportLongFileError error : myLongErrors.values()) {
      error.render(mode, writer.offset());
    }
  }

  public int getNumberOfErrors() {
    int sz = 0;
    for (ReportLongFileError error : myLongErrors.values()) {
      sz += error.getNumberOfErrors();
    }
    return sz;
  }
}
