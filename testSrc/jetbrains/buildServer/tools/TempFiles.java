package jetbrains.buildServer.tools;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TempFiles {
  private static final File ourCurrentTempDir = new File(System.getProperty("java.io.tmpdir"));
  private final File myCurrentTempDir;

  private static Random ourRandom;

  static {
    //Enforce java.io.File to cache system property value java.io.tmpdir in order
    //to workaround build agent atempt to clean agent's agentTmp dir that is
    //set to java.io.tmpdir system property while build agent is runing. 
    try {
      //noinspection ResultOfMethodCallIgnored
      File.createTempFile("magic", "enforce to cache").delete();
    } catch (IOException e) {
      e.printStackTrace();
    }
    ourRandom = new Random();
    ourRandom.setSeed(System.currentTimeMillis());
  }

  private final List<File> myFilesToDelete = new ArrayList<File>();

  public TempFiles() {
    myCurrentTempDir = ourCurrentTempDir;
    if (!myCurrentTempDir.isDirectory()) {

      throw new IllegalStateException("Temp directory is not a directory, was deleted by some process: " + myCurrentTempDir.getAbsolutePath());
    }
  }

  private File doCreateTempDir(@Nullable String prefix, @Nullable String suffix) throws IOException {
    prefix = prefix == null ? "" : prefix;
    suffix = suffix == null ? ".tmp" : suffix;

    do {
      int count = ourRandom.nextInt();
      final File f = new File(myCurrentTempDir, prefix + count + suffix);
      if (!f.exists() && f.mkdirs()) {
        return f.getCanonicalFile();
      }
    } while (true);
    
  }
  private File doCreateTempFile(String prefix, @Nullable String suffix) throws IOException {
    final File file = doCreateTempDir(prefix, suffix);
    file.delete();
    file.createNewFile();
    return file;
  }

  public final File createTempFile() throws IOException {
    File tempFile = doCreateTempFile("test", null);
    registerAsTempFile(tempFile);
    return tempFile;
  }

  public void registerAsTempFile(final File tempFile) {
    myFilesToDelete.add(tempFile);
  }

  public final File createTempFile(int size) throws IOException {
    File tempFile = createTempFile();
    int bufLen = Math.min(8 * 1024, size);
    if (bufLen == 0) return tempFile;
    FileOutputStream fos = new FileOutputStream(tempFile);
    try {
      byte[] buf = new byte[bufLen];
      for (int i=0; i < buf.length; i++) {
        buf[i] = (byte)Math.round(Math.random()*128);
      }

      int numWritten = 0;
      for (int i=0; i<size / buf.length; i++) {
        fos.write(buf);
        numWritten += buf.length;
      }

      if (size > numWritten) {
        fos.write(buf, 0, size - numWritten);
      }
    } finally {
      fos.close();
    }

    return tempFile;
  }

  /**
   * Returns a File object for created temp directory.
   * Also stores the value into this object accessed with {@link #getCurrentTempDir()}
   *
   * @return a File object for created temp directory
   * @throws IOException if directory creation fails.
   */
  public final File createTempDir() throws IOException {
    File f = doCreateTempDir("test", "");
    registerAsTempFile(f);
    return f;
  }

  /**
   * Returns the current directory used by the test or null if no test is running or no directory is created yet.
   *
   * @return see above
   */
  @Nullable
  public File getCurrentTempDir() {
    return myCurrentTempDir;
  }

  public void cleanup() {
    for (File file : myFilesToDelete) {
      if (file.exists()) {
        delete(file);
      }
    }
    myFilesToDelete.clear();
  }


  public void delete(@NotNull final File file) {
    if (!file.exists()) return;
    if (file.isDirectory()) {
      final File[] files = file.listFiles();
      if (files != null) {
        for (File child : files) {
          delete(child);
        }
      }
    }

    for(int i =0; i < 10; i++) {
      //noinspection ResultOfMethodCallIgnored
      file.delete();
      if (!file.exists()) break;
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        //NOP
      }
    }
  }
}
