package org.japura.task.executors.serial;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.japura.Application;
import org.japura.task.Task;
import org.japura.task.TaskExeception;
import org.japura.task.executors.AbstractExecutor;
import org.japura.task.executors.ExecutionType;
import org.japura.task.executors.TaskExecutor;
import org.japura.task.manager.TaskManagerEventType;
import org.japura.task.messages.ExecutorPerformedMessage;
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
public class DefaultSerialExecutor extends AbstractExecutor implements
	SerialExecutor{

  private FutureTaskSessionWrapper<?> currentRunnable;

  private Object lock = new Object();
  private List<TaskSession> sessions;
  private Executor executor;

  public DefaultSerialExecutor(TaskExecutor taskExecutor) {
	super(taskExecutor);
	this.sessions = new ArrayList<TaskSession>();
	this.executor = new Executor();
  }

  @Override
  public ExecutionType getExecutionType() {
	return ExecutionType.SERIAL;
  }

  @Override
  public void cancel() {
	synchronized (lock) {
	  for (TaskSession session : this.sessions) {
		session.forceCancel();
	  }
	}
  }

  @Override
  public boolean hasTask() {
	synchronized (lock) {
	  return (sessions.size() > 0);
	}
  }

  @Override
  public void submitTasks(TaskSession session, Task... tasks) {
	synchronized (lock) {
	  this.sessions.add(session);
	}

	TaskSessionWrapper w = new TaskSessionWrapper(session, tasks);

	fireSubmitEvents(session, tasks);

	Application.getTaskManager().fireTaskExecutionUIs(getTaskExecutor(),
		TaskManagerEventType.SESSION_CREATED,
		new TaskExecutionUIEvent(getExecutionType(), session));

	this.executor.submit(w);
  }

  @Override
  public final void addNestedTasks(Task... nestedTasks) {
	FutureTaskSessionWrapper<?> runnable = this.currentRunnable;

	long currentThreadId = Thread.currentThread().getId();
	if (runnable == null || runnable.getThreadId() == null
		|| runnable.getThreadId().longValue() != currentThreadId) {
	  // TODO msg
	  throw new TaskExeception("");
	}

	// TODO verificar se task possui parent aqui tb?

	TaskSessionWrapper w = runnable.getTaskSessionWrapper();
	TaskSession session = w.getTaskSession();

	fireSubmitEvents(session, nestedTasks);

	for (Task task : nestedTasks) {
	  w.addNestedTask(task);
	}
  }

  @Override
  public Long getCurrentThreadIdExecution() {
	if (this.currentRunnable != null) {
	  return this.currentRunnable.getThreadId();
	}
	return null;
  }

  @Override
  public TaskSession getCurrentTaskSession() {
	if (this.currentRunnable != null) {
	  return this.currentRunnable.getTaskSessionWrapper().getTaskSession();
	}
	return null;
  }

  @Override
  public void shutdown() {
	this.executor.shutdown();
  }

  @Override
  protected void finalize() {
	if (this.executor.isShutdown() == false) {
	  this.executor.shutdown();
	}
  }

  private class Executor extends ThreadPoolExecutor{
	public Executor() {
	  super(1, 1, 0L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
	}

	@Override
	protected final <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
	  TaskSessionWrapper taskSessionWrapper = (TaskSessionWrapper) runnable;
	  return new FutureTaskSessionWrapper<T>(taskSessionWrapper, value);
	}

	@Override
	protected final void beforeExecute(Thread t, Runnable r) {
	  FutureTaskSessionWrapper<?> ftsw = (FutureTaskSessionWrapper<?>) r;
	  DefaultSerialExecutor.this.currentRunnable = ftsw;
	  ftsw.setThreadId(Thread.currentThread().getId());
	}

	@Override
	protected final void afterExecute(Runnable r, Throwable t) {
	  synchronized (lock) {
		FutureTaskSessionWrapper<?> ftsw = (FutureTaskSessionWrapper<?>) r;
		TaskSession session = ftsw.getTaskSessionWrapper().getTaskSession();
		sessions.remove(session);
		if (sessions.size() == 0) {
		  Application.getMessageManager().publish(false,
			  new ExecutorPerformedMessage());
		}

		Application.getTaskManager().fireTaskExecutionUIs(getTaskExecutor(),
			TaskManagerEventType.SESSION_FINALIZED,
			new TaskExecutionUIEvent(getExecutionType(), session));
	  }
	  DefaultSerialExecutor.this.currentRunnable = null;
	}
  }

}
