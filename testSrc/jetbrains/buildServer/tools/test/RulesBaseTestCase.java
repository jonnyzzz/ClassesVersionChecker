package jetbrains.buildServer.tools.test;

import jetbrains.buildServer.tools.BaseTestCase;
import jetbrains.buildServer.tools.rules.PathSettings;
import jetbrains.buildServer.tools.rules.RulesParser;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.BeforeMethod;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 11.11.11 15:29
 */
public class RulesBaseTestCase extends BaseTestCase {
  protected File myHome;

  @BeforeMethod
  @Override
  public void setUp() throws IOException {
    super.setUp();
    myHome = createTempDir();
  }

  protected PathSettings parseConfig(@NotNull final String configText) throws IOException {
    return RulesParser.parseConfig(myHome, new StringReader(configText));
  }
}
