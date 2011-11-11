package jetbrains.buildServer.tools.test;

import jetbrains.buildServer.tools.ScanFile;
import jetbrains.buildServer.tools.rules.PathRule;
import jetbrains.buildServer.tools.rules.PathRules;
import org.jetbrains.annotations.NotNull;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 08.11.11 17:02
 */
public class PathRulesTest {
  @Test
  public void testMathLonger() {
    PathRules<PathRule> r = new PathRules<PathRule>(Arrays.asList(new PathRule("a"), new PathRule("a/aa")));
    PathRule rule = r.findRule(mockFile("a/aa/aaabbbaa"));

    Assert.assertNotNull(rule);
    Assert.assertNotNull(rule.getPath().equals("a/aa"));
  }

  @Test
  public void testMathLonger2() {
    PathRules<PathRule> r = new PathRules<PathRule>(
            Arrays.asList(new PathRule("a"),
                    new PathRule("a/aa"),
                    new PathRule("a/aa/aaaa"),
                    new PathRule(""),
                    new PathRule("a/a/a/a/a/a/a/a/a")
                    ));
    PathRule rule = r.findRule(mockFile("a/aa/aaabbbaa"));

    Assert.assertNotNull(rule);
    Assert.assertNotNull(rule.getPath().equals("a/aa"));
  }

  @Test
  public void testMathLonger_middle() {
    PathRules<PathRule> r = new PathRules<PathRule>(
            Arrays.asList(new PathRule("a"),
                    new PathRule("a/aa"),
                    new PathRule("a/aa/aaa"),
                    new PathRule(""),
                    new PathRule("a/aa/aaab"),
                    new PathRule("a/a/a/a/a/a/a/a/a")
                    ));
    PathRule rule = r.findRule(mockFile("a/aa/aaabbbaa"));

    Assert.assertNotNull(rule);
    Assert.assertNotNull(rule.getPath().equals("a/aa/aaab"));
  }

  private ScanFile mockFile(final String s) {
    return new ScanFile() {
      @NotNull
      public InputStream openStream() throws IOException {
        return null;
      }

      @NotNull
      public String getName() {

        return s;
      }

      public boolean isFile() {
        return false;
      }
    };
  }

}
