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
