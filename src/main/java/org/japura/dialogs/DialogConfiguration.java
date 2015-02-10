package org.japura.dialogs;

import java.util.HashMap;
import java.util.Map;

/**
 * <P>
 * Copyright (C) 2014 Carlos Eduardo Leite de Andrade
 * <P>
 * This library is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * <P>
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * <P>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <A
 * HREF="www.gnu.org/licenses/">www.gnu.org/licenses/</A>
 * <P>
 * For more information, contact: <A HREF="www.japura.org">www.japura.org</A>
 * <P>
 *
 * @author Carlos Eduardo Leite de Andrade
 */
public class DialogConfiguration {

  private int focusedButton;
  private Map<Integer, Integer> buttonActionKey;

  public DialogConfiguration() {
    this.focusedButton = -1;
    this.buttonActionKey = new HashMap<Integer, Integer>();
  }

  public void setButtonActionKey(int button, int key) {
    this.buttonActionKey.put(button, key);
  }

  public int getButtonActionKey(int button) {
    Integer actionKey = this.buttonActionKey.get(button);
    if (actionKey != null) {
      return actionKey.intValue();
    }
    return -1;
  }

  public void setFocusedButton(int focusedButton) {
    this.focusedButton = focusedButton;
  }

  public int getFocusedButton() {
    return focusedButton;
  }
}
