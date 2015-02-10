package org.japura.controller;

import java.util.ArrayList;
import java.util.Collection;

import org.japura.Application;
import org.japura.task.Task;
import org.japura.task.executors.TaskExecutor;
import org.japura.task.session.TaskSession;
import org.japura.util.info.IdentifierNode;
import org.japura.util.info.InfoNode;
import org.japura.util.info.InfoProvider;

/**
 * <P>
 * Copyright (C) 2014 Carlos Eduardo Leite de Andrade
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
public class GroupModel implements InfoProvider{

  private String id;
  private Group group;

  public GroupModel() {
	this.id = Application.buildId();
  }

  public final String getId() {
	return this.id;
  }

  void setGroup(Group group) {
	this.group = group;
  }

  public final Group getGroup() {
	return this.group;
  }

  @Override
  public Collection<InfoNode> getInfoNodes() {
	Collection<InfoNode> nodes = new ArrayList<InfoNode>();
	nodes.add(new InfoNode("Id", getId()));
	nodes.add(new InfoNode("Class", getClass().getName()));
	return nodes;
  }

  @Override
  public Collection<IdentifierNode> getIdentifierNodes() {
	return new ArrayList<IdentifierNode>();
  }

  @Override
  public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((id == null) ? 0 : id.hashCode());
	return result;
  }

  @Override
  public boolean equals(Object obj) {
	if (this == obj)
	  return true;
	if (obj == null)
	  return false;
	if (getClass() != obj.getClass())
	  return false;
	GroupModel other = (GroupModel) obj;
	if (id == null) {
	  if (other.id != null)
		return false;
	} else if (!id.equals(other.id))
	  return false;
	return true;
  }

  public static GroupModel getModel(Task task) {
	return GroupModel.getModel(task.getTaskExecutor());
  }

  public static GroupModel getModel(TaskSession session) {
	return GroupModel.getModel(session.getTaskExecutor());
  }

  public static GroupModel getModel(TaskExecutor taskExecutor) {
	if (taskExecutor != null && taskExecutor instanceof GroupTaskExecutor) {
	  GroupTaskExecutor groupTaskExecutor = (GroupTaskExecutor) taskExecutor;
	  Group group = groupTaskExecutor.getGroup();
	  return group.getModel();
	}
	return null;
  }
}
