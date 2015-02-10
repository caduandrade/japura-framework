package org.japura.task.executors.parallel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.japura.Application;
import org.japura.task.Task;
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
public class DefaultParallelExecutor extends AbstractExecutor implements
	ParallelExecutor{

  private Object lock = new Object();
  private List<TaskSession> sessions;
  private Executor executor;

  public DefaultParallelExecutor(TaskExecutor taskExecutor) {
	super(taskExecutor);
	this.executor = new Executor();
	this.sessions = new ArrayList<TaskSession>();
  }

  @Override
  public ExecutionType getExecutionType() {
	return ExecutionType.PARALLEL;
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
  public final void submitTasks(TaskSession session, Task... tasks) {
	synchronized (lock) {
	  this.sessions.add(session);
	}

	fireSubmitEvents(session, tasks);

	for (Task task : tasks) {
	  Application.getTaskManager().fireTaskExecutionUIs(getTaskExecutor(),
		  TaskManagerEventType.SESSION_CREATED,
		  new TaskExecutionUIEvent(getExecutionType(), session));
	  this.executor.submit(new TaskWrapper(task, session));
	}
  };

  @Override
  public boolean hasTask() {
	synchronized (lock) {
	  return (this.sessions.size() > 0);
	}
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
	  super(0, Integer.MAX_VALUE, 0L, TimeUnit.SECONDS,
		  new SynchronousQueue<Runnable>());
	}

	@Override
	protected final <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
	  TaskWrapper taskWrapper = (TaskWrapper) runnable;
	  return new FutureTaskWrapper<T>(taskWrapper, value);
	}

	@Override
	protected final void afterExecute(Runnable r, Throwable t) {
	  synchronized (lock) {
		FutureTaskWrapper<?> fw = (FutureTaskWrapper<?>) r;
		TaskSession session = fw.getTaskWrapper().getTaskSession();
		DefaultParallelExecutor.this.sessions.remove(session);
		if (DefaultParallelExecutor.this.sessions.size() == 0) {
		  Application.getMessageManager().publish(false,
			  new ExecutorPerformedMessage());
		}

		Application.getTaskManager().fireTaskExecutionUIs(getTaskExecutor(),
			TaskManagerEventType.SESSION_FINALIZED,
			new TaskExecutionUIEvent(getExecutionType(), session));
	  }
	}

  }
}
