package org.japura.task.executors;

import java.util.Collection;

import org.japura.Application;
import org.japura.task.Task;
import org.japura.task.TaskStatus;
import org.japura.task.manager.TaskManagerEventType;
import org.japura.task.messages.notify.TaskEventMessage;
import org.japura.task.messages.notify.TaskEventType;
import org.japura.task.session.TaskSession;
import org.japura.task.ui.TaskExecutionUIEvent;

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
public abstract class AbstractTaskWrapper{

  private final TaskSession session;

  public AbstractTaskWrapper(TaskSession session) {
	if (session == null) {
	  throw new IllegalArgumentException(TaskSession.class.getSimpleName()
		  + " NULL");
	}
	this.session = session;
  }

  public final TaskSession getTaskSession() {
	return session;
  }

  protected void fireBeforeTaskExecutionEvent(TaskSession session, Task task) {
	if (Application.getTaskManager().isNotifyMessagesEnabled()) {
	  Application.getMessageManager().publish(false,
		  new TaskEventMessage(task, getTaskSession(), TaskEventType.BEFORE));
	}
	Application.getTaskManager().fireTaskExecutionUIs(
		session.getTaskExecutor(),
		TaskManagerEventType.BEFORE_TASK_EXECUTION,
		new TaskExecutionUIEvent(getTaskSession().getExecutionType(), session,
			task));
  }

  protected void fireAfterTaskExecution(Task task) {
	if (Application.getTaskManager().isNotifyMessagesEnabled()) {
	  Application.getMessageManager().publish(false,
		  new TaskEventMessage(task, getTaskSession(), TaskEventType.AFTER));
	}
	Application.getTaskManager().fireTaskExecutionUIs(
		session.getTaskExecutor(),
		TaskManagerEventType.AFTER_TASK_EXECUTION,
		new TaskExecutionUIEvent(getTaskSession().getExecutionType(), session,
			task));
  }

  protected Runnable buildRunnable(final Task task,
								   final Collection<Task> discardedTasks) {
	Runnable runnable = null;
	if (task.getStatus().equals(TaskStatus.ERROR)) {
	  runnable = new Runnable() {
		@Override
		public void run() {
		  if (Application.getTaskManager().isNotifyMessagesEnabled()) {
			Application.getMessageManager().publish(
				false,
				new TaskEventMessage(task, getTaskSession(),
					TaskEventType.ERROR));
		  }
		  task.handleException(getTaskSession(), task.getException(),
			  discardedTasks);
		}
	  };
	} else if (task.getStatus().equals(TaskStatus.DONE)) {
	  runnable = new Runnable() {
		@Override
		public void run() {
		  if (Application.getTaskManager().isNotifyMessagesEnabled()) {
			Application.getMessageManager()
				.publish(
					false,
					new TaskEventMessage(task, getTaskSession(),
						TaskEventType.DONE));
		  }
		  task.done(getTaskSession());
		}
	  };
	} else if (task.getStatus().equals(TaskStatus.CANCELED)) {
	  runnable = new Runnable() {
		@Override
		public void run() {
		  if (Application.getTaskManager().isNotifyMessagesEnabled()) {
			Application.getMessageManager().publish(
				false,
				new TaskEventMessage(task, getTaskSession(),
					TaskEventType.CANCELED));
		  }
		  task.canceled(getTaskSession());
		}
	  };
	}
	return runnable;
  }

  protected void run(Task task) {
	if (task.getStatus().equals(TaskStatus.CANCELED)
		|| task.getStatus().equals(TaskStatus.ERROR)) {
	  return;
	}

	try {
	  task.willExecute(getTaskSession());
	} catch (Exception e) {
	  task.registerException(e);
	}

	if (task.getStatus().equals(TaskStatus.CANCELED)
		|| task.getStatus().equals(TaskStatus.ERROR)) {
	  return;
	}

	long startTime = System.currentTimeMillis();
	TaskEventMessage msg = null;
	if (Application.getTaskManager().isNotifyMessagesEnabled()) {
	  msg =
		  new TaskEventMessage(task, getTaskSession(),
			  TaskEventType.DO_IN_BACKGROUND);
	  Application.getMessageManager().publish(false, msg);
	}
	task.registerStatus(TaskStatus.EXECUTING);
	try {
	  task.doInBackground(getTaskSession());
	} catch (Exception e) {
	  task.registerException(e);
	}

	if (task.getStatus().equals(TaskStatus.EXECUTING)) {
	  if (getTaskSession().isCanceled()) {
		task.registerStatus(TaskStatus.CANCELED);
	  } else {
		task.registerStatus(TaskStatus.DONE);
	  }
	}
	task.registerBackgroundTimeSpent(System.currentTimeMillis() - startTime);
	if (msg != null) {
	  msg.getTaskEvent().setTimeSpent(task.getBackgroundTimeSpent());
	}
  }
}
