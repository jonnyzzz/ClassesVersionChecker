package jetbrains.buildServer.tools.fs;

import jetbrains.buildServer.tools.ScanFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 04.03.11 16:41
 */
public class FSScanFile implements ScanFile {
  private final File myFile;

  public FSScanFile(File file) {
    myFile = file;
  }

  @NotNull
  public InputStream openStream() throws FileNotFoundException {
    return new FileInputStream(myFile);
  }

  @NotNull
  public String getName() {
    return myFile.getPath();
  }

  public boolean isFile() {
    return myFile.isFile();
  }

  @NotNull
  public Collection<ScanFile> listFiles() {
    final File[] files = myFile.listFiles();
    if (files == null) return Collections.emptyList();
    List<ScanFile> sb = new ArrayList<ScanFile>();
    for (File file : files) {
      sb.add(new FSScanFile(file));
    }
    return sb;
  }
}
