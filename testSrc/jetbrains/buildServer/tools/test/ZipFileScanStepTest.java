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

import jetbrains.buildServer.tools.step.ScanZipStep;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 11.11.11 15:44
 */
public class ZipFileScanStepTest extends RulesBaseTestCase {
  private ScanZipStep myStep = new ScanZipStep();

  @Test
  public void test_push_all_files() throws IOException {
    expectFile("zip.zip!a/a.txt");
    expectFile("zip.zip!b/b.txt");
    expectFile("zip.zip!u/d/f/g.ppp");

    myStep.process(mockFile("zip.zip", zipStream("a/a.txt", "b/b.txt", "u/d/f/g.ppp")), c);

    m.assertIsSatisfied();
  }

  @Test
  public void test_push_all_files_zipzip() throws IOException {
    expectFile("zip.zip!a/a.txt");
    expectFile("zip.zip!b/b.txt");
    expectFile("zip.zip!u/d/f/g.ppp");
    expectFile("zip.zip!lib/foo.jar");

    myStep.process(mockFile("zip.zip", zipStream("a/a.txt", "b/b.txt", "u/d/f/g.ppp", "lib/foo.jar!a,b,e,fff.jar")), c);

    m.assertIsSatisfied();
  }
}
