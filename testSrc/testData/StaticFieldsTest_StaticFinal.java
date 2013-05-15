package testData;

/**
 * Created 15.05.13 14:24
 *
 * @author Eugene Petrenko (eugene.petrenko@jetbrains.com)
 */
public class StaticFieldsTest_StaticFinal {
  private static final Logger LOG1 = new Logger();
  public static final Logger LOG2 = new Logger();
  protected static final Logger LOG3 = new Logger();
  static final Logger LOG4 = new Logger();

  private static final int iLOG1 = 1;
  public static final int iLOG2 = 2;
  protected static final int iLOG3 = 3;
  static final int iLOG4 = 4;

  private static class Logger {}
}
