package jetbrains.buildServer.tools.fs;

import jetbrains.buildServer.tools.ScanFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Created 31.07.13 18:56
 *
 * @author Eugene Petrenko (eugene.petrenko@jetbrains.com)
 */
public class Naming {
  @NotNull
  public static String resolveChildFile(@NotNull final ScanFile file,
                                        @NotNull final File child) {
    return resolveChildName(file, "/", child.getName());
  }

  @NotNull
  public static String resolveZipEntry(@NotNull final ScanFile file,
                                       @NotNull final String zipPath) {
    return resolveChildName(file, "!", normalizePath(zipPath));
  }

  private static String resolveChildName(@NotNull final ScanFile parent,
                                        @NotNull final String sep,
                                        @NotNull final String childName) {
    final String parentName = parent.getName();
    if (parentName.isEmpty()) return childName;
    return parentName + sep + childName;
  }

  @NotNull
  public static String normalizePath(@NotNull String path) {
    path = path.trim().replaceAll("[\\\\/]+", "/");
    while (path.startsWith("/")) path = path.substring(1);
    return path;
  }
}
