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

package jetbrains.buildServer.tools;

import org.jetbrains.annotations.NotNull;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.File;
import java.io.IOException;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 11.11.11 15:07
 */
public class BaseTestCase {
  private TempFiles myTempFiles = new TempFiles();

  @BeforeMethod
  public void setUp() {
    myTempFiles.cleanup();
  }

  @AfterMethod
  public void tearDown() {
    myTempFiles.cleanup();
  }

  public File createTempFile() throws IOException {
    return myTempFiles.createTempFile();
  }

  public File createTempFile(int size) throws IOException {
    return myTempFiles.createTempFile(size);
  }

  public File createTempDir() throws IOException {
    return myTempFiles.createTempDir();
  }

  public void delete(@NotNull File file) {
    myTempFiles.delete(file);
  }
}
