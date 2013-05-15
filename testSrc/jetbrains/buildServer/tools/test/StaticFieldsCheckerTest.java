package jetbrains.buildServer.tools.test;

import jetbrains.buildServer.tools.ErrorReporting;
import jetbrains.buildServer.tools.ScanFile;
import jetbrains.buildServer.tools.checkers.StaticFieldsChecker;
import jetbrains.buildServer.tools.util.ClassPathUtil;
import org.jetbrains.annotations.NotNull;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.api.Invocation;
import org.jmock.lib.action.CustomAction;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import testData.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created 15.05.13 14:23
 *
 * @author Eugene Petrenko (eugene.petrenko@jetbrains.com)
 */
public class StaticFieldsCheckerTest {
  private Mockery m;
  private StaticFieldsChecker myChecker;
  private ErrorReporting myErrors;
  private List<String> myLoggerErrors;

  @BeforeMethod
  public void setUp() {
    m = new Mockery();
    myErrors = m.mock(ErrorReporting.class);
    myChecker = new StaticFieldsChecker();
    myLoggerErrors = new ArrayList<String>();
    m.checking(new Expectations(){{
      allowing(myErrors).postCheckError(with(any(ScanFile.class)), with(any(String.class)));
      will(new CustomAction("log") {
        public Object invoke(Invocation invocation) throws Throwable {
          final String error = (String) invocation.getParameter(1);
          myLoggerErrors.add(error);
          System.out.println(error);
          return null;
        }
      });
    }});
  }

  @AfterMethod
  public void tearDown() {
    m.assertIsSatisfied();
  }

  @Test
  public void test_report_static_final() throws IOException {
    processClass(StaticFieldsTest_StaticFinal.class, "Class contains 'testData.StaticFieldsTest_StaticFinal.LOG[1-4]' final static field");
  }

  @Test
  public void test_report_static() throws IOException {
    processClass(StaticFieldsTest_Static.class, "Class contains 'testData.StaticFieldsTest_Static.i?LOG[1-4]' non-final static field");
  }

  @Test
  public void test_report_static_nestedClasses() throws IOException {
    processClass(StaticFieldsTest_NestedClasses.class,
            "Class contains 'testData.StaticFieldsTest_NestedClasses$Foo.foo' non-final static field",
            "Class contains 'testData.StaticFieldsTest_NestedClasses$N$M$Q.[nmq][1-2]' (non-)?final static field"
            );
  }

  @Test
  public void test_report_static_no_issues() throws IOException {
    processClass(StaticFieldsTest_NotAnIssues.class);
  }

  @Test
  public void test_report_static_enums() throws IOException {
    processClass(StaticFieldsTest_Enum.class);
  }

  @Test
  public void test_report_static_interface() throws IOException {
    processClass(StaticFieldsTest_Interface.class, "Class contains 'testData.StaticFieldsTest_Interface.singleton' final static field");
  }

  @Test
  public void test_report_static_allowed_primitive_types() throws IOException {
    processClass(StaticFieldsTest_Allowed_StaticFinal.class,
            "Class contains 'testData.StaticFieldsTest_Allowed_StaticFinal.xi[1-9]' static final field");
  }

  private void processClass(@NotNull final Class<?> clazz, @NotNull String... errors) throws IOException {
    final File home = ClassPathUtil.getClassFile(clazz);
    Assert.assertNotNull(home);

    final File parentFile = home.getParentFile();
    Assert.assertNotNull(parentFile);

    System.out.println("Class home: " + parentFile);

    final File[] allFiles = parentFile.listFiles(new FilenameFilter() {
      public boolean accept(@NotNull File dir, @NotNull String name) {
        return name.equals(clazz.getSimpleName() + ".class") || name.startsWith(clazz.getSimpleName() + "$");
      }
    });
    Assert.assertNotNull(allFiles);
    Arrays.sort(allFiles);

    for (File cf : allFiles) {
      System.out.println("Checking: " + cf);
      myChecker.process(file(cf), myErrors);
    }

    System.out.flush();

    assertErrors(errors);
  }

  private void assertErrors(@NotNull String... errors) {
    List<String> all = new ArrayList<String>(myLoggerErrors);

    for (String error : errors) {
      Pattern pt = Pattern.compile(error.replace("$", "\\$").replace(".", "\\."));
      for (Iterator<String> iterator = all.iterator(); iterator.hasNext(); ) {
        if (pt.matcher(iterator.next()).matches()) iterator.remove();
      }
    }

    final StringBuilder sb = new StringBuilder();
    for (String s : all) {
      sb.append(s).append("\r\n");
    }
    Assert.assertTrue(all.isEmpty(), sb.toString());
  }

  @NotNull
  private ScanFile file(@NotNull final File clazz) throws IOException {
    final ScanFile sf = m.mock(ScanFile.class, clazz.getPath());
    m.checking(new Expectations(){{
      allowing(sf).getName(); will(returnValue(clazz.getPath()));
      allowing(sf).openStream(); will(new CustomAction("create stream") {
        public Object invoke(Invocation invocation) throws Throwable {
          return new FileInputStream(clazz);
        }
      });
    }});

    return sf;
  }

}
