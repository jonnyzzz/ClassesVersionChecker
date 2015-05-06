/*
 * Copyright 2000-2015 JetBrains s.r.o.
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

import jetbrains.buildServer.tools.rules.PathRule;
import jetbrains.buildServer.tools.rules.SimplePathRules;
import org.testng.annotations.Test;

import java.util.Arrays;

/**
 * Created 06.05.2015 12:18
 *
 * @author Eugene Petrenko (eugene.petrenko@jetbrains.com)
 */
public class SimplePathRulesTest extends RulesBaseTestCase {
  @Test
  public void pathRulesShouldBeOrdered() {
    SimplePathRules<PathRule> rules = new SimplePathRules<PathRule>(Arrays.asList(
            new PathRule("a"),
            new PathRule("aa"),
            new PathRule("b"),
            new PathRule("b/c/d"),
            new PathRule("a/c/d")
            ));


    assertRule(rules.findRule(mockFile("z")), null);

    assertRule(rules.findRule(mockFile("a")), "a");
    assertRule(rules.findRule(mockFile("aa")), "aa");
    assertRule(rules.findRule(mockFile("aaa")), "aa");
    assertRule(rules.findRule(mockFile("a/")), "a");
    assertRule(rules.findRule(mockFile("a/c")), "a");
    assertRule(rules.findRule(mockFile("a/c/e")), "a");
    assertRule(rules.findRule(mockFile("a/c/d")), "a/c/d");
    assertRule(rules.findRule(mockFile("a/c/dddd")), "a/c/d");

    assertRule(rules.findRule(mockFile("b")), "b");
    assertRule(rules.findRule(mockFile("b/")), "b");
    assertRule(rules.findRule(mockFile("b/c")), "b");
    assertRule(rules.findRule(mockFile("b/c/d")), "b/c/d");
    assertRule(rules.findRule(mockFile("b/c/d/")), "b/c/d");
  }
}
