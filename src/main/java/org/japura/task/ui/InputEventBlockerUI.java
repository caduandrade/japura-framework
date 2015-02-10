package org.japura.task.ui;

import org.japura.Application;
import org.japura.gui.util.InputEventBlocker;

/**
 * <P>
 * Copyright (C) 2011-2014 Carlos Eduardo Leite de Andrade
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
public class InputEventBlockerUI implements TaskExecutionUI{

  private InputEventBlocker eventHook = new InputEventBlocker();
  private boolean first = true;

  @Override
  public void sessionCreated(TaskExecutionUIEvent event) {
	if (first && Application.getTaskManager().hasTask()) {
	  setEnabled(true);
	  first = false;
	}
  }

  @Override
  public void beforeTaskExecution(TaskExecutionUIEvent event) {}

  @Override
  public void afterTaskExecution(TaskExecutionUIEvent event) {}

  @Override
  public void sessionFinalized(TaskExecutionUIEvent event) {
	if (Application.getTaskManager().hasTask() == false) {
	  setEnabled(false);
	  first = true;
	}
  }

  @Override
  public void sessionCancelPermissionChanged(TaskExecutionUIEvent event) {}

  protected void setEnabled(boolean enabled) {
	if (enabled) {
	  eventHook.apply();
	} else {
	  eventHook.remove();
	}
  }

}
