package testData;

/**
 * Created 15.05.13 15:32
 *
 * @author Eugene Petrenko (eugene.petrenko@jetbrains.com)
 */
public class StaticFieldsTest_NestedClasses {
  public static class Foo {
    public static Foo foo;
  }

  public void bar() {
    class ZZZ {
      public ZZZ z = new ZZZ();
    }
  }

  public static class N {
    public static class M {
      public static class Q {
        public static final N n1 = new N();
        public static final M m1 = new M();
        public static final Q q1 = new Q();

        public static N n2 = new N();
        public static M m2 = new M();
        public static Q q2 = new Q();

        final N n3 = new N();
        final M m3 = new M();
        final Q q3 = new Q();
      }
    }
  }

}
