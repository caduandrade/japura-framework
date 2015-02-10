package org.japura.task.session;

import java.util.Collection;

import org.japura.Application;
import org.japura.Session;
import org.japura.task.TaskExeception;
import org.japura.task.executors.ExecutionType;
import org.japura.task.executors.TaskExecutor;
import org.japura.task.manager.TaskManagerEventType;
import org.japura.task.ui.TaskExecutionUIEvent;
import org.japura.util.info.InfoNode;

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
public class TaskSession extends Session{

  private TaskSessionListener listener;
  private TaskExecutor taskExecutor;
  private ExecutionType executionType;
  private boolean cancelEnabled;
  private boolean canceled;

  public TaskSession(TaskSessionListener listener) {
	this.listener = listener;
  }

  public ExecutionType getExecutionType() {
	return executionType;
  }

  /**
   * Registers the task executor. DO NOT USE THIS METHOD UNLESS YOU ARE
   * DEVELOPING A NEW TASK EXECUTOR.
   * 
   * @param taskExecutor
   *          the executor
   */
  public final void registerTaskExecutor(TaskExecutor taskExecutor) {
	if (taskExecutor == null) {
	  throw new TaskExeception(TaskExecutor.class.getSimpleName() + "  NULL");
	}
	this.taskExecutor = taskExecutor;
  }

  /**
   * Registers the executor type. DO NOT USE THIS METHOD UNLESS YOU ARE
   * DEVELOPING A NEW TASK EXECUTOR.
   * 
   * @param executionType
   *          the executor type
   */
  public final void registerExecutionType(ExecutionType executionType) {
	if (executionType == null) {
	  throw new TaskExeception(ExecutionType.class.getSimpleName() + " NULL");
	}
	this.executionType = executionType;
  }

  public TaskExecutor getTaskExecutor() {
	return taskExecutor;
  }

  public boolean isCancelEnabled() {
	return this.cancelEnabled;
  }

  public void setCancelEnabled(boolean cancelEnabled) {
	this.cancelEnabled = cancelEnabled;
	Application.getTaskManager().fireTaskExecutionUIs(getTaskExecutor(),
		TaskManagerEventType.CANCEL_PERMISSION_CHANGED,
		new TaskExecutionUIEvent(getExecutionType(), this));
  }

  public boolean isCanceled() {
	return this.canceled;
  }

  public void cancel() {
	if (isCancelEnabled()) {
	  this.canceled = true;
	  if (this.listener != null) {
		this.listener.sessionCanceled(this);
	  }
	}
  }

  public void forceCancel() {
	this.canceled = true;
	if (this.listener != null) {
	  this.listener.sessionCanceled(this);
	}
  }

  @Override
  protected void addExtraInfoNodes(Collection<InfoNode> nodes) {
	nodes
		.add(new InfoNode("cancelEnabled", Boolean.toString(isCancelEnabled())));
	nodes.add(new InfoNode("canceled", Boolean.toString(isCanceled())));
  }

}
