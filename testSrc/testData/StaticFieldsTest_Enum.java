package testData;

/**
 * Created 15.05.13 16:06
 *
 * @author Eugene Petrenko (eugene.petrenko@jetbrains.com)
 */
public class StaticFieldsTest_Enum {
  public static enum Foo {
    A,B,C
    ;
    public static enum F2 {
      F
    }
  }

  public enum Bar {
    E,F,G
  }
}
