package org.japura.task.executors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.japura.Application;
import org.japura.task.Task;
import org.japura.task.TaskExeception;
import org.japura.task.TaskStatus;
import org.japura.task.executors.parallel.ParallelExecutor;
import org.japura.task.executors.serial.SerialExecutor;
import org.japura.task.session.TaskSession;
import org.japura.task.session.TaskSessionListener;
import org.japura.task.ui.TaskExecutionUI;
import org.japura.task.ui.TaskExecutionUIFactory;
import org.japura.util.info.IdentifierNode;
import org.japura.util.info.InfoNode;

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
public abstract class AbstractTaskExecutor implements TaskExecutor{

  private String id;
  private Object owner;
  private boolean shutdown;
  private SerialExecutor serialExecutor;
  private ParallelExecutor parallelExecutor;
  private TaskExecutionUI taskExecutionUI;

  public AbstractTaskExecutor(Object owner) {
	this.owner = owner;
	this.id = Application.buildId();
	Application.getTaskManager().register(this);
  }

  protected void initialize() {
	this.serialExecutor =
		Application.getTaskManager().buildSerialExecutor(this);
	this.parallelExecutor =
		Application.getTaskManager().buildParallelExecutor(this);
	updateTaskExecutionUI();
  }

  public String getId() {
	return id;
  }

  @Override
  public Object getOwner() {
	return this.owner;
  }

  @Override
  public void cancel() {
	this.serialExecutor.cancel();
	this.parallelExecutor.cancel();
  }

  private void updateTaskExecutionUI() {
	TaskExecutionUIFactory factory =
		Application.getTaskManager().getTaskExecutionUIFactory();
	if (factory != null) {
	  this.taskExecutionUI = factory.buildTaskExecutionUI(this);
	}
  }

  @Override
  public final boolean hasTask() {
	return serialExecutor.hasTask() || parallelExecutor.hasTask();
  }

  @Override
  public boolean isShutdown() {
	return shutdown;
  }

  @Override
  public void shutdown() {
	this.shutdown = true;
	this.serialExecutor.shutdown();
	this.parallelExecutor.shutdown();
  }

  @Override
  public TaskSession submitTask(Task... tasks) {
	return submitTask(null, tasks);
  }

  @Override
  public TaskSession submitTask(TaskSessionListener listener, Task... tasks) {
	return submit(Type.SERIAL, listener, null, tasks);
  }

  @Override
  public TaskSession submitTask(boolean isParallel, Task... tasks) {
	return submitTask(isParallel, null, tasks);
  }

  @Override
  public TaskSession submitTask(boolean isParallel,
								TaskSessionListener listener, Task... tasks) {
	Type type;
	if (isParallel) {
	  type = Type.PARALLEL;
	} else {
	  type = Type.SERIAL;
	}
	return submit(type, listener, null, tasks);
  }

  @Override
  public void submitNestedTask(Task... tasks) {
	for (Task task : tasks) {
	  if (task.getParentId() == null) {
		throw new TaskExeception("The nested task " + task.getClass().getName()
			+ " has not been submitted through a parent task.");
	  }
	}

	submit(Type.NESTED, null, null, tasks);
  }

  private TaskSession submit(Type type, TaskSessionListener listener,
							 TaskSession session, Task... tasks) {
	if (isShutdown()) {
	  return null;
	}

	for (Task task : tasks) {
	  if (task == null) {
		throw new TaskExeception("Task is NULL");
	  }

	  // TODO verificar outros status

	  if (task.getStatus().equals(TaskStatus.SUBMITTED)) {
		throw new TaskExeception("The task " + task.toString()
			+ " has already been submitted");
	  }
	  if (task.getStatus().equals(TaskStatus.EXECUTING)) {
		throw new TaskExeception("The task " + task.toString()
			+ " is executing");
	  }
	}

	if (type.equals(Type.NESTED)) {
	  long currentThreadId = Thread.currentThread().getId();

	  if (this.serialExecutor.getCurrentThreadIdExecution() == null
		  || this.serialExecutor.getCurrentThreadIdExecution().longValue() != currentThreadId) {
		// TODO msg
		throw new TaskExeception("");
	  }
	}

	if (type.equals(Type.NESTED)) {
	  session = this.serialExecutor.getCurrentTaskSession();
	} else {
	  session =
		  Application.getTaskManager().getTaskSessionFactory()
			  .buildTaskSession(session, listener, tasks);
	}

	if (session == null) {
	  throw new TaskExeception("TaskSession NULL");
	}

	session.registerTaskExecutor(this);

	for (Task task : tasks) {
	  task.registerTaskSubmitter(this);
	  task.registerTaskExecutor(this);
	  task.registerStatus(TaskStatus.SUBMITTED);
	  if (type.equals(Type.PARALLEL)) {
		session.registerExecutionType(ExecutionType.PARALLEL);
		task.registerExecutionType(ExecutionType.PARALLEL);
	  } else {
		session.registerExecutionType(ExecutionType.SERIAL);
		task.registerExecutionType(ExecutionType.SERIAL);
	  }
	}

	if (type.equals(Type.NESTED) == false) {
	  tasks[0].initializeSession(session);
	}

	if (type.equals(Type.PARALLEL)) {
	  this.parallelExecutor.submitTasks(session, tasks);
	} else if (type.equals(Type.NESTED)) {
	  this.serialExecutor.addNestedTasks(tasks);
	} else if (type.equals(Type.SERIAL)) {
	  this.serialExecutor.submitTasks(session, tasks);
	}

	return session;
  }

  @Override
  public HashMap<String, Object> getHandlerExceptionParameters(Task task) {
	return new HashMap<String, Object>();
  }

  @Override
  public String toString() {
	return getClass().getName();
  }

  @Override
  public TaskExecutionUI getTaskExecutionUI() {
	return this.taskExecutionUI;
  }

  @Override
  public void setTaskExecutionUI(TaskExecutionUI ui) {
	this.taskExecutionUI = ui;
  }

  @Override
  public Collection<IdentifierNode> getIdentifierNodes() {
	return new ArrayList<IdentifierNode>();
  }

  @Override
  public Collection<InfoNode> getInfoNodes() {
	Collection<InfoNode> nodes = new ArrayList<InfoNode>();
	nodes.add(new InfoNode("Package", getClass().getPackage().getName()));
	nodes.add(new InfoNode("Class", getClass().getSimpleName()));
	nodes.add(new InfoNode("Id", this.id));
	return nodes;
  }

  private static enum Type {
	NESTED,
	PARALLEL,
	SERIAL;
  }

}
