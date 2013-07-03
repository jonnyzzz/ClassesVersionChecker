package jetbrains.buildServer.tools.errors;

import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.TreeSet;

/**
* Created 03.07.13 20:59
*
* @author Eugene Petrenko (eugene.petrenko@jetbrains.com)
*/
public class ReportLongFileError {
  private final String myLongFile;
  private final Set<String> myErrors = new TreeSet<String>();

  public ReportLongFileError(@NotNull final String longFile) {
    myLongFile = longFile;
  }

  public void addCheckError(@NotNull String message) {
    myErrors.add(message);
  }

  public void render(@NotNull RenderMode mode, @NotNull LogWriter writer) {
    if (myErrors.isEmpty()) return;

    writer.println(myLongFile + " (" + getNumberOfErrors() + ")");
    final LogWriter offset = writer.offset();
    for (String error : myErrors) {
      offset.println(error);
    }
  }

  public int getNumberOfErrors() {
    return myErrors.size();
  }
}
