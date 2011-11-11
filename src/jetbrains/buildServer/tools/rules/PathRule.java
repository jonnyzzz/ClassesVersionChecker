package jetbrains.buildServer.tools.rules;

import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
* @author Eugene Petrenko (eugene.petrenko@gmail.com)
*         Date: 08.11.11 15:41
*/
public class PathRule {
  private final String myPath;
  private boolean myIsVisited;

  public PathRule(String path) {
    myPath = path;
  }

  @NotNull
  public String getPath() {
    return myPath;
  }

  @NotNull
  public File getBaseFile() {
    int idx = myPath.indexOf('!');
    if (idx < 0) return new File(myPath);
    return new File(myPath.substring(0, idx));
  }

  public boolean isVisited() {
    return myIsVisited;
  }

  public void setVisited() {
    myIsVisited = true;
  }
}
