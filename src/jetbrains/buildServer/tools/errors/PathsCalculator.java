package jetbrains.buildServer.tools.errors;

import jetbrains.buildServer.tools.Arguments;
import jetbrains.buildServer.tools.ScanFile;
import org.jetbrains.annotations.NotNull;

/**
* Created 03.07.13 20:57
*
* @author Eugene Petrenko (eugene.petrenko@jetbrains.com)
*/
public class PathsCalculator {
  private final String myScanPath;

  public PathsCalculator(@NotNull Arguments argz) {
    myScanPath = argz.getScanHome().getPath();
  }


  @NotNull
  public String path(@NotNull ScanFile file) {
    return path(file.getName());
  }

  @NotNull
  public String path(@NotNull String str) {
    if (str.startsWith(myScanPath)) {
      str = str.substring(myScanPath.length());
    }
    while (str.length() > 0 && (str.charAt(0) == '/' || str.charAt(0) == '\\')) str = str.substring(1);
    return str;
  }

  @NotNull
  public String shortPath(@NotNull ScanFile file) {
    String path = path(file);
    int x = path.lastIndexOf('!');
    if (x <= 0) return path;
    return path.substring(0, x);
  }
}
