package org.japura.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.japura.InfoNodeIdentifiers;
import org.japura.exception.HandlerExceptionParameters;
import org.japura.task.Task;
import org.japura.task.executors.AbstractTaskExecutor;
import org.japura.util.info.InfoNode;

/**
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
 * 
 */
public class GroupTaskExecutor extends AbstractTaskExecutor{

  private Group group;

  public GroupTaskExecutor(Group group) {
	super(group);
	this.group = group;
	initialize();
  }

  @Override
  public Collection<InfoNode> getInfoNodes() {
	Collection<InfoNode> nodes = new ArrayList<InfoNode>();
	nodes.add(new InfoNode("Package", getClass().getPackage().getName()));
	nodes.add(new InfoNode("Class", getClass().getSimpleName()));
	nodes.add(new InfoNode("Group Id", getGroup().getId()));
	nodes.add(new InfoNode("Group Name", getGroup().getName()));

	Controller root = getGroup().getRootController();
	if (root != null) {
	  nodes.add(new InfoNode(InfoNodeIdentifiers.GROUP_ROOT_CONTROLLER.name(),
		  "Root Controller", root.getInfoNodes()));
	} else {
	  nodes.add(new InfoNode(InfoNodeIdentifiers.GROUP_ROOT_CONTROLLER.name(),
		  "Root Controller", "null"));
	}

	nodes.add(new InfoNode("Group Session", getGroup().getModel()
		.getInfoNodes()));

	return nodes;
  }

  @Override
  public HashMap<String, Object> getHandlerExceptionParameters(Task task) {
	HashMap<String, Object> parameters = new HashMap<String, Object>();
	parameters.put(HandlerExceptionParameters.CONTROLLER_GROUP_ID, getGroup()
		.getId());

	String controllerId = getGroup().getRootController().getControllerId();
	parameters.put(HandlerExceptionParameters.ROOT_CONTROLLER_ID, controllerId);

	return parameters;
  }

  public Group getGroup() {
	return group;
  }

  @Override
  public String getTaskSubmitterId() {
	return this.group.getId();
  }

}
