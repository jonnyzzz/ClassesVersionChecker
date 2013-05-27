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

import jetbrains.buildServer.tools.ErrorReporting;
import jetbrains.buildServer.tools.ScanFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
* @author Eugene Petrenko (eugene.petrenko@gmail.com)
*         Date: 08.11.11 18:00
*/
public class PathSettings {
  private final PathRules<PathRule> myExcludes;
  private final PathRules<VersionRule> myVersions;
  private final PathRules<StaticCheckRule> myStatics;

  public PathSettings(@NotNull final Collection<PathRule> excludes,
                      @NotNull final Collection<VersionRule> versions,
                      @NotNull final Collection<StaticCheckRule> staticChecks) {
    myExcludes = new PathRules<PathRule>(excludes);
    myVersions = new PathRules<VersionRule>(versions);
    myStatics = new PathRules<StaticCheckRule>(staticChecks);
  }

  @NotNull
  public Collection<PathRule> getExcludes() {
    return myExcludes.getRules();
  }

  @NotNull
  public Collection<VersionRule> getVersions() {
    return myVersions.getRules();
  }

  @NotNull
  public Collection<StaticCheckRule> getStaticChecks() {
    return myStatics.getRules();
  }

  public boolean isPathExcluded(@NotNull ScanFile file) {
    return myExcludes.findRule(file) != null;
  }

  @NotNull
  public Collection<? extends CheckHolder> getFileCheckMode(@NotNull ScanFile file) {
    return notNulls(myVersions.findRule(file), myStatics.findRule(file));
  }

  @NotNull
  private static <T> Collection<? extends T> notNulls(@Nullable T... ts) {
    if (ts == null) return Collections.emptyList();

    final List<T> list = new ArrayList<T>(ts.length);
    for (T t : ts) {
      if (t != null) list.add(t);
    }
    return list;
  }

  public boolean validateRules(@NotNull final PrintStream ps) {
    boolean failed = true;
    for (PathRule exclude : getExcludes()) {
      if (!exclude.getBaseFile().exists()) {
        ps.println("Exclude rule file " + exclude.getPath() + " does not exist");
      }
    }

    for (PathRule exclude : getVersions()) {
      if (!exclude.getBaseFile().exists()) {
        ps.println("Version rule " + exclude.getPath() + " does not match existing file");
        failed = false;
      }
    }

    for (PathRule st : getStaticChecks()) {
      if (!st.getBaseFile().exists()) {
        ps.println("Static check rule " + st.getPath() + " does not match existing file");
        failed = false;
      }
    }
    return failed;
  }


  public void assertVisited(@NotNull final ErrorReporting reporting) {
    for (VersionRule rule : getVersions()) {
      if (!rule.isVisited()) {
        reporting.ruleNotVisited(rule);
      }
    }
    for (StaticCheckRule rule : getStaticChecks()) {
      if (!rule.isVisited()) {
        reporting.ruleNotVisited(rule);
      }
    }
  }


  public void dumpTotalRules(@NotNull final PrintStream ps) {
    ps.println("Excludes: ");
    for (PathRule e : getExcludes()) {
      ps.println("  " + e.getPath());
    }
    ps.println();
    ps.println("Versions to check: ");
    for (VersionRule e : getVersions()) {
      ps.println("  " + e.getVersion() + " => " + e.getPath());
    }
    ps.println();
    ps.println("Static checks to check: ");
    for (StaticCheckRule e : getStaticChecks()) {
      ps.println("  static check " + " => " + e.getPath());
    }
    ps.println();
    ps.println();
  }

}
