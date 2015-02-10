package org.japura.controller;

import java.util.ArrayList;
import java.util.Collection;

import org.japura.Application;
import org.japura.InfoNodeIdentifiers;
import org.japura.task.Task;
import org.japura.task.executors.TaskSubmitter;
import org.japura.task.session.TaskSession;
import org.japura.task.session.TaskSessionListener;
import org.japura.util.info.IdentifierNode;
import org.japura.util.info.InfoNode;
import org.japura.util.info.InfoProvider;

/**
 * Copyright (C) 2010-2014 Carlos Eduardo Leite de Andrade
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
public final class Group extends DefaultControllerCollection implements
	TaskSubmitter, InfoProvider{

  private final String id;
  private final String name;
  private final Context context;
  private final GroupModel model;
  private GroupTaskExecutor groupTaskExecutor;

  public Group(String name, Context context,
	  Class<? extends GroupModel> groupModelClass) {
	this.id = Application.buildId();
	this.context = context;
	if (name == null) {
	  name = "";
	}
	this.name = name.trim();

	try {
	  this.model = groupModelClass.newInstance();
	} catch (Exception e) {
	  throw new ControllerException("Error to create new instance of "
		  + groupModelClass.getName(), e);
	}

	this.model.setGroup(this);
	this.groupTaskExecutor = new GroupTaskExecutor(this);
  }

  public void rebuildGroupTaskExecutor() {
	this.groupTaskExecutor.shutdown();
	this.groupTaskExecutor = new GroupTaskExecutor(this);
  }

  public GroupTaskExecutor getGroupTaskExecutor() {
	return this.groupTaskExecutor;
  }

  public String getName() {
	return this.name;
  }

  public GroupModel getModel() {
	return this.model;
  }

  /**
   * Gets the context.
   * 
   * @return {@link Context}
   */
  public Context getContext() {
	return this.context;
  }

  public Controller getRootController() {
	if (count() == 0) {
	  return null;
	}
	return get(Controller.class).getRoot();
  }

  public String getId() {
	return id;
  }

  public <E> void freeAll(Class<E> clss) {
	Collection<E> controllers = getAll(clss);
	Controller[] array = new Controller[controllers.size()];
	int i = 0;
	for (E controller : controllers) {
	  array[i] = (Controller) controller;
	  i++;
	}
	Application.getControllerManager().free(array);
  }

  public void free() {
	Collection<Controller> controllers = getAll();
	Application.getControllerManager().free(
		controllers.toArray(new Controller[0]));
  }

  @Override
  public String toString() {
	return "[name: " + getName() + "] [id: " + getId() + "]";
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
	Group other = (Group) obj;
	if (id == null) {
	  if (other.id != null)
		return false;
	} else if (!id.equals(other.id))
	  return false;
	return true;
  }

  private void checkRegistered() {
	if (getRootController().getControllerStatus().equals(
		ControllerStatus.REGISTERED) == false) {
	  throw new ControllerException(
		  "It is not possible to submit new tasks. The root controller has been marked to be disposed.");
	}
  }

  @Override
  public TaskSession submitTask(Task... tasks) {
	return submitTask(null, tasks);
  }

  @Override
  public TaskSession submitTask(TaskSessionListener listener, Task... tasks) {
	checkRegistered();
	for (Task task : tasks) {
	  task.registerTaskSubmitter(this);
	}
	return this.groupTaskExecutor.submitTask(listener, tasks);
  }

  @Override
  public TaskSession submitTask(boolean isParallel, Task... tasks) {
	return submitTask(isParallel, null, tasks);
  }

  @Override
  public TaskSession submitTask(boolean isParallel,
								TaskSessionListener listener, Task... tasks) {
	checkRegistered();
	for (Task task : tasks) {
	  task.registerTaskSubmitter(this);
	}
	return this.groupTaskExecutor.submitTask(isParallel, listener, tasks);
  }

  @Override
  public String getTaskSubmitterId() {
	return getId();
  }

  @Override
  public Collection<InfoNode> getInfoNodes() {
	Collection<InfoNode> nodes = new ArrayList<InfoNode>();
	nodes.add(new InfoNode("Package", getClass().getPackage().getName()));
	nodes.add(new InfoNode("Class", getClass().getSimpleName()));
	nodes.add(new InfoNode("Id", getId()));
	nodes.add(new InfoNode("Name", getName()));

	Controller root = getRootController();
	if (root != null) {
	  nodes.add(new InfoNode(InfoNodeIdentifiers.GROUP_ROOT_CONTROLLER.name(),
		  "Root Controller", root.getInfoNodes()));
	} else {
	  nodes.add(new InfoNode(InfoNodeIdentifiers.GROUP_ROOT_CONTROLLER.name(),
		  "Root Controller", "null"));
	}

	nodes.add(new InfoNode("Group Model", getModel().getInfoNodes()));

	return nodes;
  }

  @Override
  public Collection<IdentifierNode> getIdentifierNodes() {
	return new ArrayList<IdentifierNode>();
  }

  public static <T extends Controller> T getControllerFrom(Task task,
														   Class<T> clss) {
	Group group = Group.getGroupFrom(task);
	if (group != null) {
	  return group.get(clss);
	}
	return null;
  }

  public static Controller getRootFrom(Task task) {
	Group group = Group.getGroupFrom(task);
	if (group != null) {
	  return group.getRootController();
	}
	return null;
  }

  public static Group getGroupFrom(Task task) {
	if (task.getTaskExecutor() != null
		&& task.getTaskExecutor() instanceof GroupTaskExecutor) {
	  GroupTaskExecutor groupTaskExecutor =
		  (GroupTaskExecutor) task.getTaskExecutor();
	  return groupTaskExecutor.getGroup();
	}
	return null;
  }

}
