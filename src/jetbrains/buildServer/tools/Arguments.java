package jetbrains.buildServer.tools;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 08.11.11 15:48
 */
public class Arguments {
  private final File myScanHome;
  private final File myConfigFile;
  private final File myReport;

  public Arguments(@NotNull final File scanHome,
                   @NotNull final File configFile,
                   @NotNull final File report) throws IOException {
    myReport = report;
    myScanHome = scanHome.getCanonicalFile();
    myConfigFile = configFile.getCanonicalFile();
  }

  @NotNull
  public File getReport() {
    return myReport;
  }

  @NotNull
  public File getConfigFile() {
    return myConfigFile;
  }

  @NotNull
  public File getScanHome() {
    return myScanHome;
  }

  public void dumpTotalRules(@NotNull final PrintStream ps) {
    ps.println();
    ps.println("Current scan settings: ");
    ps.println("Scan home: " + myScanHome);
    ps.println();
  }
}
