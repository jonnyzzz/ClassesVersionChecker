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
 * Created 31.07.13 19:03
 *
 * @author Eugene Petrenko (eugene.petrenko@jetbrains.com)
 */
public abstract class FSScanFileBase implements ScanFile {
  protected final File myFile;

  public FSScanFileBase(@NotNull final File file) {
    myFile = file;
  }

  @NotNull
  public InputStream openStream() throws FileNotFoundException {
    return new FileInputStream(myFile);
  }

  @NotNull
  public abstract String getName();

  public boolean isFile() {
    return myFile.isFile();
  }

  @Override
  public boolean isPhysical() {
    return true;
  }

  @NotNull
  public Collection<ScanFile> listFiles() {
    final File[] files = myFile.listFiles();
    if (files == null) return Collections.emptyList();
    final List<ScanFile> sb = new ArrayList<ScanFile>();
    for (File file : files) {
      sb.add(new FSScanFile(this, file));
    }
    return sb;
  }

  @Override
  public String toString() {
    return "FSScanFile{name=" + getName() + "}";
  }
}
