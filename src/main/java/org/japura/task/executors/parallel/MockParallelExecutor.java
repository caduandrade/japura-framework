package org.japura.task.executors.parallel;

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
public class MockParallelExecutor extends AbstractExecutor implements
	ParallelExecutor{

  private int count;

  public MockParallelExecutor(TaskExecutor taskExecutor) {
	super(taskExecutor);
  }

  @Override
  public ExecutionType getExecutionType() {
	return ExecutionType.PARALLEL;
  }

  @Override
  public boolean hasTask() {
	return (count > 0);
  }

  @Override
  public void cancel() {}

  @Override
  public void submitTasks(TaskSession session, Task... tasks) {
	this.count = tasks.length;

	fireSubmitEvents(session, tasks);

	for (Task task : tasks) {
	  Application.getTaskManager().fireTaskExecutionUIs(getTaskExecutor(),
		  TaskManagerEventType.SESSION_CREATED,
		  new TaskExecutionUIEvent(getExecutionType(), session));
	  TaskWrapper tw = new TaskWrapper(task, session);
	  tw.run();
	  count--;
	}

	if (count == 0) {
	  Application.getMessageManager().publish(false,
		  new ExecutorPerformedMessage());
	}
	Application.getTaskManager().fireTaskExecutionUIs(getTaskExecutor(),
		TaskManagerEventType.SESSION_FINALIZED,
		new TaskExecutionUIEvent(getExecutionType(), session));
  }

  @Override
  public void shutdown() {}

}
