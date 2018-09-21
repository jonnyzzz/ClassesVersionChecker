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

package jetbrains.buildServer.tools.test;

import jetbrains.buildServer.tools.ScanFile;
import jetbrains.buildServer.tools.rules.PathRule;
import jetbrains.buildServer.tools.rules.PathRules;
import org.jetbrains.annotations.NotNull;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 08.11.11 17:02
 */
public class PathRulesTest {
  @Test
  public void testMathLonger() {
    PathRules<PathRule> r = new PathRules<PathRule>(Arrays.asList(new PathRule("a"), new PathRule("a/aa")), Collections.<PathRule>emptyList());
    PathRule rule = r.findRule(mockFile("a/aa/aaabbbaa"));

    Assert.assertNotNull(rule);
    Assert.assertNotNull(rule.getPath().equals("a/aa"));
  }

  @Test
  public void testMathLonger2() {
    PathRules<PathRule> r = new PathRules<PathRule>(
            Arrays.asList(new PathRule("a"),
                    new PathRule("a/aa"),
                    new PathRule("a/aa/aaaa"),
                    new PathRule(""),
                    new PathRule("a/a/a/a/a/a/a/a/a")
                    ), Collections.<PathRule>emptyList());
    PathRule rule = r.findRule(mockFile("a/aa/aaabbbaa"));

    Assert.assertNotNull(rule);
    Assert.assertNotNull(rule.getPath().equals("a/aa"));
  }

  @Test
  public void testMathLonger_middle() {
    PathRules<PathRule> r = new PathRules<PathRule>(
            Arrays.asList(new PathRule("a"),
                    new PathRule("a/aa"),
                    new PathRule("a/aa/aaa"),
                    new PathRule(""),
                    new PathRule("a/aa/aaab"),
                    new PathRule("a/a/a/a/a/a/a/a/a")
                    ), Collections.<PathRule>emptyList());
    PathRule rule = r.findRule(mockFile("a/aa/aaabbbaa"));

    Assert.assertNotNull(rule);
    Assert.assertNotNull(rule.getPath().equals("a/aa/aaab"));
  }

  private ScanFile mockFile(final String s) {
    return new ScanFile() {
      @NotNull
      public InputStream openStream() throws IOException {
        return null;
      }

      @NotNull
      public String getName() {

        return s;
      }

      public boolean isFile() {
        return false;
      }

      public boolean isPhysical() {
        return false;
      }
    };
  }

}
