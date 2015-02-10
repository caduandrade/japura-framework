package org.japura.controller.manager;

import java.util.Collection;

import org.japura.controller.Context;
import org.japura.controller.Controller;
import org.japura.controller.ControllerCollection;

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
public interface ControllerManager extends ControllerCollection{

  /**
   * Remove from the memory all controllers of a specific class.
   * <P>
   * Controllers defined as permanent, can not be removed unless the parent has
   * been removed.
   * <P>
   * All executions of the tasks will be canceled.
   * 
   * @param clss
   *          controller's class
   */
  public void freeAll(Class<?> clss);

  /**
   * Remove from the memory all controllers.
   * <P>
   * Controllers defined as permanent, can not be removed unless the parent has
   * been removed.
   * 
   * @see #freeAll(Class)
   */
  public void freeAll();

  public void free(Controller... controllers);

  public void purge();

  public Controller getRoot(String groupId);

  public <E> E buildController(Class<E> controllerClass, Context context,
							   Controller parentController);

  public <E> E buildController(Class<E> controllerClass, Context context);

  public <E> E buildController(Class<E> controllerClass);

  public Collection<Context> getContexts();

  public Collection<Context> getContexts(String name);

  public int getContextsCount();

  public void setComponentBuilder(ComponentBuilder componentBuilder);

  public ComponentBuilder getComponentBuilder();

}
