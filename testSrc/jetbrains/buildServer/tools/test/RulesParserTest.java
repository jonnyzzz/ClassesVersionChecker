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

import jetbrains.buildServer.tools.java.JavaVersion;
import jetbrains.buildServer.tools.rules.PathSettings;
import jetbrains.buildServer.tools.rules.StaticCheckRule;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Iterator;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 11.11.11 15:05
 */
public class RulesParserTest extends RulesBaseTestCase {

  @Test
  public void testResolveRules_include() throws IOException {
    final String configText = "1.5 => fff";
    final PathSettings s = parseConfig(configText);

    Assert.assertTrue(s.getVersions().getExcludes().isEmpty());
    Assert.assertEquals(s.getVersions().getIncludes().size(), 1);
    Assert.assertEquals(s.getVersions().getIncludes().iterator().next().getVersion(), JavaVersion.Java_1_5);

    Assert.assertEquals(s.getVersions().getIncludes().iterator().next().getPath(), "fff");
  }

  @Test
  public void testResolveRules_include_zip() throws IOException {
    final PathSettings s = parseConfig("1.5 => fff.jar!f/a/d/d/g/r");

    Assert.assertTrue(s.getVersions().getExcludes().isEmpty());
    Assert.assertEquals(s.getVersions().getIncludes().size(), 1);
    Assert.assertEquals(s.getVersions().getIncludes().iterator().next().getVersion(), JavaVersion.Java_1_5);

    Assert.assertEquals(s.getVersions().getIncludes().iterator().next().getPath(), "fff.jar!f/a/d/d/g/r");
  }

  @Test
  public void testResolveRules_include_zip_normalize() throws IOException {
    final PathSettings s = parseConfig("1.5 => p\\q//f\\//\\\\/fff.jar!f/a/\\d/d\\g/r");

    Assert.assertTrue(s.getVersions().getExcludes().isEmpty());
    Assert.assertEquals(s.getVersions().getIncludes().size(), 1);
    Assert.assertEquals(s.getVersions().getIncludes().iterator().next().getVersion(), JavaVersion.Java_1_5);
    Assert.assertEquals(s.getVersions().getIncludes().iterator().next().getPath(), "p/q/f/fff.jar!f/a/d/d/g/r");
  }

  @Test
  public void testResolveRules_exclude() throws IOException {
    final PathSettings s = parseConfig("- => fff.jar!f/a/d/d/g/r");

    Assert.assertTrue(s.getVersions().getIncludes().isEmpty());
    Assert.assertEquals(s.getVersions().getExcludes().size(), 1);

    Assert.assertEquals(s.getVersions().getExcludes().iterator().next().getPath(), "fff.jar!f/a/d/d/g/r");
  }

  @Test
  public void testResolveRules_exclude_normalize() throws IOException {
    final PathSettings s = parseConfig("- => fff.jar!f/a\\d//d/\\g/r");

    Assert.assertTrue(s.getVersions().getIncludes().isEmpty());
    Assert.assertEquals(s.getVersions().getExcludes().size(), 1);

    Assert.assertEquals(s.getVersions().getExcludes().iterator().next().getPath(), "fff.jar!f/a/d/d/g/r");
  }

  @Test
  public void testStaticClass_oneRule() throws IOException {
    PathSettings s1 = parseConfig("\n" +
            "allow static class org.apache.log4j.Logger\n" +
            "\n" +
            "check static => webapps/\n" +
            "check static => agent/");

    final Iterator<? extends StaticCheckRule> it = s1.getStaticChecks().getIncludes().iterator();

    Assert.assertEquals(s1.getStaticChecks().getIncludes().size(), 2);
    Assert.assertEquals(it.next().getPath(), "agent/");
    Assert.assertEquals(it.next().getPath(), "webapps/");
  }

  @Test
  public void testStaticClass_empty_rule() throws IOException {
    PathSettings s1 = parseConfig("\n" +
            "check static => \n");

    final Iterator<? extends StaticCheckRule> it = s1.getStaticChecks().getIncludes().iterator();

    Assert.assertEquals(s1.getStaticChecks().getIncludes().size(), 1);
    Assert.assertEquals(it.next().getPath(), "");
  }
}
