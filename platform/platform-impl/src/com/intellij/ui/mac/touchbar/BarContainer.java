// Copyright 2000-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.ui.mac.touchbar;

public interface BarContainer {
  TouchBar get();
  void release();

  default boolean isTemporary() { return false; }
}
