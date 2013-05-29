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

import jetbrains.buildServer.tools.java.JavaVersion;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.Scanner;

import static jetbrains.buildServer.tools.rules.PathRules.Builder;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 08.11.11 18:07
 */
public class RulesParser {
  @NotNull
  public static PathSettings parseConfig(@NotNull final File scanHome, @NotNull final Reader rdr) throws IOException {
    final Builder<VersionRule> myVersions = new Builder<VersionRule>();
    final Builder<StaticCheckRule> myStatics = new Builder<StaticCheckRule>();
    final StaticRuleSettings myAllowedStaticClasses = new StaticRuleSettings();

    try {
      final Scanner sc = new Scanner(rdr);
      while(sc.hasNextLine()) {
        String line = sc.nextLine().trim();
        if (line.length() == 0 || line.startsWith(";")) {
          continue;
        }
        line = line.trim();

        if (line.startsWith("allow static class")) {
          String clazz = line.substring("allow static class".length()).trim();
          if (clazz.length() > 0) {
            myAllowedStaticClasses.addRule(clazz);
          }
          continue;
        }

        if (line.startsWith("check static =>")) {
          String path = line.substring("check static =>".length()).trim();
          myStatics.include(new StaticCheckRule(resolvePath(scanHome, path), myAllowedStaticClasses));
          continue;
        }

        if (line.startsWith("- check static =>")) {
          String path = line.substring("- check static =>".length()).trim();
          myStatics.exclude(new PathRule(resolvePath(scanHome, path)));
          continue;
        }

        if (line.startsWith("-")) {
          String path = line.substring(1).trim();
          if (path.startsWith("=>")) {
            path = path.substring(2).trim();
          } else {
            throw new IOException("Failed to parse - rule: " + line);
          }
          myVersions.exclude(new PathRule(resolvePath(scanHome, path)));
          continue;
        }

        boolean parsed = false;
        for (JavaVersion v : JavaVersion.values()) {
          final String prefix = v.getShortName();
          if (!line.startsWith(prefix)) continue;

          String part = line.substring(prefix.length()).trim();

          if (part.startsWith("=>")) {
            part = part.substring(2).trim();
          } else {
            throw new IOException("Failed to parse java rule: " + line);
          }

          myVersions.include(new VersionRule(resolvePath(scanHome, part), v));
          parsed = true;
          break;
        }
        if (parsed) continue;


        throw new IOException("Unexpected string: '" + line + "' in config");
      }
    } finally {
      rdr.close();
    }

    return new PathSettings(myVersions.build(), myStatics.build());
  }

  private static String resolvePath(@NotNull final File home, @NotNull String path) throws IOException {
    path = path.trim().replaceAll("[\\\\/]+", "/");
    if (path.length() == 0) return home.getPath();
    int q = path.indexOf('!');

    if (q > 0) {
      return resolvePath(home, path.substring(0, q)) + (path.substring(q));
    }

    return new File(home, path).getCanonicalPath();
  }

}
