package testData;

/**
 * Created 15.05.13 15:37
 *
 * @author Eugene Petrenko (eugene.petrenko@jetbrains.com)
 */
public class StaticFieldsTest_Static {
  private static Logger LOG1 = new Logger();
  public static Logger LOG2 = new Logger();
  protected static Logger LOG3 = new Logger();
  static Logger LOG4 = new Logger();

  private static int iLOG1 = 1;
  public static int iLOG2 = 2;
  protected static int iLOG3 = 3;
  static int iLOG4 = 4;

  private static class Logger {}
}
