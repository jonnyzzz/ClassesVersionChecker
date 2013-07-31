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

import jetbrains.buildServer.tools.fs.Naming;
import jetbrains.buildServer.tools.java.JavaVersion;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static jetbrains.buildServer.tools.rules.PathRules.Builder;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 08.11.11 18:07
 */
public class RulesParser {
  private final Builder<VersionRule> myVersions = new Builder<VersionRule>();
  private final Builder<StaticCheckRule> myStatics = new Builder<StaticCheckRule>();
  private final StaticRuleSettings myAllowedStaticClasses = new StaticRuleSettings();
  private final List<Parser> myParsers;
  private final Queue<File> myConfigs = new ArrayDeque<File>();

  public RulesParser() {
    final List<Parser> parsers = new ArrayList<Parser>();

    parsers.add(new RegexParser(Pattern.compile("include\\s+(.+)\\s*")) {
      @Override
      protected boolean parse(@NotNull Matcher matcher) throws IOException {
        if (myConfigs.isEmpty()) return false;
        parseConfig(new File(myConfigs.peek().getParentFile(), matcher.group(1).trim()));
        return true;
      }
    });

    parsers.add(
            new RegexParser(Pattern.compile("allow\\s+static\\s+class\\s+(.+)\\s*")) {
              @Override
              protected boolean parse(@NotNull Matcher matcher) {
                final String clazz = matcher.group(1).trim();
                myAllowedStaticClasses.addRule(clazz);
                return true;
              }
            });

    parsers.add(new RegexParser(Pattern.compile("check\\s+static\\s*=>\\s*(.*)\\s*")) {
      protected boolean parse(@NotNull Matcher line) throws IOException {
        String path = line.group(1).trim();
        myStatics.include(new StaticCheckRule(resolvePath(path), myAllowedStaticClasses));
        return true;
      }
    });
    parsers.add(new RegexParser(Pattern.compile("-\\s*check\\s+static\\s*=\\s*>\\s*(.+)\\s*")) {
      protected boolean parse(@NotNull Matcher line) throws IOException {
        String path = line.group(1).trim();
        myStatics.exclude(new PathRule(resolvePath(path)));
        return true;
      }
    });

    for (final JavaVersion v : JavaVersion.values()) {
      parsers.add(new Parser() {
        public boolean parse(@NotNull String line) throws IOException {
          final String prefix = v.getShortName();
          if (!line.startsWith(prefix)) return false;

          String part = line.substring(prefix.length()).trim();
          if (part.startsWith("=>")) {
            part = part.substring(2).trim();
          } else {
            throw new IOException("Failed to parse java rule: " + line);
          }

          myVersions.include(new VersionRule(resolvePath(part), v));
          return true;
        }
      });
    }
    parsers.add(new Parser() {
      public boolean parse(@NotNull String line) throws IOException {
        if (!line.startsWith("-")) return false;

        String path = line.substring(1).trim();
        if (path.startsWith("=>")) {
          path = path.substring(2).trim();
        } else {
          throw new IOException("Failed to parse - rule: " + line);
        }
        myVersions.exclude(new PathRule(resolvePath(path)));
        return true;
      }
    });

    myParsers = Collections.unmodifiableList(parsers);
  }

  @NotNull
  public PathSettings build() {
    return new PathSettings(myVersions.build(), myStatics.build());
  }

  @NotNull
  public RulesParser parseConfig(@NotNull final File config) {
    Reader rdr = null;
    try {
      myConfigs.add(config);
      rdr = new InputStreamReader(new FileInputStream(config), "utf-8");
      return parseConfig(rdr);
    } catch(IOException e) {
      System.err.println("Failed to parse settings from: " + config + ". " + e.getMessage());
      throw new RuntimeException("No settings found in: " + config);
    } finally {
      myConfigs.poll();
      try {
        if (rdr != null) rdr.close();
      } catch (IOException e) {
        //NOP
      }
    }
  }

  @NotNull
  public RulesParser parseConfig(@NotNull final Reader rdr) throws IOException {
    try {
      final Scanner sc = new Scanner(rdr);
      while (sc.hasNextLine()) {
        String line = sc.nextLine().trim();
        if (line.length() == 0 || line.startsWith(";")) {
          continue;
        }
        line = line.trim();

        boolean handled = false;
        for (Parser parser : myParsers) {
          if (parser.parse(line)) {
            handled = true;
            break;
          }
        }

        if (handled) continue;
        throw new IOException("Unexpected string: '" + line + "' in config");
      }
    } finally {
      rdr.close();
    }
    return this;
  }

  private static String resolvePath(@NotNull String path) throws IOException {
    return Naming.normalizePath(path);
  }

  private interface Parser {
    boolean parse(@NotNull String line) throws IOException;
  }

  private abstract static class RegexParser implements Parser {
    private final Pattern myRegex;

    protected RegexParser(@NotNull Pattern regex) {
      myRegex = regex;
    }

    public boolean parse(@NotNull final String line) throws IOException {
      final Matcher matcher = myRegex.matcher(line);
      return matcher.matches() && parse(matcher);
    }

    protected abstract boolean parse(@NotNull Matcher matcher) throws IOException;
  }
}
