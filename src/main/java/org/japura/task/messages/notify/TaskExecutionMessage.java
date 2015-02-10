package org.japura.task.messages.notify;

import java.util.Collection;

import org.japura.controller.Context;
import org.japura.controller.GroupTaskExecutor;
import org.japura.message.Message;
import org.japura.task.Task;
import org.japura.task.executors.TaskExecutor;
import org.japura.task.session.TaskSession;
import org.japura.util.info.InfoNode;

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
public final class TaskExecutionMessage extends Message{

  private boolean parallel;
  private String sessionId;
  private Collection<InfoNode> sessionInfoNodes;
  private Collection<InfoNode> contextInfoNodes;
  private Task task;
  private String previousExecutedTaskId;

  public TaskExecutionMessage(boolean parallel, TaskSession session, Task task,
	  String previousExecutedTaskId) {
	this.parallel = parallel;
	this.sessionId = session.getId();
	this.sessionInfoNodes = session.getInfoNodes();
	this.task = task;
	this.previousExecutedTaskId = previousExecutedTaskId;
	TaskExecutor executor = task.getTaskExecutor();
	if (executor instanceof GroupTaskExecutor) {
	  GroupTaskExecutor groupTaskExecutor = (GroupTaskExecutor) executor;
	  Context context = groupTaskExecutor.getGroup().getContext();
	  this.contextInfoNodes = context.getInfoNodes();
	}
  }

  public boolean isParallel() {
	return parallel;
  }

  public Collection<InfoNode> getSessionInfoNodes() {
	return sessionInfoNodes;
  }

  public Collection<InfoNode> getContextInfoNodes() {
	return contextInfoNodes;
  }

  public String getSessionId() {
	return sessionId;
  }

  public Task getTask() {
	return task;
  }

  public String getPreviousExecutedTaskId() {
	return previousExecutedTaskId;
  }

}
