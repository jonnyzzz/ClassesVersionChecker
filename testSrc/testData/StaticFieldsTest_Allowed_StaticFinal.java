package testData;

/**
 * Created 15.05.13 17:24
 *
 * @author Eugene Petrenko (eugene.petrenko@jetbrains.com)
 */
public class StaticFieldsTest_Allowed_StaticFinal {
  public static final int i1 = 42;
  public static final long i2 = 42;
  public static final short i3 = 42;
  public static final char i4 = 42;
  public static final byte i5 = 42;
  public static final double i6 = 42;
  public static final float i7 = 42;
  public static final String i8 = "42";
  public static final boolean i9 = false;


  public static final int xi1 = doSomeStaticInit();
  public static final long xi2 = doSomeStaticInit();
  public static final short xi3 = doSomeStaticInit();
  public static final char xi4 = doSomeStaticInit3();
  public static final byte xi5 = doSomeStaticInit();
  public static final double xi6 = doSomeStaticInit();
  public static final float xi7 = doSomeStaticInit();
  public static final String xi8 = doSomeStaticInit2();
  public static final boolean xi9 = doSomeStaticInit4();

  private static byte doSomeStaticInit() {
    return 42;
  }

  private static String doSomeStaticInit2() {
    return "42";
  }

  private static char doSomeStaticInit3() {
    return 2;
  }

  private static boolean doSomeStaticInit4() {
    return true;
  }
}
