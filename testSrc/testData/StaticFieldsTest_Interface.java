package testData;

/**
 * Created 15.05.13 16:35
 *
 * @author Eugene Petrenko (eugene.petrenko@jetbrains.com)
 */
public interface StaticFieldsTest_Interface {
  StaticFieldsTest_Interface singleton = new StaticFieldsTest_Interface(){};
  int magic = 255;

  enum Foo {
    A, B, C
  }
}
