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

import jetbrains.buildServer.tools.ScanFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
* @author Eugene Petrenko (eugene.petrenko@gmail.com)
*         Date: 08.11.11 15:42
*/
public class PathRules<T extends PathRule> {
  private final List<T> myRules;

  public PathRules(@NotNull final Collection<T> rules) {
    myRules = new ArrayList<T>(rules);
    Collections.sort(myRules, CMP);
  }

  @Nullable
  public T findRule(@NotNull final ScanFile file) {
    final String name = file.getName();
    for (T rule : myRules) {
      if (name.startsWith(rule.getPath())) {
        rule.setVisited();
        return rule;
      }
    }
    return null;
  }

  private final static Comparator<PathRule> CMP = new Comparator<PathRule>() {
    public int compare(PathRule o1, PathRule o2) {
      final String p1 = o1.getPath();
      final String p2 = o2.getPath();

      final int s1 = p1.length();
      final int s2 = p2.length();

      if (s1 < s2) return 1;
      if (s1 > s2) return -1;
      return p1.compareTo(p2);
    }
  };

}
