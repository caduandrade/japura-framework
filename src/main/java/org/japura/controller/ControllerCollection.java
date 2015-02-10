package org.japura.controller;

import java.util.Collection;

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
public interface ControllerCollection{

  public void register(Controller controller);

  public void unregister(Controller controller);

  public void unregisterAll();

  public Collection<ControllerCollectionListener> getListeners();

  public void addListener(ControllerCollectionListener listener);

  public void removeListener(ControllerCollectionListener listener);

  public void fireListeners(Controller controller, ControllerStatus newStatus);

  /**
   * Gets the controller of a specific identifier.
   * 
   * @param id
   *          the controller's identifier
   * 
   * @return the controller or <code>NULL</code> if its not exist.
   * 
   */
  public Controller get(String id);

  /**
   * Gets the first controller of a specific class.
   * <P>
   * If not exists, a controller will be instantiated.
   * 
   * @param <E>
   *          .
   * @param clss
   *          the controller's class
   * @return the controller
   */
  // TODO rever javadoc
  public <E> E get(Class<E> clss);

  public <E> E get(String id, Class<E> clss);

  /**
   * Gets the all controllers of a specific class.
   * 
   * @param clss
   *          the controller's class
   * @return Collection with the controllers.
   * 
   */
  public <E> Collection<E> getAll(Class<E> clss);

  public Collection<Controller> getAll();

  /**
   * Indicates whether the controller is instantiated.
   * 
   * @param clss
   *          controller's class
   * @return boolean
   */
  public boolean contains(Class<?> clss);

  /**
   * Prints all the instantiated controllers.
   */
  public void printAllControllers();

  /**
   * Get the total of controllers of a specific class.
   * 
   * @param clss
   *          the controller's class
   * @return the total of controllers
   */
  public int count(Class<?> clss);

  public int count();

  public boolean contains(Controller controller);

}
