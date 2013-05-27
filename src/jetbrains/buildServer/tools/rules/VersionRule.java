/*
 * Copyright 2000-2011 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jetbrains.buildServer.tools.rules;

import jetbrains.buildServer.tools.CheckAction;
import jetbrains.buildServer.tools.checkers.ClassFileChecker;
import jetbrains.buildServer.tools.java.JavaVersion;
import org.jetbrains.annotations.NotNull;

/**
* @author Eugene Petrenko (eugene.petrenko@gmail.com)
*         Date: 08.11.11 15:41
*/
public class VersionRule extends PathRule implements CheckHolder {
  private final JavaVersion myVersion;

  public VersionRule(@NotNull final String path, @NotNull final JavaVersion version) {
    super(path);
    myVersion = version;
  }

  @NotNull
  public JavaVersion getVersion() {
    return myVersion;
  }

  @Override
  public String toString() {
    return "VersionRule{" + super.toString() + ", " +
            "myVersion=" + myVersion +
            '}';
  }

  @NotNull
  public CheckAction getCheckAction() {
    return new ClassFileChecker(getVersion());
  }
}
