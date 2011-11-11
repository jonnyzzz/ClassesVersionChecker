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
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 11.11.11 15:33
 */
public class RulesMatcherTest extends RulesBaseTestCase {

  @Test
  public void testMatchesFile() throws IOException {
    final PathSettings s = parseConfig("1.7 => aaa/bbb.jar\r\n-=>aaa/b");

    Assert.assertEquals(s.getFileCheckMode(mockFile("aaa/bbb.jar")), JavaVersion.Java_1_7);
    Assert.assertEquals(s.getFileCheckMode(mockFile("aaa/bbb.jar!aaa")), JavaVersion.Java_1_7);
    Assert.assertEquals(s.getFileCheckMode(mockFile("aaa/bbb.jar!aaa/bbb")), JavaVersion.Java_1_7);
    Assert.assertEquals(s.getFileCheckMode(mockFile("aaa/bbb.jar!aaa/bbb.jar!zzz")), JavaVersion.Java_1_7);
    Assert.assertEquals(s.getFileCheckMode(mockFile("aaa")), null);

    Assert.assertTrue(s.isPathExcluded(mockFile("aaa/b")));
    Assert.assertTrue(s.isPathExcluded(mockFile("aaa/bb")));
    Assert.assertTrue(s.isPathExcluded(mockFile("aaa/bbq")));

    //excludes wins
    Assert.assertTrue(s.isPathExcluded(mockFile("aaa/bbb.jar")));
    Assert.assertTrue(s.isPathExcluded(mockFile("aaa/bbb.jar!sss")));
  }

  @Test
  public void testLongestRuleWins() throws IOException {
    final PathSettings s = parseConfig("1.7 => aaa/bbb/ccc/ddd.jar\r\n1.2=>");

    Assert.assertEquals(s.getFileCheckMode(mockFile("aaa")), JavaVersion.Java_1_2);
    Assert.assertEquals(s.getFileCheckMode(mockFile("aaa/bbb.jar")), JavaVersion.Java_1_2);
    Assert.assertEquals(s.getFileCheckMode(mockFile("aaa/bbb.jar!aaa")), JavaVersion.Java_1_2);
    Assert.assertEquals(s.getFileCheckMode(mockFile("aaa/bbb/bbb.jar!aaa/bbb")), JavaVersion.Java_1_2);
    Assert.assertEquals(s.getFileCheckMode(mockFile("aaa/bbb/ccc/bbb.jar!aaa/bbb")), JavaVersion.Java_1_2);
    Assert.assertEquals(s.getFileCheckMode(mockFile("aaa/bbb/ccc/ddd/bbb.jar!aaa/bbb")), JavaVersion.Java_1_2);
    Assert.assertEquals(s.getFileCheckMode(mockFile("aaa/bbb.jar!aaa/bbb.jar!zzz")), JavaVersion.Java_1_2);

    Assert.assertEquals(s.getFileCheckMode(mockFile("aaa/bbb/ccc/ddd.jar!aaa/bbb.jar!zzz")), JavaVersion.Java_1_7);
    Assert.assertEquals(s.getFileCheckMode(mockFile("aaa/bbb/ccc/ddd.jar")), JavaVersion.Java_1_7);
    Assert.assertEquals(s.getFileCheckMode(mockFile("aaa/bbb/ccc/ddd.jarddd")), JavaVersion.Java_1_7);
    Assert.assertEquals(s.getFileCheckMode(mockFile("aaa/bbb/ccc/ddd.jar!e/d/sd")), JavaVersion.Java_1_7);
  }

}
