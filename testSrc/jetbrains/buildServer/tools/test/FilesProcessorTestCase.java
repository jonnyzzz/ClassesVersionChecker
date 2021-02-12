package jetbrains.buildServer.tools.test;

import jetbrains.buildServer.tools.ErrorKind;
import jetbrains.buildServer.tools.ScanFile;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

/**
 * Created 31.07.13 19:26
 *
 * @author Eugene Petrenko (eugene.petrenko@jetbrains.com)
 */
public abstract class FilesProcessorTestCase extends RulesBaseTestCase {
  @Test
  public void should_support_archive_as_scan_root() throws IOException {
    saveFile("foo.zip", zipStream(
            zipStream("a/zipInZip.jar", file("foo.txt", "aaaa".getBytes())),
            zipStream("q/zip3Zip.jar", file("another-foo.txt", "aaaa".getBytes())),
            zipStream("z/zip4Zip.jar",
                    classBytes("51.class", 51), //1.7
                    classBytes("50.class", 50), //1.6
                    classBytes("49.class", 49), //1.5
                    classBytes("48.class", 48), //1.4
                    classBytes("47.class", 47), //1.3
                    classBytes("46.class", 46), //1.2
                    file("someOther", "Kino Rulezz".getBytes())
            ),
            zipStream("x/zip4Zip.jar",
                    classBytes("51.class", 47) //1.3
                    )
            )
    );
    myHome = new File(myHome, "foo.zip");
    runTest("\n 1.3 => \n 1.7 => z");
  }

  @Test
  public void test_class_in_zip4_1_7() throws IOException {
    writeAllVersionClassesToZip4();
    runTest("1.7 =>");
  }

  @Test
  public void test_class_in_zip4_1_6() throws IOException {
    expectCheckError("foo.zip!a/zipInZip.jar!q/zip3Zip.jar!z/zip4Zip.jar!51.class", ErrorKind.VERSION);
    writeAllVersionClassesToZip4();
    runTest("1.6 =>");
  }

  @Test
  public void test_class_in_zip4_1_5() throws IOException {
    expectCheckError("foo.zip!a/zipInZip.jar!q/zip3Zip.jar!z/zip4Zip.jar!51.class", ErrorKind.VERSION);
    expectCheckError("foo.zip!a/zipInZip.jar!q/zip3Zip.jar!z/zip4Zip.jar!50.class", ErrorKind.VERSION);

    writeAllVersionClassesToZip4();
    runTest("1.5 =>");
  }

  @Test
  public void test_class_in_zip4_1_4() throws IOException {
    expectCheckError("foo.zip!a/zipInZip.jar!q/zip3Zip.jar!z/zip4Zip.jar!51.class", ErrorKind.VERSION);
    expectCheckError("foo.zip!a/zipInZip.jar!q/zip3Zip.jar!z/zip4Zip.jar!50.class", ErrorKind.VERSION);
    expectCheckError("foo.zip!a/zipInZip.jar!q/zip3Zip.jar!z/zip4Zip.jar!49.class", ErrorKind.VERSION);

    writeAllVersionClassesToZip4();
    runTest("1.4 =>");
  }

  @Test
  public void test_class_in_zip4_1_3() throws IOException {
    expectCheckError("foo.zip!a/zipInZip.jar!q/zip3Zip.jar!z/zip4Zip.jar!51.class", ErrorKind.VERSION);
    expectCheckError("foo.zip!a/zipInZip.jar!q/zip3Zip.jar!z/zip4Zip.jar!50.class", ErrorKind.VERSION);
    expectCheckError("foo.zip!a/zipInZip.jar!q/zip3Zip.jar!z/zip4Zip.jar!49.class", ErrorKind.VERSION);
    expectCheckError("foo.zip!a/zipInZip.jar!q/zip3Zip.jar!z/zip4Zip.jar!48.class", ErrorKind.VERSION);

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
    expectCheckError("foo.zip!51.class", ErrorKind.VERSION);
    writeAllVersionClassesToZip();
    runTest("1.6 =>");
  }

  @Test
  public void test_class_in_zip_1_5() throws IOException {
    expectCheckError("foo.zip!51.class", ErrorKind.VERSION);
    expectCheckError("foo.zip!50.class", ErrorKind.VERSION);

    writeAllVersionClassesToZip();
    runTest("1.5 =>");
  }

  @Test
  public void test_class_in_zip_1_4() throws IOException {
    expectCheckError("foo.zip!51.class", ErrorKind.VERSION);
    expectCheckError("foo.zip!50.class", ErrorKind.VERSION);
    expectCheckError("foo.zip!49.class", ErrorKind.VERSION);

    writeAllVersionClassesToZip();
    runTest("1.4 =>");
  }

  @Test
  public void test_class_in_zip_1_3() throws IOException {
    expectCheckError("foo.zip!51.class", ErrorKind.VERSION);
    expectCheckError("foo.zip!50.class", ErrorKind.VERSION);
    expectCheckError("foo.zip!49.class", ErrorKind.VERSION);
    expectCheckError("foo.zip!48.class", ErrorKind.VERSION);

    writeAllVersionClassesToZip();
    runTest("1.3 =>");
  }

  @Test
  public void test_ignore_module_info_classes() throws IOException {
    expectNoErrors();

    writeZipWithModuleInfoClasses();
    runTest("1.8 =>");
  }

  private void expectNoErrors() {
    m.checking(new Expectations(){{
      never(rep).postCheckError(with(any(ScanFile.class)), with(any(ErrorKind.class)), with(any(String.class)));
      never(rep).postCheckError(with(any(ScanFile.class)), with(any(ErrorKind.class)), with(any(String.class)), with(any(String.class)));
      never(rep).postError(with(any(ScanFile.class)), with(any(String.class)));
    }});
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

  private void writeZipWithModuleInfoClasses() throws IOException {
    saveFile("foo.zip",
            zipStream(
                    zipStream("a/zipInZip.jar",
                            classBytes("module-info.class", 55), // java 11
                            classBytes("52.class", 52), //1.8
                            file("someOther", "Kino Rulezz".getBytes())
                    ),
                    classBytes("a/b/c/d/module-info.class", 56) // java 12
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
  public void test_all_9() throws IOException {
    writeAllVersionClasses();
    runTest("9 =>");
  }

  @Test
  public void test_all_1_8() throws IOException {
    expectCheckError("53.class", ErrorKind.VERSION);
    writeAllVersionClasses();
    runTest("1.8 =>");
  }

  @Test
  public void test_all_1_7() throws IOException {
    expectCheckError("53.class", ErrorKind.VERSION);
    expectCheckError("52.class", ErrorKind.VERSION);
    writeAllVersionClasses();
    runTest("1.7 =>");
  }

  @Test
  public void test_all_1_6() throws IOException {
    expectCheckError("53.class", ErrorKind.VERSION);
    expectCheckError("52.class", ErrorKind.VERSION);
    expectCheckError("51.class", ErrorKind.VERSION);
    writeAllVersionClasses();
    runTest("1.6 =>");
  }

  @Test
  public void test_all_1_5() throws IOException {
    expectCheckError("53.class", ErrorKind.VERSION);
    expectCheckError("52.class", ErrorKind.VERSION);
    expectCheckError("51.class", ErrorKind.VERSION);
    expectCheckError("50.class", ErrorKind.VERSION);
    writeAllVersionClasses();
    runTest("1.5 =>");
  }

  @Test
  public void test_all_1_4() throws IOException {
    expectCheckError("53.class", ErrorKind.VERSION);
    expectCheckError("52.class", ErrorKind.VERSION);
    expectCheckError("51.class", ErrorKind.VERSION);
    expectCheckError("50.class", ErrorKind.VERSION);
    expectCheckError("49.class", ErrorKind.VERSION);

    writeAllVersionClasses();
    runTest("1.4 =>");
  }

  @Test
  public void test_all_1_3() throws IOException {
    expectCheckError("53.class", ErrorKind.VERSION);
    expectCheckError("52.class", ErrorKind.VERSION);
    expectCheckError("51.class", ErrorKind.VERSION);
    expectCheckError("50.class", ErrorKind.VERSION);
    expectCheckError("49.class", ErrorKind.VERSION);
    expectCheckError("48.class", ErrorKind.VERSION);

    writeAllVersionClasses();
    runTest("1.3 =>");
  }

  @Test
  public void test_all_1_2() throws IOException {
    expectCheckError("53.class", ErrorKind.VERSION);
    expectCheckError("52.class", ErrorKind.VERSION);
    expectCheckError("51.class", ErrorKind.VERSION);
    expectCheckError("50.class", ErrorKind.VERSION);
    expectCheckError("49.class", ErrorKind.VERSION);
    expectCheckError("48.class", ErrorKind.VERSION);
    expectCheckError("47.class", ErrorKind.VERSION);

    writeAllVersionClasses();
    runTest("1.2 =>");
  }

  @Test
  public void test_broken_class() throws IOException {
    saveFile("foo.class", "this is not a right class".getBytes());
    expectCheckError("foo.class", ErrorKind.VERSION);
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

  @Test
  public void test_not_found_file_rule4() throws IOException {
    expectRuleNotVisited("foo");
    saveFile("foo/aaa.txt", "qqq".getBytes());

    //there should be some classes to check!
    runTest("1.4 => foo");
  }

  @Test
  public void test_not_found_file_rule5() throws IOException {
    expectRuleNotVisited("foo.zip");
    saveFile("foo.zip", zipStream("aaa.ppp", "some.file.zzz"));

    //there should be some classes to check!
    runTest("1.4 => foo.zip");
  }

  @Test
  public void test_no_rule_specified() throws IOException {
    expectCheckError("some/file.class", ErrorKind.PATTERN);
    saveFile("some/file.class", "aaa".getBytes());
    saveFile("foo/aaa.class", classBytes(10));

    runTest("1.4 => foo");
  }

  private void writeAllVersionClasses() throws IOException {
    saveFile("53.class", classBytes(53)); //1.9
    saveFile("52.class", classBytes(52)); //1.8
    saveFile("51.class", classBytes(51)); //1.7
    saveFile("50.class", classBytes(50)); //1.6
    saveFile("49.class", classBytes(49)); //1.5
    saveFile("48.class", classBytes(48)); //1.4
    saveFile("47.class", classBytes(47)); //1.3
    saveFile("46.class", classBytes(46)); //1.2
  }

  protected abstract void runTest(@NotNull String config) throws IOException;
}
