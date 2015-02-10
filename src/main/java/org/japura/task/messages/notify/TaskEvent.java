package org.japura.task.messages.notify;

import org.japura.task.Task;
import org.japura.task.executors.TaskExecutor;
import org.japura.task.session.TaskSession;

/**
 * <P>
 * Copyright (C) 2011-2013 Carlos Eduardo Leite de Andrade
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
public class TaskEvent{
  private String taskClass;
  private String taskId;
  private String name;
  private String source;
  private TaskEventType eventType;
  private long time;
  private boolean edt;
  private boolean waitForEDT;
  private long timeSpent;
  private String session;

  public TaskEvent(Task task, TaskSession session, TaskEventType eventType,
	  long time, boolean edt) {
	this.taskId = task.getId();
	this.name = task.getName();
	this.time = time;
	this.eventType = eventType;
	this.edt = edt;
	this.waitForEDT = task.isWaitForEDT();
	this.timeSpent = task.getBackgroundTimeSpent();
	this.taskClass = task.getClass().getName();

	TaskExecutor owner = task.getTaskExecutor();
	if (owner == null) {
	  this.source = "NULL";
	} else {
	  this.source = owner.toString();
	}

	if (session == null) {
	  this.session = "NULL";
	} else {
	  this.session =
		  session.getClass().getName() + " [" + session.getId() + "]";
	}
  }

  public String getSource() {
	return source;
  }

  public boolean isEdt() {
	return edt;
  }

  public String getName() {
	return name;
  }

  public String getTaskId() {
	return taskId;
  }

  public String getTaskClass() {
	return taskClass;
  }

  public TaskEventType getEventType() {
	return eventType;
  }

  public boolean isWaitForEDT() {
	return waitForEDT;
  }

  public long getStartTime() {
	return time;
  }

  public void setTimeSpent(long timeSpent) {
	this.timeSpent = timeSpent;
  }

  public String getTimeSpent() {
	if (timeSpent < 1000) {
	  return Long.toString(timeSpent) + "ms";
	}

	long ms = timeSpent % 1000;
	long s = timeSpent / 1000;
	long m = timeSpent / 60000;
	long h = timeSpent / 3600000;

	StringBuilder sb = new StringBuilder();
	if (h > 0) {
	  sb.append(h);
	  sb.append("h");
	}
	if (m > 0) {
	  if (sb.length() > 0) {
		sb.append(" ");
	  }
	  sb.append(m);
	  sb.append("m");
	}
	if (s > 0) {
	  if (sb.length() > 0) {
		sb.append(" ");
	  }
	  sb.append(s);
	  sb.append("s");
	}
	if (ms > 0) {
	  if (sb.length() > 0) {
		sb.append(" ");
	  }
	  sb.append(ms);
	  sb.append("ms");
	}

	return sb.toString();
  }

  public String getSession() {
	return session;
  }
}
