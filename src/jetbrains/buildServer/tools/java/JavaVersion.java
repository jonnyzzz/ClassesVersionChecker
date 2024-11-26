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

package jetbrains.buildServer.tools.java;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 08.11.11 13:58
 *
 */
public enum JavaVersion {
  ///see http://en.wikipedia.org/wiki/Java_class_file

  Java_24(68, "Java 24", "24"),
  Java_23(67, "Java 23", "23"),
  Java_22(66, "Java 22", "22"),
  Java_21(65, "Java 21", "21"),
  Java_20(64, "Java 20", "20"),
  Java_19(63, "Java 19", "19"),
  Java_18(62, "Java 18", "18"),
  Java_17(61, "Java 17", "17"),
  Java_16(60, "Java 16", "16"),
  Java_15(59, "Java 15", "15"),
  Java_14(58, "Java 14", "14"),
  Java_13(57, "Java 13", "13"),
  Java_12(56, "Java 12", "12"),
  Java_11(55, "Java 11", "11"),
  Java_10(54, "Java 10", "10"),
  Java_9(53, "Java 9", "9"),
  Java_1_8(52, "Java 1.8", "1.8"),
  Java_1_7(51, "Java 1.7", "1.7"),
  Java_1_6(50, "Java 1.6", "1.6"),
  Java_1_5(49, "Java 1.5", "1.5"),
  Java_1_4(48, "Java 1.4", "1.4"),
  Java_1_3(47, "Java 1.3", "1.3"),
  Java_1_2(46, "Java 1.2", "1.2"),
  ;

  private final int myClassVersion;
  private final String myName;
  private final String myShortName;

  JavaVersion(int classVersion, String name, String shortName) {
    myClassVersion = classVersion;
    myName = name;
    myShortName = shortName;
  }

  public int getClassVersion() {
    return myClassVersion;
  }

  @NotNull
  public String getName() {
    return myName;
  }

  @NotNull
  public String getShortName() {
    return myShortName;
  }

  @Override
  public String toString() {
    return getName() + " (" + getClassVersion() + ")";
  }

  public boolean canRunOn(@NotNull final JavaVersion jvm) {
    return this.getClassVersion() <= jvm.getClassVersion();
  }

  @Nullable
  public static JavaVersion find(int v) {
    for (JavaVersion version : values()) {
      if (version.getClassVersion() == v) return version;
    }
    if (v < Java_1_2.getClassVersion()) return Java_1_2;
    return null;
  }
}
