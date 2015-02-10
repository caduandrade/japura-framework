package org.japura.task.manager;

import org.japura.task.executors.DefaultTaskExecutor;
import org.japura.task.executors.TaskExecutor;
import org.japura.task.executors.parallel.MockParallelExecutor;
import org.japura.task.executors.parallel.ParallelExecutor;
import org.japura.task.executors.serial.MockSerialExecutor;
import org.japura.task.executors.serial.SerialExecutor;

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
public class MockTaskManager extends DefaultTaskManager{

  @Override
  public ParallelExecutor buildParallelExecutor(TaskExecutor taskExecutor) {
	return new MockParallelExecutor(taskExecutor);
  }

  @Override
  public SerialExecutor buildSerialExecutor(TaskExecutor taskExecutor) {
	return new MockSerialExecutor(taskExecutor);
  }

  @Override
  public TaskExecutor buildTaskExecutor() {
	return new DefaultTaskExecutor();
  }

  @Override
  public boolean invokeTaskMethodsOnEDT() {
	return false;
  }

}
