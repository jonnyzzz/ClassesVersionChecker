package jetbrains.buildServer.tools.rules;

import jetbrains.buildServer.tools.CheckAction;
import jetbrains.buildServer.tools.checkers.StaticFieldsChecker;
import org.jetbrains.annotations.NotNull;

/**
 * Created 27.05.13 18:07
 *
 * @author Eugene Petrenko (eugene.petrenko@jetbrains.com)
 */
public class StaticCheckRule extends PathRule implements CheckHolder {
  private final StaticRuleSettings mySettings;

  public StaticCheckRule(@NotNull final String path,
                         @NotNull final StaticRuleSettings settings) {
    super(path);
    mySettings = settings;
  }

  @Override
  public String toString() {
    return "StaticRule{" + super.toString() + '}';
  }

  @NotNull
  public CheckAction getCheckAction() {
    return new StaticFieldsChecker();
  }
}
