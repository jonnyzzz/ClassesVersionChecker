/*
 * Copyright 2000-2011 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jetbrains.buildServer.tools.rules;

import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.StringTokenizer;

/**
* @author Eugene Petrenko (eugene.petrenko@gmail.com)
*         Date: 08.11.11 15:41
*/
public class PathRule {
  private static final String DELIMITERS = "/!";
  private final String myPath;
  private boolean myIsVisited;

  public PathRule(@NotNull final String path) {
    myPath = path;
  }

  @NotNull
  public String getPath() {
    return myPath;
  }

  public boolean isVisited() {
    return myIsVisited;
  }

  public void setVisited() {
    myIsVisited = true;
  }

  public boolean accept(String fileName) {
    StringTokenizer fileNameTokens = new StringTokenizer(fileName, DELIMITERS, true);
    StringTokenizer myPathTokens = new StringTokenizer(myPath, DELIMITERS, true);
    while (myPathTokens.hasMoreTokens()) {
      if (!fileNameTokens.hasMoreTokens()) return false;

      String myPathToken = myPathTokens.nextToken();
      String fileNameToken = fileNameTokens.nextToken();

      if (myPathToken.equals("*")) {
        continue;
      }

      if (!myPathTokens.hasMoreTokens()) {
        //last token in myPath - can be substring of remaining fileName
        return fileNameToken.startsWith(myPathToken);
      }
      if (!fileNameToken.equals(myPathToken)) return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "PathRule{" +
            "myPath='" + myPath + '\'' +
            ", myIsVisited=" + myIsVisited +
            '}';
  }

  final static Comparator<PathRule> MATCH_ORDER = new Comparator<PathRule>() {
    public int compare(PathRule o1, PathRule o2) {
      StringTokenizer p1Parts = new StringTokenizer(o1.getPath(), DELIMITERS, true);
      StringTokenizer p2Parts = new StringTokenizer(o2.getPath(), DELIMITERS, true);
      while (p1Parts.hasMoreTokens() || p2Parts.hasMoreTokens()) {
        if (!p1Parts.hasMoreTokens()) return 1;
        if (!p2Parts.hasMoreTokens()) return -1;

        String p1Token = p1Parts.nextToken();
        String p2Token = p2Parts.nextToken();

        if (p1Token.equals(p2Token)) continue;

        if (p1Token.equals("*")) return 1;
        if (p2Token.equals("*")) return -1;

        if (p1Token.contains(p2Token)) return -1;
        if (p2Token.contains(p1Token)) return 1;

        return p1Token.compareTo(p2Token);
      }
      return 0;
    }
  };

}
