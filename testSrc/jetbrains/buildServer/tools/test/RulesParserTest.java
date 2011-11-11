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

import jetbrains.buildServer.tools.BaseTestCase;
import jetbrains.buildServer.tools.java.JavaVersion;
import jetbrains.buildServer.tools.rules.PathSettings;
import jetbrains.buildServer.tools.rules.RulesParser;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 11.11.11 15:05
 */
public class RulesParserTest extends BaseTestCase {

  @Test
  public void testResolveRules_include() throws IOException {
    final File home = createTempDir();

    final PathSettings s = RulesParser.parseConfig(home, new StringReader("1.5 => fff"));

    Assert.assertTrue(s.getExcludes().isEmpty());
    Assert.assertEquals(s.getVersions().size(), 1);
    Assert.assertEquals(s.getVersions().iterator().next().getVersion(), JavaVersion.Java_1_5);

    final File base = new File(home, "fff");
    Assert.assertEquals(s.getVersions().iterator().next().getPath(), base.getPath());
    Assert.assertEquals(s.getVersions().iterator().next().getBaseFile(), base);
  }

  @Test
  public void testResolveRules_include_zip() throws IOException {
    final File home = createTempDir();

    final PathSettings s = RulesParser.parseConfig(home, new StringReader("1.5 => fff.jar!f/a/d/d/g/r"));

    Assert.assertTrue(s.getExcludes().isEmpty());
    Assert.assertEquals(s.getVersions().size(), 1);
    Assert.assertEquals(s.getVersions().iterator().next().getVersion(), JavaVersion.Java_1_5);

    final File base = new File(home, "fff.jar");
    Assert.assertEquals(s.getVersions().iterator().next().getPath(), base.getPath() + "!f/a/d/d/g/r");
    Assert.assertEquals(s.getVersions().iterator().next().getBaseFile(), base);
  }

  @Test
  public void testResolveRules_include_zip_normalize() throws IOException {
    final File home = createTempDir();

    final PathSettings s = RulesParser.parseConfig(home, new StringReader("1.5 => p\\q//f\\//\\\\/fff.jar!f/a/\\d/d\\g/r"));

    Assert.assertTrue(s.getExcludes().isEmpty());
    Assert.assertEquals(s.getVersions().size(), 1);
    Assert.assertEquals(s.getVersions().iterator().next().getVersion(), JavaVersion.Java_1_5);

    final File base = new File(home, "p/q/f/fff.jar");
    Assert.assertEquals(s.getVersions().iterator().next().getPath(), base.getPath() + "!f/a/d/d/g/r");
    Assert.assertEquals(s.getVersions().iterator().next().getBaseFile(), base);
  }

  @Test
  public void testResolveRules_exclude() throws IOException {
    final File home = createTempDir();

    final PathSettings s = RulesParser.parseConfig(home, new StringReader("- => fff.jar!f/a/d/d/g/r"));

    Assert.assertTrue(s.getVersions().isEmpty());
    Assert.assertEquals(s.getExcludes().size(), 1);

    final File base = new File(home, "fff.jar");
    Assert.assertEquals(s.getExcludes().iterator().next().getPath(), base.getPath() + "!f/a/d/d/g/r");
    Assert.assertEquals(s.getExcludes().iterator().next().getBaseFile(), base);
  }

  @Test
  public void testResolveRules_exclude_normalize() throws IOException {
    final File home = createTempDir();

    final PathSettings s = RulesParser.parseConfig(home, new StringReader("- => fff.jar!f/a\\d//d/\\g/r"));

    Assert.assertTrue(s.getVersions().isEmpty());
    Assert.assertEquals(s.getExcludes().size(), 1);

    final File base = new File(home, "fff.jar");
    Assert.assertEquals(s.getExcludes().iterator().next().getPath(), base.getPath() + "!f/a/d/d/g/r");
    Assert.assertEquals(s.getExcludes().iterator().next().getBaseFile(), base);
  }
}
