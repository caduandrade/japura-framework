package org.japura.task.ui;

import org.japura.task.Task;
import org.japura.task.TaskExeception;
import org.japura.task.TaskStatus;
import org.japura.task.executors.ExecutionType;
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
public class TaskExecutionUIEvent{

  private TaskSession session;
  private String taskMessage;
  private TaskStatus taskStatus;
  private Class<? extends Task> taskClass;
  private ExecutionType executionType;

  public TaskExecutionUIEvent(ExecutionType executionType, TaskSession session) {
	this(executionType, session, null);
  }

  public TaskExecutionUIEvent(ExecutionType executionType, TaskSession session,
	  Task task) {
	if (session == null) {
	  throw new TaskExeception(TaskSession.class.getSimpleName() + " NULL");
	}
	if (executionType == null) {
	  throw new TaskExeception(ExecutionType.class.getSimpleName() + " NULL");
	}
	this.session = session;
	this.executionType = executionType;
	if (task != null) {
	  this.taskMessage = task.getMessage();
	  this.taskStatus = task.getStatus();
	  this.taskClass = task.getClass();
	}
  }

  public ExecutionType getExecutionType() {
	return executionType;
  }

  public TaskSession getSession() {
	return session;
  }

  public Class<? extends Task> getTaskClass() {
	return taskClass;
  }

  public String getTaskMessage() {
	return taskMessage;
  }

  public TaskStatus getTaskStatus() {
	return taskStatus;
  }

}
