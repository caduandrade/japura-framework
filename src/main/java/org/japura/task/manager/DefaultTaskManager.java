package org.japura.task.manager;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.SwingUtilities;

import org.japura.Application;
import org.japura.task.TaskExeception;
import org.japura.task.executors.DefaultTaskExecutor;
import org.japura.task.executors.TaskExecutor;
import org.japura.task.executors.parallel.DefaultParallelExecutor;
import org.japura.task.executors.parallel.ParallelExecutor;
import org.japura.task.executors.serial.DefaultSerialExecutor;
import org.japura.task.executors.serial.SerialExecutor;
import org.japura.task.session.DefaultTaskSessionFactory;
import org.japura.task.session.TaskSessionFactory;
import org.japura.task.ui.TaskExecutionUI;
import org.japura.task.ui.TaskExecutionUIEvent;
import org.japura.task.ui.TaskExecutionUIFactory;

/**
 * <P>
 * Copyright (C) 2012-2014 Carlos Eduardo Leite de Andrade
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
public class DefaultTaskManager implements TaskManager{

  private TaskExecutionUIFactory taskExecutionUIFactory;
  private TaskSessionFactory taskSessionFactory =
	  new DefaultTaskSessionFactory();
  private boolean invokeTaskMethodsOnEDT = true;
  private ReentrantLock lock = new ReentrantLock();
  private TaskExecutor globalTaskSubmitter;
  private Collection<TaskExecutor> taskExecutors;
  private boolean notifyMessagesEnabled;

  public DefaultTaskManager() {
	this.taskExecutors = new HashSet<TaskExecutor>();
  }

  @Override
  public boolean isNotifyMessagesEnabled() {
	return notifyMessagesEnabled;
  }

  @Override
  public void setNotifyMessagesEnabled(boolean enabled) {
	this.notifyMessagesEnabled = enabled;
  }

  @Override
  public void fireTaskExecutionUIs(TaskExecutor taskExecutor,
								   final TaskManagerEventType type,
								   final TaskExecutionUIEvent event) {
	if (type == null) {
	  return;
	}

	final TaskExecutionUI taskExecutionUI = taskExecutor.getTaskExecutionUI();

	if (taskExecutionUI != null) {
	  SwingUtilities.invokeLater(new Runnable() {
		@Override
		public void run() {
		  try {
			if (TaskManagerEventType.BEFORE_TASK_EXECUTION.equals(type)) {
			  taskExecutionUI.beforeTaskExecution(event);
			} else if (TaskManagerEventType.AFTER_TASK_EXECUTION.equals(type)) {
			  taskExecutionUI.afterTaskExecution(event);
			} else if (TaskManagerEventType.SESSION_FINALIZED.equals(type)) {
			  taskExecutionUI.sessionFinalized(event);
			} else if (TaskManagerEventType.SESSION_CREATED.equals(type)) {
			  taskExecutionUI.sessionCreated(event);
			} else if (TaskManagerEventType.CANCEL_PERMISSION_CHANGED
				.equals(type)) {
			  taskExecutionUI.sessionCancelPermissionChanged(event);
			} else {
			  throw new TaskExeception("Unsupported "
				  + TaskManagerEventType.class.getSimpleName() + " :"
				  + type.name());
			}
		  } catch (Exception e) {
			Application.getHandlerExceptionManager().handle(
				new TaskExeception(e));
		  }
		}
	  });

	}
  }

  @Override
  public void setTaskSessionFactory(TaskSessionFactory taskSessionFactory) {
	if (taskSessionFactory == null) {
	  taskSessionFactory = new DefaultTaskSessionFactory();
	}
	this.taskSessionFactory = taskSessionFactory;
  }

  @Override
  public TaskSessionFactory getTaskSessionFactory() {
	return taskSessionFactory;
  }

  // TODO impedir que o global shutdow
  @Override
  public void register(TaskExecutor taskExecutor) {
	lock.lock();
	try {
	  Iterator<TaskExecutor> it = taskExecutors.iterator();
	  while (it.hasNext()) {
		TaskExecutor e = it.next();
		if (e.isShutdown() && e.hasTask() == false) {
		  it.remove();
		}
	  }

	  if (taskExecutors.contains(taskExecutor) == false) {
		taskExecutors.add(taskExecutor);
	  }
	} finally {
	  lock.unlock();
	}
  }

  @Override
  public boolean hasTask() {
	lock.lock();
	try {
	  for (TaskExecutor executor : taskExecutors) {
		if (executor.hasTask()) {
		  return true;
		}
	  }
	  return false;
	} finally {
	  lock.unlock();
	}
  }

  @Override
  public boolean hasTask(Object taskExecutorOwner) {
	lock.lock();
	try {
	  for (TaskExecutor executor : taskExecutors) {
		if (taskExecutorOwner == null) {
		  if (executor.getOwner() == null && executor.hasTask()) {
			return true;
		  }
		} else {
		  Object owner = executor.getOwner();
		  if (owner != null && owner.equals(taskExecutorOwner)
			  && executor.hasTask()) {
			return true;
		  }
		}
	  }
	  return false;
	} finally {
	  lock.unlock();
	}
  }

  @Override
  public TaskExecutor getGlobalExecutor() {
	if (this.globalTaskSubmitter == null) {
	  this.globalTaskSubmitter = new GlobalTaskExecutor();
	}
	return this.globalTaskSubmitter;
  }

  @Override
  public TaskExecutor buildTaskExecutor() {
	DefaultTaskExecutor executor = new DefaultTaskExecutor();
	return executor;
  }

  public void setInvokeTaskMethodsOnEDT(boolean invokeTaskMethodsOnEDT) {
	this.invokeTaskMethodsOnEDT = invokeTaskMethodsOnEDT;
  }

  @Override
  public boolean invokeTaskMethodsOnEDT() {
	return invokeTaskMethodsOnEDT;
  }

  @Override
  public TaskExecutionUIFactory getTaskExecutionUIFactory() {
	return this.taskExecutionUIFactory;
  }

  @Override
  public void setTaskExecutionUIFactory(TaskExecutionUIFactory factory) {
	this.taskExecutionUIFactory = factory;
  }

  @Override
  public ParallelExecutor buildParallelExecutor(TaskExecutor taskExecutor) {
	return new DefaultParallelExecutor(taskExecutor);
  }

  @Override
  public SerialExecutor buildSerialExecutor(TaskExecutor taskExecutor) {
	return new DefaultSerialExecutor(taskExecutor);
  }

  private static class GlobalTaskExecutor extends DefaultTaskExecutor{

	@Override
	public String toString() {
	  return getClass().getSimpleName();
	}

  }

}
