package org.japura.task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.japura.Application;
import org.japura.task.executors.ExecutionType;
import org.japura.task.executors.TaskExecutor;
import org.japura.task.executors.TaskSubmitter;
import org.japura.task.session.TaskSession;
import org.japura.util.info.IdentifierNode;
import org.japura.util.info.InfoNode;
import org.japura.util.info.InfoProvider;

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
public class Task implements InfoProvider{

  private String id;
  private String parentId;
  private int nestedLevel;
  private TaskExecutor taskExecutor;
  private Exception exception;
  private String name;
  private String description;
  private String message;
  private boolean waitForEDT = false;
  private long backgroundTimeSpent;
  private TaskStatus status;
  private ExecutionType executionType;
  private Map<Class<? extends TaskSubmitter>, String> taskSubmitters;

  public Task() {
	this(null);
  }

  public Task(String name) {
	this.id = Application.buildId();
	this.status = TaskStatus.PENDING;
	if (name == null) {
	  name = "";
	}
	setName(name);

	TaskDescription taskDescription =
		getClass().getAnnotation(TaskDescription.class);
	if (taskDescription != null) {
	  this.description = taskDescription.description().trim();
	} else {
	  this.description = "";
	}

	this.taskSubmitters = new HashMap<Class<? extends TaskSubmitter>, String>();
  }

  public String getDescription() {
	return description;
  }

  private void clear() {
	this.exception = null;
	this.backgroundTimeSpent = 0;
  }

  public final TaskExecutor getTaskExecutor() {
	return taskExecutor;
  }

  public String getParentId() {
	return parentId;
  }

  private void setParentId(String parentId) {
	this.parentId = parentId;
  }

  public long getBackgroundTimeSpent() {
	return backgroundTimeSpent;
  }

  public final String getId() {
	return id;
  }

  public String getName() {
	return name;
  }

  public void setName(String name) {
	this.name = name;
  }

  public int getNestedLevel() {
	return nestedLevel;
  }

  private void setNestedLevel(int nestedLevel) {
	this.nestedLevel = nestedLevel;
  }

  public String getMessage() {
	return message;
  }

  public void setMessage(String message) {
	this.message = message;
  }

  public boolean isWaitForEDT() {
	return waitForEDT;
  }

  public void setWaitForEDT(boolean waitForEDT) {
	this.waitForEDT = waitForEDT;
  }

  public void initializeSession(TaskSession session) {}

  public void submitted(TaskSession session) {}

  public void willExecute(TaskSession session) throws Exception {}

  /**
   * Method to be executed in a background thread.
   * 
   * @param session
   *          the session
   * @throws Exception
   *           if an exception occurs
   * @see #handleException(TaskSession, Exception, Collection)
   */
  public void doInBackground(TaskSession session) throws Exception {}

  public void done(TaskSession session) {}

  public void canceled(TaskSession session) {}

  public void handleException(TaskSession session, Exception e,
							  Collection<Task> discardedTasks) {
	Application.getHandlerExceptionManager().handle(getException(),
		getTaskExecutor().getHandlerExceptionParameters(this));
  }

  public Exception getException() {
	return exception;
  }

  public final TaskStatus getStatus() {
	return status;
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
	  throw new TaskExeception("TaskExecutor can't be NULL");
	}
	this.taskExecutor = taskExecutor;
  }

  /**
   * Registers the task submitter. DO NOT USE THIS METHOD UNLESS YOU ARE
   * DEVELOPING A NEW TASK SUBMITTER.
   * 
   * @param taskSubmitter
   *          the submitter
   */
  public final void registerTaskSubmitter(TaskSubmitter taskSubmitter) {
	if (taskSubmitter == null) {
	  throw new TaskExeception("TaskSubmitter can't be NULL");
	}
	this.taskSubmitters.put(taskSubmitter.getClass(),
		taskSubmitter.getTaskSubmitterId());
  }

  public Map<Class<? extends TaskSubmitter>, String> getTaskSubmitters() {
	return Collections.unmodifiableMap(taskSubmitters);
  }

  public String getTaskSubmitterId(Class<? extends TaskSubmitter> taskSubmitterClass) {
	return taskSubmitters.get(taskSubmitterClass);
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

  /**
   * Registers the time spent on {@link #doInBackground(TaskSession)} method. DO
   * NOT USE THIS METHOD UNLESS YOU ARE DEVELOPING A NEW TASK EXECUTOR.
   * 
   * @param backgroundTimeSpent
   *          the time spent
   */
  public final void registerBackgroundTimeSpent(long backgroundTimeSpent) {
	this.backgroundTimeSpent = backgroundTimeSpent;
  }

  /**
   * Registers a new status. DO NOT USE THIS METHOD UNLESS YOU ARE DEVELOPING A
   * NEW TASK EXECUTOR.
   * 
   * @param status
   *          the new status
   */
  public final void registerStatus(TaskStatus status) {
	TaskStatus.validate(this.status, status);
	this.status = status;
	if (status.equals(TaskStatus.SUBMITTED)) {
	  clear();
	}
  }

  /**
   * Registers an exception. DO NOT USE THIS METHOD UNLESS YOU ARE DEVELOPING A
   * NEW TASK EXECUTOR.
   * 
   * @param exception
   *          the exception
   */
  public final void registerException(Exception exception) {
	this.exception = exception;
	registerStatus(TaskStatus.ERROR);
  }

  public final void submitNestedTask(Task... tasks) {
	// TODO f
	if (getStatus().equals(TaskStatus.EXECUTING) == false) {
	  throw new TaskExeception("");
	}

	// validating
	for (Task task : tasks) {
	  TaskStatus status = task.getStatus();
	  switch (status) {
		case DONE:
		  // OK
		  break;
		case ERROR:
		  // OK
		  break;
		case PENDING:
		  // OK
		  break;
		case CANCELED:
		  // OK
		  break;
		case EXECUTING:
		  throw new TaskExeception("Invalid status for nested task: " + status);
		case SUBMITTED:
		  throw new TaskExeception("Invalid status for nested task: " + status);
		  // /CLOVER:OFF
		default:
		  throw new TaskExeception("Status not supported: " + status);
		  // /CLOVER:ON
	  }
	}

	for (Task task : tasks) {
	  task.setParentId(getId());
	  task.setNestedLevel(getNestedLevel() + 1);
	  if (task.getMessage() == null) {
		task.setMessage(getMessage());
	  }
	}
	if (getExecutionType().equals(ExecutionType.PARALLEL)) {
	  throw new TaskExeception("The task " + getClass().getName()
		  + " can not submit a nested task in a parallel execution.");
	} else {
	  getTaskExecutor().submitNestedTask(tasks);
	}
  }

  @Override
  public int hashCode() {
	return id.hashCode();
  }

  protected void retry(Collection<Task> discardedTasks) {
	retry(false, discardedTasks);
  }

  protected void retry(boolean isParallel, Collection<Task> discardedTasks) {
	TaskSequence ts = new TaskSequence();
	ts.add(this);
	ts.add(discardedTasks);
	getTaskExecutor().submitTask(isParallel, ts.getTasks());
  }

  public final ExecutionType getExecutionType() {
	return executionType;
  }

  @Override
  public boolean equals(Object obj) {
	if (obj == null)
	  return false;
	if (getClass() != obj.getClass())
	  return false;
	Task other = (Task) obj;
	if (!id.equals(other.id))
	  return false;
	return true;
  }

  @Override
  public String toString() {
	return getClass().getName() + "[id:" + getId() + "]";
  }

  @Override
  public Collection<IdentifierNode> getIdentifierNodes() {
	return new ArrayList<IdentifierNode>();
  }

  @Override
  public Collection<InfoNode> getInfoNodes() {
	Collection<InfoNode> nodes = new ArrayList<InfoNode>();
	nodes.add(new InfoNode("Package", getClass().getPackage().getName()));

	String packageName = getClass().getPackage().getName();
	if (packageName.length() > 0) {
	  packageName += ".";
	}
	String className = getClass().getName();
	String taskClass =
		className.substring(packageName.length(), className.length());

	nodes.add(new InfoNode("Class", taskClass));
	nodes.add(new InfoNode("Id", this.id));
	nodes.add(new InfoNode("Name", getName()));
	nodes.add(new InfoNode("Description", getDescription()));
	return nodes;
  }

}
