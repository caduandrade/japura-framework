package org.japura.task;

import java.util.ArrayList;
import java.util.Collection;

/**
 * <P>
 * Copyright (C) 2012-2013 Carlos Eduardo Leite de Andrade
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
public class TaskSequence{

  private Collection<Task> tasks = new ArrayList<Task>();

  public void add(Task... tasks) {
	for (Task task : tasks) {
	  if (this.tasks.contains(task) == false) {
		this.tasks.add(task);
	  }
	}
  }

  public void add(Collection<Task> tasks) {
	for (Task task : tasks) {
	  if (this.tasks.contains(task) == false) {
		this.tasks.add(task);
	  }
	}
  }

  public void add(TaskSequence taskSequence) {
	add(taskSequence.getTasks());
  }

  public Task[] getTasks() {
	return tasks.toArray(new Task[0]);
  }

}
