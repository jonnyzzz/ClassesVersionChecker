package jetbrains.buildServer.tools.rules;

import jetbrains.buildServer.tools.CheckAction;
import org.jetbrains.annotations.NotNull;

/**
 * Created 27.05.13 18:13
 *
 * @author Eugene Petrenko (eugene.petrenko@jetbrains.com)
 */
public interface CheckHolder {
  @NotNull
  CheckAction getCheckAction();
}
