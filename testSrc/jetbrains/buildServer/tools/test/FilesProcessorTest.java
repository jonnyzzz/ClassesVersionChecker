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

import jetbrains.buildServer.tools.FilesProcessor;
import jetbrains.buildServer.tools.java.JavaCheckSettings;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.Test;

import java.io.*;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 11.11.11 16:03
 */
public class FilesProcessorTest extends RulesBaseTestCase {

  @Test
  public void test_all_1_7() throws IOException {
    writeAllVersionClasses();
    runTest("1.7 =>");
  }

  @Test
  public void test_all_1_6() throws IOException {
    expectCheckError("51.class");
    writeAllVersionClasses();
    runTest("1.6 =>");
  }

  @Test
  public void test_all_1_5() throws IOException {
    expectCheckError("51.class");
    expectCheckError("50.class");
    writeAllVersionClasses();
    runTest("1.5 =>");
  }

  @Test
  public void test_all_1_4() throws IOException {
    expectCheckError("51.class");
    expectCheckError("50.class");
    expectCheckError("49.class");

    writeAllVersionClasses();
    runTest("1.4 =>");
  }

  @Test
  public void test_all_1_3() throws IOException {
    expectCheckError("51.class");
    expectCheckError("50.class");
    expectCheckError("49.class");
    expectCheckError("48.class");

    writeAllVersionClasses();
    runTest("1.3 =>");
  }

  @Test
  public void test_all_1_2() throws IOException {
    expectCheckError("51.class");
    expectCheckError("50.class");
    expectCheckError("49.class");
    expectCheckError("48.class");
    expectCheckError("47.class");

    writeAllVersionClasses();
    runTest("1.2 =>");
  }

  @Test
  public void test_broken_class() throws IOException {
    saveFile("foo.class", "this is not a right class".getBytes());
    expectGenericError("foo.class");
    runTest("1.7 => ");
  }

  private void writeAllVersionClasses() throws IOException {
    saveFile("51.class", classBytes(51)); //1.7
    saveFile("50.class", classBytes(50)); //1.6
    saveFile("49.class", classBytes(49)); //1.5
    saveFile("48.class", classBytes(48)); //1.4
    saveFile("47.class", classBytes(47)); //1.3
    saveFile("46.class", classBytes(46)); //1.2
  }

  private void runTest(@NotNull final String config) throws IOException {
    FilesProcessor.processFiles(myHome, new JavaCheckSettings(parseConfig(config)), rep);
  }

}
