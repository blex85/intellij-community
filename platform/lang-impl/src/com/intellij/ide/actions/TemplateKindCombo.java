/*
 * Copyright 2000-2010 JetBrains s.r.o.
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
package com.intellij.ide.actions;

import com.intellij.ide.ui.ListCellRendererWrapper;
import com.intellij.ide.ui.UISettings;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CustomShortcutSet;
import com.intellij.openapi.actionSystem.KeyboardShortcut;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Trinity;
import com.intellij.ui.ComboboxSpeedSearch;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.KeyEvent;

public class TemplateKindCombo extends JComboBox {
  private static final Logger LOG = Logger.getInstance("#com.intellij.ide.actions.TemplateKindCombo");

  public TemplateKindCombo() {
    super();

    setRenderer(new ListCellRendererWrapper(getRenderer()) {
      @Override
      public void customize(final JList list, final Object value, final int index, final boolean selected, final boolean cellHasFocus) {
        if (value instanceof Trinity) {
          setText((String)((Trinity)value).first);
          setIcon ((Icon)((Trinity)value).second);
        }
      }
    });

    new ComboboxSpeedSearch(this) {
      @Override
      protected String getElementText(Object element) {
        if (element instanceof Trinity) {
          return (String)((Trinity)element).first;
        }
        return null;
      }
    };
  }

  public void addItem(String presentableName, Icon icon, String templateName) {
    addItem(new Trinity<String, Icon, String>(presentableName, icon, templateName));
  }

  public String getSelectedName() {
    //noinspection unchecked
    final Trinity<String, Icon, String> trinity = (Trinity<String, Icon, String>)getSelectedItem();
    if (trinity == null) {
      LOG.error("Model: " + getModel());
    }
    return trinity.third;
  }

  public void setSelectedName(@Nullable String name) {
    if (name == null) return;
    ComboBoxModel model = getModel();
    for (int i = 0, n = model.getSize(); i < n; i++) {
      Trinity<String, Icon, String> trinity = (Trinity<String, Icon, String>)model.getElementAt(i);
      if (name.equals(trinity.third)) {
        setSelectedItem(trinity);
        return;
      }
    }
  }

  public void registerUpDownHint(JComponent component) {
    final AnAction arrow = new AnAction() {
      @Override
      public void actionPerformed(AnActionEvent e) {
        if (e.getInputEvent() instanceof KeyEvent) {
          final int code = ((KeyEvent)e.getInputEvent()).getKeyCode();
          scrollBy(code == KeyEvent.VK_DOWN ? 1 : code == KeyEvent.VK_UP ? -1 : 0);
        }
      }
    };
    final KeyboardShortcut up = new KeyboardShortcut(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), null);
    final KeyboardShortcut down = new KeyboardShortcut(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), null);
    arrow.registerCustomShortcutSet(new CustomShortcutSet(up, down), component);
  }

  private void scrollBy(int delta) {
    final int size = getModel().getSize();
    int next = getSelectedIndex() + delta;
    if (next < 0 || next >= size) {
      if (!UISettings.getInstance().CYCLE_SCROLLING) {
        return;
      }
      next = (next + size) % size;
    }
    setSelectedIndex(next);
  }

}
