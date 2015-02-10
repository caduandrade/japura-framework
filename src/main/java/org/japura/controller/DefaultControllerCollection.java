package org.japura.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import org.japura.controller.listeners.ControllerCollectionListener;

/**
 * Copyright (C) 2013 Carlos Eduardo Leite de Andrade
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
public class DefaultControllerCollection implements ControllerCollection{

  private Collection<Controller> controllers;
  private List<ControllerCollectionListener> listeners;
  private Comparator<Controller> comparator;
  private Collection<Context> contexts;

  public DefaultControllerCollection() {
	controllers = new HashSet<Controller>();
	listeners =
		Collections
			.synchronizedList(new ArrayList<ControllerCollectionListener>());
	comparator = new Comparator<Controller>() {
	  @Override
	  public int compare(Controller o1, Controller o2) {
		if (o1.isRoot()) {
		  return -1;
		}
		if (o2.isRoot()) {
		  return 1;
		}
		return 0;
	  }
	};
  }

  @Override
  public Collection<ControllerCollectionListener> getListeners() {
	return Collections.unmodifiableList(listeners);
  }

  @Override
  public void addListener(ControllerCollectionListener listener) {
	this.listeners.add(listener);
  }

  @Override
  public void removeListener(ControllerCollectionListener listener) {
	this.listeners.remove(listener);
  }

  public final void testControllerClass(Class<?> clss) {
	if (clss == null) {
	  throw new IllegalArgumentException("Null class");
	}
	if (Controller.class.isAssignableFrom(clss) == false) {
	  throw new IllegalArgumentException("Not a valid controller class: "
		  + clss.getName());
	}
  }

  @Override
  public void fireListeners(Controller controller, ControllerStatus newStatus) {
	for (ControllerCollectionListener listener : listeners) {
	  if (newStatus.equals(ControllerStatus.REGISTERED)) {
		listener.registered(controller);
	  } else if (newStatus.equals(ControllerStatus.UNREGISTERING)) {
		listener.unregistering(controller);
	  } else if (newStatus.equals(ControllerStatus.UNREGISTERED)) {
		listener.unregistered(controller);
	  }
	}
  }

  @Override
  public final void register(Controller controller) {
	synchronized (controllers) {
	  contexts = null;
	  controllers.add(controller);
	}
	fireListeners(controller, ControllerStatus.REGISTERED);
  }

  @Override
  public final void unregister(Controller controller) {
	synchronized (controllers) {
	  contexts = null;
	  controllers.remove(controller);
	}
	fireListeners(controller, ControllerStatus.UNREGISTERED);
  }

  @Override
  public final void unregisterAll() {
	Collection<Controller> removed = null;
	synchronized (controllers) {
	  contexts = null;
	  removed = new ArrayList<Controller>(controllers);
	  controllers.clear();
	}
	for (Controller controller : removed) {
	  fireListeners(controller, ControllerStatus.UNREGISTERED);
	}
  }

  /**
   * Gets the controller of a specific class.
   * 
   * @param clss
   *          the controller's class
   * @return the controller or <code>NULL</code> if its not exist.
   * 
   */
  public <E> E get(Class<E> clss) {
	testControllerClass(clss);
	synchronized (controllers) {
	  for (Controller controller : controllers) {
		if (clss.isAssignableFrom(controller.getClass())) {
		  return clss.cast(controller);
		}
	  }
	}
	return null;
  }

  @Override
  public <E> E get(String id, Class<E> clss) {
	testControllerClass(clss);
	if (id != null) {
	  synchronized (controllers) {
		for (Controller controller : controllers) {
		  if (clss.isAssignableFrom(controller.getClass())
			  && controller.getControllerId().equals(id)) {
			return clss.cast(controller);
		  }
		}
	  }
	}
	return null;
  }

  @Override
  public final Controller get(String id) {
	if (id != null) {
	  synchronized (controllers) {
		for (Controller controller : controllers) {
		  if (controller.getControllerId().equals(id)) {
			return controller;
		  }
		}
	  }
	}
	return null;
  }

  @Override
  public final <E> Collection<E> getAll(Class<E> clss) {
	testControllerClass(clss);
	List<E> list = new ArrayList<E>();
	synchronized (controllers) {
	  for (Controller controller : controllers) {
		if (clss.isAssignableFrom(controller.getClass())) {
		  list.add(clss.cast(controller));
		}
	  }
	}
	return list;
  }

  @Override
  public final Collection<Controller> getAll() {
	synchronized (controllers) {
	  List<Controller> list = new ArrayList<Controller>(controllers);
	  Collections.sort(list, comparator);
	  return list;
	}
  }

  @Override
  public final boolean contains(Class<?> clss) {
	testControllerClass(clss);
	synchronized (controllers) {
	  for (Controller controller : controllers) {
		if (clss.isAssignableFrom(controller.getClass())) {
		  return true;
		}
	  }
	}
	return false;
  }

  @Override
  public final void printAllControllers() {
	synchronized (controllers) {
	  for (Controller controller : controllers) {
		System.out.println(controller.toString());
	  }
	}
  }

  @Override
  public final int count(Class<?> clss) {
	testControllerClass(clss);
	int count = 0;
	synchronized (controllers) {
	  for (Controller controller : controllers) {
		if (clss.isAssignableFrom(controller.getClass())) {
		  count++;
		}
	  }
	}
	return count;
  }

  @Override
  public int count() {
	synchronized (controllers) {
	  return controllers.size();
	}
  }

  @Override
  public final boolean contains(Controller controller) {
	if (controller != null) {
	  synchronized (controllers) {
		return controllers.contains(controller);
	  }
	}
	return false;
  }

  protected Collection<Context> getContexts() {
	Collection<Context> contexts = new HashSet<Context>();
	synchronized (controllers) {
	  for (Controller controller : controllers) {
		contexts.add(controller.getContext());
	  }
	}
	return contexts;
  }

  protected Collection<Context> getContexts(String name) {
	synchronized (controllers) {
	  if (contexts == null) {
		contexts = new HashSet<Context>();
	  }
	  for (Controller controller : controllers) {
		if (controller.getContext().getName().equals(name)) {
		  contexts.add(controller.getContext());
		}
	  }
	  return Collections.unmodifiableCollection(contexts);
	}
  }

  protected int getContextsCount() {
	return getContexts().size();
  }
}
