package testData;

/**
 * Created 15.05.13 16:36
 *
 * @author Eugene Petrenko (eugene.petrenko@jetbrains.com)
 */
public enum StaticFieldsTest_MutableEnum {
  A,
  B,
  C
  ;

  public static StaticFieldsTest_MutableEnum CC = A;
  public int mutableFoo;
  String mutableFoo2;
}
