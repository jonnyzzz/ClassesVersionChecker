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
import jetbrains.buildServer.tools.rules.PathSettings;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.Test;

import java.io.*;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 11.11.11 16:03
 */
public class FilesProcessorTest extends RulesBaseTestCase {
  @Test
  public void test_class_in_zip4_1_7() throws IOException {
    writeAllVersionClassesToZip4();
    runTest("1.7 =>");
  }

  @Test
  public void test_class_in_zip4_1_6() throws IOException {
    expectCheckError("foo.zip!a/zipInZip.jar!q/zip3Zip.jar!z/zip4Zip.jar!51.class");
    writeAllVersionClassesToZip4();
    runTest("1.6 =>");
  }

  @Test
  public void test_class_in_zip4_1_5() throws IOException {
    expectCheckError("foo.zip!a/zipInZip.jar!q/zip3Zip.jar!z/zip4Zip.jar!51.class");
    expectCheckError("foo.zip!a/zipInZip.jar!q/zip3Zip.jar!z/zip4Zip.jar!50.class");

    writeAllVersionClassesToZip4();
    runTest("1.5 =>");
  }

  @Test
  public void test_class_in_zip4_1_4() throws IOException {
    expectCheckError("foo.zip!a/zipInZip.jar!q/zip3Zip.jar!z/zip4Zip.jar!51.class");
    expectCheckError("foo.zip!a/zipInZip.jar!q/zip3Zip.jar!z/zip4Zip.jar!50.class");
    expectCheckError("foo.zip!a/zipInZip.jar!q/zip3Zip.jar!z/zip4Zip.jar!49.class");

    writeAllVersionClassesToZip4();
    runTest("1.4 =>");
  }

  @Test
  public void test_class_in_zip4_1_3() throws IOException {
    expectCheckError("foo.zip!a/zipInZip.jar!q/zip3Zip.jar!z/zip4Zip.jar!51.class");
    expectCheckError("foo.zip!a/zipInZip.jar!q/zip3Zip.jar!z/zip4Zip.jar!50.class");
    expectCheckError("foo.zip!a/zipInZip.jar!q/zip3Zip.jar!z/zip4Zip.jar!49.class");
    expectCheckError("foo.zip!a/zipInZip.jar!q/zip3Zip.jar!z/zip4Zip.jar!48.class");

    writeAllVersionClassesToZip4();
    runTest("1.3 =>");
  }


  @Test
  public void test_class_in_zip_1_7() throws IOException {
    writeAllVersionClassesToZip();
    runTest("1.7 =>");
  }

  @Test
  public void test_class_in_zip_1_6() throws IOException {
    expectCheckError("foo.zip!51.class");
    writeAllVersionClassesToZip();
    runTest("1.6 =>");
  }

  @Test
  public void test_class_in_zip_1_5() throws IOException {
    expectCheckError("foo.zip!51.class");
    expectCheckError("foo.zip!50.class");

    writeAllVersionClassesToZip();
    runTest("1.5 =>");
  }

  @Test
  public void test_class_in_zip_1_4() throws IOException {
    expectCheckError("foo.zip!51.class");
    expectCheckError("foo.zip!50.class");
    expectCheckError("foo.zip!49.class");

    writeAllVersionClassesToZip();
    runTest("1.4 =>");
  }

  @Test
  public void test_class_in_zip_1_3() throws IOException {
    expectCheckError("foo.zip!51.class");
    expectCheckError("foo.zip!50.class");
    expectCheckError("foo.zip!49.class");
    expectCheckError("foo.zip!48.class");

    writeAllVersionClassesToZip();
    runTest("1.3 =>");
  }

  private void writeAllVersionClassesToZip() throws IOException {
    saveFile("foo.zip",
            zipStream(
                    classBytes("51.class", 51), //1.7
                    classBytes("50.class", 50), //1.6
                    classBytes("49.class", 49), //1.5
                    classBytes("48.class", 48), //1.4
                    classBytes("47.class", 47), //1.3
                    classBytes("46.class", 46), //1.2
                    file("someOther", "Kino Rulezz".getBytes())
            )
    );
  }

  private void writeAllVersionClassesToZip4() throws IOException {
    saveFile("foo.zip",
            zipStream(
            zipStream("a/zipInZip.jar",
            zipStream("q/zip3Zip.jar",
            zipStream("z/zip4Zip.jar",
                    classBytes("51.class", 51), //1.7
                    classBytes("50.class", 50), //1.6
                    classBytes("49.class", 49), //1.5
                    classBytes("48.class", 48), //1.4
                    classBytes("47.class", 47), //1.3
                    classBytes("46.class", 46), //1.2
                    file("someOther", "Kino Rulezz".getBytes())
            ))))
    );
  }


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

  @Test
  public void test_not_found_file_rule() throws IOException {
    expectRuleNotVisited("some/useful/file.txt");
    runTest("1.7 => some/useful/file.txt");
  }

  @Test
  public void test_not_found_file_rule2() throws IOException {
    expectRuleNotVisited("some/useful.zip!file.txt");
    runTest("1.7 => some/useful.zip!file.txt");
  }

  @Test
  public void test_not_found_file_rule3() throws IOException {
    saveFile("som/useful.zip", zipStream(file("aaa.txt", "this is aaa".getBytes()), file("aaa2.txt", "this is aaa".getBytes())));
    expectRuleNotVisited("some/useful.zip!file.txt");
    runTest("1.7 => some/useful.zip!file.txt");
  }

  private void writeAllVersionClasses() throws IOException {
    saveFile("51.class", classBytes(51)); //1.7
    saveFile("50.class", classBytes(50)); //1.6
    saveFile("49.class", classBytes(49)); //1.5
    saveFile("48.class", classBytes(48)); //1.4
    saveFile("47.class", classBytes(47)); //1.3
    saveFile("46.class", classBytes(46)); //1.2
  }

  protected void runTest(@NotNull final String config) throws IOException {
    final PathSettings rules = parseConfig(config);
    FilesProcessor.processFiles(myHome, new JavaCheckSettings(rules), rep);
    rules.assertVisited(rep);
  }

}
