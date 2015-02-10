package org.japura.controller;

import java.util.Collection;

import org.japura.controller.listeners.ControllerListener;
import org.japura.message.Message;
import org.japura.message.Subscriber;
import org.japura.task.executors.TaskSubmitter;
import org.japura.util.info.InfoProvider;

/**
 * The Controller isolates business logic from presentation.
 * <P>
 * Must implement the following methods:
 * <UL>
 * <LI> <code>getComponent</code> to get the controlled component</LI>
 * <LI> <code>isComponentInstancied</code> to indicates whether the controller is
 * instantiated</LI>
 * </UL>
 * <P>
 * Every instantiated controller is added to a pool of controllers. Through the
 * pool, its possible reach any controller.
 * <P>
 * The state of permanent indicates that the controller can't be removed from
 * the pool unless the parent has been removed.
 * <P>
 * Annotations:
 * <P>
 * <UL>
 * <LI><code>ChildController</code> - defines that a controller can't be
 * instantiated by constructor, only through method <code>createChild</code>.</LI>
 * <LI><code>Singleton</code> - defines a controller as singleton.</LI>
 * </UL>
 * <P>
 * Copyright (C) 2009-2014 Carlos Eduardo Leite de Andrade
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
 * E
 * 
 * @author Carlos Eduardo Leite de Andrade
 * 
 * @param V
 *          controlled component class
 */
public interface Controller extends TaskSubmitter, Subscriber, InfoProvider{

  public void addControllerListener(ControllerListener listener);

  public void removeControllerListener(ControllerListener listener);

  public Collection<ControllerListener> getControllerListeners();

  /**
   * Gets the context.
   * 
   * @return {@link Context}
   */
  public Context getContext();

  /**
   * Registers a child controller in the container. DO NOT USE THIS METHOD
   * UNLESS YOU ARE DEVELOPING A NEW CONTROLLER MANAGER.
   * 
   * @param controller
   *          the child controller.
   */
  public void registerChild(Controller controller);

  /**
   * Unregisters a child controller in the container. DO NOT USE THIS METHOD
   * UNLESS YOU ARE DEVELOPING A NEW CONTROLLER MANAGER.
   * 
   * @param controller
   *          the child controller.
   */
  public void unregisterChild(Controller controller);

  /**
   * Unregisters the children controllers in the container. DO NOT USE THIS
   * METHOD UNLESS YOU ARE DEVELOPING A NEW CONTROLLER MANAGER.
   */
  public void unregisterChildren();

  /**
   * Unregisters the parent controller in the container. DO NOT USE THIS METHOD
   * UNLESS YOU ARE DEVELOPING A NEW CONTROLLER MANAGER.
   */
  public void unregisterParent();

  /**
   * Defines a status for the controller. DO NOT USE THIS METHOD UNLESS YOU ARE
   * DEVELOPING A NEW CONTROLLER MANAGER.
   * 
   * @param status
   */
  public void setControllerStatus(ControllerStatus status);

  public ControllerStatus getControllerStatus();

  /**
   * Obtains the identifier.
   * 
   * @return the identifier
   */
  public String getControllerId();

  public String getControllerDescription();

  public String stringToDebugComponent();

  /**
   * Creates a child controller.
   * <P>
   * The new controller contains the parent controller's identifier.
   * 
   * @param <E>
   * @param clss
   *          the controller's class
   * @return the new controller
   * @see #getRoot()
   * @see #getParent()
   */
  public <E> E createChild(Class<E> clss);

  /**
   * Removes the controller from the memory. <BR>
   * <BR>
   * Undo, if exists, the current link to any other controller
   */
  public void free();

  public void freeChildren();

  public <E> E getChild(Class<E> clss);

  public Controller getChild(String id);

  public Group getGroup();

  public String getGroupId();

  public <E> Collection<E> getChildren(Class<E> clss);

  public Collection<Controller> getChildren();

  public String getControllerName();

  public void setControllerName(String name);

  public Controller getParent();

  public String getParentId();

  public boolean containsChild(Class<?> clss);

  public Controller getRoot();

  public boolean isRoot();

  public void publish(boolean synchronous, Message message);

}
