package org.japura.task.executors.serial;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.SwingUtilities;

import org.japura.Application;
import org.japura.task.Task;
import org.japura.task.TaskExeception;
import org.japura.task.TaskStatus;
import org.japura.task.executors.AbstractTaskWrapper;
import org.japura.task.messages.notify.TaskEventMessage;
import org.japura.task.messages.notify.TaskEventType;
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
class TaskSessionWrapper extends AbstractTaskWrapper implements Runnable{

  private List<Task> queueTasks;
  private int runCount;
  private boolean finished;

  public TaskSessionWrapper(TaskSession session, Task... tasks) {
	super(session);
	this.queueTasks = new ArrayList<Task>();
	for (Task task : tasks) {
	  this.queueTasks.add(task);
	}
  }

  public int getRunCount() {
	return runCount;
  }

  public void addNestedTask(Task task) {
	if (finished) {
	  // TODO f
	  throw new TaskExeception("");
	}
	if (getTaskSession().isCanceled()) {
	  // TODO f
	  throw new TaskExeception("");
	}
	int index = queueTasks.size() - 1;
	while (index > -1) {
	  Task qt = queueTasks.get(index);
	  if (qt.getParentId() != null
		  && qt.getParentId().equals(task.getParentId())) {
		break;
	  }
	  index--;
	}
	queueTasks.add(index + 1, task);
  }

  @Override
  public void run() {
	String previousTaskId = null;
	while (queueTasks.size() > 0) {
	  Task task = queueTasks.remove(0);

	  runCount++;

	  fireBeforeTaskExecutionEvent(getTaskSession(), task);

	  if (getTaskSession().isCanceled()) {
		task.registerStatus(TaskStatus.CANCELED);
	  }

	  run(task);

	  // TODO exception, tratar

	  fireAfterTaskExecution(task);

	  if (Application.getTaskManager().isNotifyMessagesEnabled()) {
		Application.getMessageManager().publish(
			false,
			new TaskExecutionMessage(false, getTaskSession(), task,
				previousTaskId));
	  }

	  previousTaskId = task.getId();

	  Collection<Task> discardedTasks = new ArrayList<Task>();
	  if (task.getStatus().equals(TaskStatus.ERROR)) {
		for (Task otherTask : queueTasks) {
		  otherTask.registerStatus(TaskStatus.DISCARDED);
		  discardedTasks.add(otherTask);
		}
		queueTasks.clear();
		runCount += discardedTasks.size();
		for (Task discardedTask : discardedTasks) {
		  fireBeforeTaskExecutionEvent(getTaskSession(), discardedTask);
		  fireAfterTaskExecution(discardedTask);

		  if (Application.getTaskManager().isNotifyMessagesEnabled()) {
			Application.getMessageManager().publish(
				false,
				new TaskEventMessage(discardedTask, getTaskSession(),
					TaskEventType.DISCARDED));
			Application.getMessageManager().publish(
				false,
				new TaskExecutionMessage(false, getTaskSession(),
					discardedTask, previousTaskId));
		  }

		  previousTaskId = discardedTask.getId();
		}
	  }

	  Runnable runnable = buildRunnable(task, discardedTasks);

	  if (runnable == null) {
		return;
	  }

	  if (Application.getTaskManager().invokeTaskMethodsOnEDT()) {
		if (task.isWaitForEDT()) {
		  try {
			SwingUtilities.invokeAndWait(runnable);
		  } catch (InterruptedException e) {
			e.printStackTrace();
		  } catch (InvocationTargetException e) {
			e.printStackTrace();
		  }
		} else {
		  SwingUtilities.invokeLater(runnable);
		}
	  } else {
		runnable.run();
	  }

	}
	finished = true;
  }

}
