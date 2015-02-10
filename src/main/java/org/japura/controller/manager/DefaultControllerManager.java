package org.japura.controller.manager;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.japura.Application;
import org.japura.controller.Context;
import org.japura.controller.Controller;
import org.japura.controller.ControllerException;
import org.japura.controller.ControllerStatus;
import org.japura.controller.DefaultController;
import org.japura.controller.DefaultControllerCollection;
import org.japura.controller.Group;
import org.japura.controller.annotations.ChildController;
import org.japura.controller.annotations.NonDisposableRoot;
import org.japura.controller.messages.ControllerUnregisterMessage;

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
public class DefaultControllerManager extends DefaultControllerCollection
	implements ControllerManager{

  private Object freeLock = new Object();
  private ComponentBuilder componentBuilder = new DefaultComponentBuilder();

  protected final void methodNotImplemented() {
	throw new RuntimeException("Method not implemented");
  }

  @Override
  public final <E> E get(Class<E> clss) {
	ChildController cc = clss.getAnnotation(ChildController.class);
	if (cc != null && cc.getOnlyFromGroup()) {
	  throw new ControllerException(
		  "You need get the child controller "
			  + clss.getName()
			  + " through the group. Use getGroup().get() or Controller.getFromGroup() method.");
	}

	E controller = super.get(clss);
	if (controller == null) {
	  controller = buildController(clss, null, null);
	}
	return controller;
  }

  private void fetchHierarchy(Controller parentController, Set<Controller> list) {
	Collection<Controller> children = parentController.getChildren();
	for (Controller child : children) {
	  if (child.getControllerStatus().equals(ControllerStatus.REGISTERED)) {
		list.add(child);
	  }
	  fetchHierarchy(child, list);
	}
  }

  @Override
  public Controller getRoot(String groupId) {
	for (Controller controller : getAll()) {
	  if (controller.getGroup().getId().equals(groupId)) {
		return controller.getRoot();
	  }
	}
	return null;
  }

  @Override
  public void freeAll() {
	free(getAll().toArray(new Controller[0]));
  }

  @Override
  public void freeAll(Class<?> clss) {
	free(getAll(clss).toArray(new Controller[0]));
  }

  @Override
  public void free(Controller... controllers) {
	synchronized (freeLock) {
	  Set<Controller> freeList = new HashSet<Controller>();
	  for (Controller controller : controllers) {
		if (controller.isRoot()
			&& controller.getClass().getAnnotation(NonDisposableRoot.class) != null) {
		  continue;
		}
		if (controller.getControllerStatus()
			.equals(ControllerStatus.REGISTERED)) {
		  freeList.add(controller);
		}
		fetchHierarchy(controller, freeList);
	  }

	  for (Controller controller : freeList) {
		ControllerStatus newStatus = ControllerStatus.UNREGISTERING;
		controller.setControllerStatus(newStatus);

		Group group = controller.getGroup();
		group.fireListeners(controller, newStatus);
		Context context = controller.getContext();
		context.fireListeners(controller, newStatus);
		fireListeners(controller, newStatus);
	  }
	  purge();
	}
  }

  @Override
  public void purge() {
	synchronized (freeLock) {
	  Map<Group, Collection<Controller>> map =
		  new HashMap<Group, Collection<Controller>>();

	  for (Controller controller : getAll()) {
		if (controller.getControllerStatus().equals(
			ControllerStatus.UNREGISTERING) == false) {
		  continue;
		}
		Group group = controller.getGroup();
		Collection<Controller> list = map.get(group);
		if (list == null) {
		  list = new ArrayList<Controller>();
		}
		map.put(group, list);
		list.add(controller);
	  }

	  for (Entry<Group, Collection<Controller>> entry : map.entrySet()) {
		Group group = entry.getKey();

		if (Application.getTaskManager().hasTask(group)) {
		  continue;
		}

		// unregister all
		for (Controller controller : entry.getValue()) {
		  Application.getMessageManager().unregister(controller);

		  group.unregister(controller);

		  Context context = controller.getContext();
		  context.unregister(controller);

		  Controller parentController = controller.getParent();
		  if (parentController != null) {
			controller.unregisterParent();
			parentController.unregisterChild(controller);
		  }
		  controller.unregisterChildren();

		  unregister(controller);

		  controller.setControllerStatus(ControllerStatus.UNREGISTERED);
		}

		if (group.count() == 0) {
		  group.getGroupTaskExecutor().shutdown();
		}

		Application.getMessageManager().publish(false,
			new ControllerUnregisterMessage(entry.getValue()), this);
	  }
	}
  }

  @Override
  public <E> E buildController(Class<E> controllerClass, Context context,
							   Controller parentController) {
	E controller = null;
	if (DefaultController.class.isAssignableFrom(controllerClass)) {
	  try {
		Constructor<E> constructor =
			controllerClass.getConstructor(Context.class, Controller.class);
		controller = constructor.newInstance(context, parentController);
	  } catch (NoSuchMethodException e) {
		throw new ControllerException(
			controllerClass.getName()
				+ " must have a constructor with context and parent controller parameters.",
			e);
	  } catch (Exception e) {
		throw new ControllerException("Controller constructor error", e);
	  }
	} else {
	  throw new IllegalArgumentException("Not a supported controller class: "
		  + controllerClass.getName());
	}
	return controller;
  }

  @Override
  public <E> E buildController(Class<E> controllerClass) {
	return this.buildController(controllerClass, null, null);
  }

  @Override
  public <E> E buildController(Class<E> controllerClass, Context context) {
	return this.buildController(controllerClass, context, null);
  }

  @Override
  public Collection<Context> getContexts() {
	return super.getContexts();
  }

  @Override
  public Collection<Context> getContexts(String name) {
	return super.getContexts(name);
  }

  @Override
  public int getContextsCount() {
	return super.getContextsCount();
  }

  @Override
  public void setComponentBuilder(ComponentBuilder componentBuilder) {
	this.componentBuilder = componentBuilder;
  }

  @Override
  public ComponentBuilder getComponentBuilder() {
	return this.componentBuilder;
  }
}
