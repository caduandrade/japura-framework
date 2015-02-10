package org.japura.task.executors.parallel;

import java.util.ArrayList;

import javax.swing.SwingUtilities;

import org.japura.Application;
import org.japura.task.Task;
import org.japura.task.TaskStatus;
import org.japura.task.executors.AbstractTaskWrapper;
import org.japura.task.messages.notify.TaskExecutionMessage;
import org.japura.task.session.TaskSession;

/**
 * <P>
 * Copyright (C) 2013-2014 Carlos Eduardo Leite de Andrade
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
class TaskWrapper extends AbstractTaskWrapper implements Runnable{

  private Task task;

  public TaskWrapper(Task task, TaskSession session) {
	super(session);
	this.task = task;
  }

  @Override
  public void run() {
	fireBeforeTaskExecutionEvent(getTaskSession(), task);

	if (getTaskSession().isCanceled()) {
	  task.registerStatus(TaskStatus.CANCELED);
	}

	run(task);

	if (Application.getTaskManager().isNotifyMessagesEnabled()) {
	  Application.getMessageManager().publish(false,
		  new TaskExecutionMessage(true, getTaskSession(), task, null));
	}

	fireAfterTaskExecution(task);

	Runnable runnable = buildRunnable(task, new ArrayList<Task>());

	if (runnable == null) {
	  return;
	}

	if (Application.getTaskManager().invokeTaskMethodsOnEDT()) {
	  SwingUtilities.invokeLater(runnable);
	} else {
	  runnable.run();
	}
  }

}
