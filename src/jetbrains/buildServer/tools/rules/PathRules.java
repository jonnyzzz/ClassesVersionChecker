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
  private final SimplePathRules<T> myIncludes;
  private final SimplePathRules<PathRule> myExcludes;

  public PathRules(@NotNull final Collection<? extends T> includes,
                   @NotNull final Collection<? extends PathRule> excludes) {
    myIncludes = new SimplePathRules<T>(includes);
    myExcludes = new SimplePathRules<PathRule>(excludes);
  }

  @NotNull
  public Collection<? extends T> getIncludes() {
    return myIncludes.getRules();
  }

  @NotNull
  public Collection<? extends PathRule> getExcludes() {
    return myExcludes.getRules();
  }

  @Nullable
  public T findRule(@NotNull final ScanFile file) {
    if (isPathExcluded(file)) return null;
    return myIncludes.findRule(file);
  }

  public boolean isPathExcluded(@NotNull ScanFile file) {
    return myExcludes.findRule(file) != null;
  }

  public static class Builder<T extends PathRule> {
    private final Collection<T> myIncludes = new ArrayList<T>();
    private final Collection<PathRule> myExcludes = new ArrayList<PathRule>();

    public void include(@NotNull final T rule) {
      myIncludes.add(rule);
    }

    public void exclude(@NotNull final PathRule rule) {
      myExcludes.add(rule);
    }

    @NotNull
    public PathRules<T> build() {
      return new PathRules<T>(myIncludes, myExcludes);
    }

  }
}
