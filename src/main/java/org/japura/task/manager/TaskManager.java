package org.japura.task.manager;

import org.japura.task.executors.TaskExecutor;
import org.japura.task.executors.parallel.ParallelExecutor;
import org.japura.task.executors.serial.SerialExecutor;
import org.japura.task.session.TaskSessionFactory;
import org.japura.task.ui.TaskExecutionUIEvent;
import org.japura.task.ui.TaskExecutionUIFactory;

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
public interface TaskManager{

  public void setNotifyMessagesEnabled(boolean enabled);

  public boolean isNotifyMessagesEnabled();

  public void fireTaskExecutionUIs(TaskExecutor taskExecutor,
								   TaskManagerEventType type,
								   TaskExecutionUIEvent event);

  public boolean hasTask();

  public boolean hasTask(Object taskExecutorOwner);

  public void setTaskSessionFactory(TaskSessionFactory taskSessionFactory);

  public TaskSessionFactory getTaskSessionFactory();

  // TODO expurgeExecutors

  public TaskExecutor getGlobalExecutor();

  public boolean invokeTaskMethodsOnEDT();

  public void register(TaskExecutor taskExecutor);

  public TaskExecutor buildTaskExecutor();

  public ParallelExecutor buildParallelExecutor(TaskExecutor taskExecutor);

  public SerialExecutor buildSerialExecutor(TaskExecutor taskExecutor);

  public TaskExecutionUIFactory getTaskExecutionUIFactory();

  public void setTaskExecutionUIFactory(TaskExecutionUIFactory factory);
}
