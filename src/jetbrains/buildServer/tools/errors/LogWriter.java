package jetbrains.buildServer.tools.errors;

import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;

/**
* Created 03.07.13 20:53
*
* @author Eugene Petrenko (eugene.petrenko@jetbrains.com)
*/
public class LogWriter {
  private final PrintStream myWriter;
  private final String myOffset;

  public LogWriter(@NotNull final PrintStream writer, @NotNull final String offset) {
    myOffset = offset;
    myWriter = writer;
  }

  public void println() {
    myWriter.println();
  }

  public void println(@NotNull Object s) {
    myWriter.println(myOffset + s);
  }

  @NotNull
  public LogWriter offset() {
    return new LogWriter(myWriter, "  " + myOffset);
  }
}
