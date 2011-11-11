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
import jetbrains.buildServer.tools.ScanFile;
import jetbrains.buildServer.tools.rules.PathSettings;
import jetbrains.buildServer.tools.rules.RulesParser;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.BeforeMethod;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 11.11.11 15:29
 */
public class RulesBaseTestCase extends BaseTestCase {
  protected File myHome;

  @BeforeMethod
  @Override
  public void setUp() throws IOException {
    super.setUp();
    myHome = createTempDir();
  }

  protected PathSettings parseConfig(@NotNull final String configText) throws IOException {
    return RulesParser.parseConfig(myHome, new StringReader(configText));
  }

  @NotNull
  public ScanFile mockFile(@NotNull final String relPath) {
    final String path = new File(myHome, relPath).getPath();
    return new ScanFile() {
      @NotNull
      public InputStream openStream() throws IOException {
        throw new UnsupportedOperationException();
      }

      @NotNull
      public String getName() {
        return path;
      }

      public boolean isFile() {
        return false;
      }
    };
  }


}
