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
public class SimplePathRules<T extends PathRule> {
  private final Set<T> myRules;

  public SimplePathRules(@NotNull final Collection<? extends T> rules) {
    final TreeSet<T> ts = new TreeSet<T>(MATCH_ORDER);
    ts.addAll(rules);
    myRules = Collections.unmodifiableSet(ts);
  }

  @NotNull
  public Collection<? extends T> getRules() {
    Set<T> set = new TreeSet<T>(DUMP_ORDER);
    set.addAll(myRules);
    return set;
  }

  @Nullable
  public T findRule(@NotNull final ScanFile file) {
    final String name = file.getName();
    for (T rule : myRules) {
      if (rule.accept(name)) {
        rule.setVisited();
        return rule;
      }
    }
    return null;
  }

  private final static Comparator<PathRule> MATCH_ORDER = new Comparator<PathRule>() {
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

  private final static Comparator<PathRule> DUMP_ORDER = new Comparator<PathRule>() {
    public int compare(PathRule o1, PathRule o2) {
      final String p1 = o1.getPath();
      final String p2 = o2.getPath();

      return p1.compareTo(p2);
    }
  };
}
