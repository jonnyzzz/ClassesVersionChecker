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

import jetbrains.buildServer.tools.*;
import jetbrains.buildServer.tools.rules.PathRule;
import jetbrains.buildServer.tools.rules.PathSettings;
import jetbrains.buildServer.tools.rules.RulesParser;
import jetbrains.buildServer.tools.rules.VersionRule;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jmock.Mockery;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 11.11.11 15:29
 */
public class RulesBaseTestCase extends BaseTestCase {
  protected Mockery m;
  protected Continuation c;
  protected File myHome;
  protected ErrorReporting rep;


  @BeforeMethod
  @Override
  public void setUp() throws IOException {
    super.setUp();
    myHome = createTempDir();
    m = new Mockery();
    c = m.mock(Continuation.class);
    rep = m.mock(ErrorReporting.class);
  }

  @AfterMethod
  @Override
  public void tearDown() {
    super.tearDown();
    m.assertIsSatisfied();
  }

  @NotNull
  protected PathSettings parseConfig(@NotNull final String configText) throws IOException {
    return new RulesParser().parseConfig(new StringReader(configText)).build();
  }

  @NotNull
  public ScanFile mockFile(@NotNull final String relPath) {
    return new ScanFile() {
      @NotNull
      public InputStream openStream() throws IOException {
        throw new UnsupportedOperationException();
      }

      @NotNull
      public String getName() {
        return relPath;
      }

      public boolean isFile() {
        return false;
      }
    };
  }

  @NotNull
  public ScanFile mockFile(@NotNull final String relPath, @NotNull final byte[] content) {
    return new ScanFile() {
      @NotNull
      public InputStream openStream() throws IOException {
        return new ByteArrayInputStream(content);
      }

      @NotNull
      public String getName() {
        return relPath;
      }

      public boolean isFile() {
        return true;
      }
    };
  }


  protected class Expectations extends org.jmock.Expectations {
    protected BaseMatcher<ScanFile> file(@NotNull final String path) {
      final String name = resolve(path);
      return new BaseMatcher<ScanFile>() {
        public boolean matches(Object item) {
          ScanFile sf = (ScanFile) item;
          return sf.getName().equals(name);
        }

        public void describeTo(Description description) {
          description.appendText("ScanFile{" + name + "}");
        }
      };
    }

    @NotNull
    private String resolve(@NotNull String path) {
      return mockFile(path).getName();
    }

    protected BaseMatcher<VersionRule> versionRule(@NotNull final String path) {
      final String name = resolve(path);
      return new BaseMatcher<VersionRule>() {
        public boolean matches(Object item) {
          VersionRule vs = (VersionRule) item;
          return vs.getPath().equals(name);
        }

        public void describeTo(Description description) {
          description.appendText("Version Rule: " + name);
        }
      };
    }
  }

  public void expectFile(@NotNull final String name) {
    m.checking(new Expectations(){{
      oneOf(c).postTask(with(file(name)));
    }});
  }

  public void expectCheckError(@NotNull final String name, @NotNull final ErrorKind kind) {
    m.checking(new Expectations(){{
      oneOf(rep).postCheckError(with(file(name)), with(equal(kind)), with(any(String.class)));
    }});
  }

  public void expectGenericError(@NotNull final String name) {
    m.checking(new Expectations(){{
      oneOf(rep).postError(with(file(name)), with(any(String.class)));
    }});
  }

  public void expectRuleNotVisited(@NotNull final String name) {
    m.checking(new Expectations(){{
      oneOf(rep).ruleNotVisited(with(versionRule(name)));
    }});
  }


  @NotNull
  protected byte[] zipStream(@NotNull final String... files) throws IOException {
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    zipStream(os, files);

    os.close();
    return os.toByteArray();
  }

  @NotNull
  protected ZipAction zipStream(@NotNull final String name, @NotNull final ZipAction... files) throws IOException {
    final byte[] bytes = zipStream(files);
    return new ZipAction(name) {
      @Override
      public byte[] getContent() {
        return bytes;
      }
    };
  }

  @NotNull
  protected ZipAction file(@NotNull final String name, @NotNull final byte[] content) {
    return new ZipFileAction(name, content);
  }

  protected byte[] zipStream(@NotNull final ZipAction... files) throws IOException {
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    zipStream(os, files);

    os.close();
    return os.toByteArray();
  }

  private void zipStream(@NotNull final OutputStream os, @NotNull final String[] files) throws IOException {
    final ZipOutputStream zos = new ZipOutputStream(os);

    for (String file : files) {
      if (!file.contains(".")) {
        zos.putNextEntry(new ZipEntry(file));
      } else {
        int sp = file.indexOf('!');
        if (sp < 0) {
          zos.putNextEntry(new ZipEntry(file));
          zos.write(file.getBytes());
        } else {
          final String path = file.substring(0, sp);
          zos.putNextEntry(new ZipEntry(path));

          final String[] rest = file.substring(sp).split(",");
          zipStream(zos, rest);
        }
      }
    }
    zos.finish();
  }

  private void zipStream(@NotNull final OutputStream os, @NotNull final ZipAction[] files) throws IOException {
    final ZipOutputStream zos = new ZipOutputStream(os);

    for (ZipAction file : files) {
      zos.putNextEntry(new ZipEntry(file.getName()));
      final byte[] content = file.getContent();
      if (content != null) {
        zos.write(content);
      }
    }
    zos.finish();
  }

  protected abstract class ZipAction {
    private final String myName;

    public ZipAction(@NotNull final String name) {
      myName = name;
    }

    public String getName() {
      return myName;
    }

    @Nullable
    public abstract byte[] getContent();
  }

  protected class ZipFileAction extends ZipAction {
    @NotNull
    private final byte[] myContent;

    public ZipFileAction(@NotNull String name, @NotNull final byte[] content) {
      super(name);
      myContent = content;
    }

    @NotNull
    @Override
    public byte[] getContent() {
      return myContent;
    }
  }

  protected void saveFile(@NotNull final String name, @NotNull final byte[] ps) throws IOException {
    final File file = new File(myHome, name);
    //noinspection ResultOfMethodCallIgnored
    file.getParentFile().mkdirs();
    OutputStream os = new FileOutputStream(file);
    os.write(ps);
    os.close();
  }

  @NotNull
  protected byte[] classBytes(int version) {
    return new byte[]{(byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE, 0, 0, 0, (byte) version, 0, 0, 0, 0, 0};
  }

  @NotNull
  protected ZipAction classBytes(@NotNull String name, int version) {
    return file(name, classBytes(version));
  }

  protected void assertRule(@Nullable final PathRule actual, @Nullable final String expectedPath) {
    if (actual == null) {
      Assert.assertNull(expectedPath);
    } else {
      Assert.assertEquals(actual.getPath(), expectedPath);
    }
  }
}
