package org.japura.task.executors;

import org.japura.task.Task;
import org.japura.task.session.TaskSession;
import org.japura.task.session.TaskSessionListener;

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
public interface TaskSubmitter{

  /**
   * Submits one or more tasks. A new {@link TaskSession} will be created and
   * shared with the given tasks. The tasks will be executed in a FIFO order.
   * 
   * @param tasks
   *          The tasks.
   * @return the new {@link TaskSession}
   */
  public TaskSession submitTask(Task... tasks);

  /**
   * Submits one or more tasks. A new {@link TaskSession} will be created and
   * shared with the given tasks. The tasks will be executed in a FIFO order.
   * 
   * @param listener
   *          an optional listener for the session
   * @param tasks
   *          The tasks.
   * @return the new {@link TaskSession}
   */
  public TaskSession submitTask(TaskSessionListener listener, Task... tasks);

  /**
   * Submits one or more tasks. A new {@link TaskSession} will be created and
   * shared with the given tasks.
   * 
   * @param isParallel
   *          Defines whether the tasks are executed on a parallel execution.
   * @param tasks
   *          The tasks.
   * @return the new {@link TaskSession}
   */
  public TaskSession submitTask(boolean isParallel, Task... tasks);

  /**
   * Submits one or more tasks. A new {@link TaskSession} will be created and
   * shared with the given tasks.
   * 
   * @param isParallel
   *          Defines whether the tasks are executed on a parallel execution.
   * @param listener
   *          an optional listener for the session
   * @param tasks
   *          The tasks.
   * @return the new {@link TaskSession}
   */
  public TaskSession submitTask(boolean isParallel,
								TaskSessionListener listener, Task... tasks);

  public String getTaskSubmitterId();

}
