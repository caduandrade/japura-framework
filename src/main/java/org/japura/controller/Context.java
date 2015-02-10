package org.japura.controller;

import java.util.ArrayList;
import java.util.Collection;

import org.japura.Application;
import org.japura.util.info.IdentifierNode;
import org.japura.util.info.InfoNode;
import org.japura.util.info.InfoProvider;

/**
 * <P>
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
 */
public final class Context extends DefaultControllerCollection implements
	InfoProvider{

  public static final String MAIN_CONTEXT = "MAIN";
  private final static Context mainContext = new Context();

  public static Context getMainContext() {
	return Context.mainContext;
  }

  private final String id;
  private final String name;
  private final ContextSession session;

  private Context() {
	this.name = Context.MAIN_CONTEXT;
	this.id = Application.buildId();
	this.session = new ContextSession();
  }

  public Context(String name) {
	if (name == null) {
	  throw new IllegalArgumentException("Null content name");
	}
	name = name.trim();
	if (name.length() == 0) {
	  throw new IllegalArgumentException("Empty context name");
	}
	if (name.equals(Context.MAIN_CONTEXT)) {
	  throw new IllegalArgumentException("Reserved context name: " + name);
	}
	this.name = name;
	this.id = Application.buildId();
	this.session = new ContextSession();
  }

  public String getId() {
	return id;
  }

  public String getName() {
	return name;
  }

  public ContextSession getSession() {
	return session;
  }

  public <E> E buildController(Class<E> controllerClass) {
	testControllerClass(controllerClass);
	E controller =
		Application.getControllerManager().buildController(controllerClass,
			this, null);
	return controller;
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

  @Override
  public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + id.hashCode();
	return result;
  }

  @Override
  public boolean equals(Object obj) {
	if (obj == null)
	  return false;
	if (getClass() != obj.getClass())
	  return false;
	Context other = (Context) obj;
	if (!id.equals(other.id))
	  return false;
	return true;
  }

  @Override
  public Collection<IdentifierNode> getIdentifierNodes() {
	return new ArrayList<IdentifierNode>();
  }

  @Override
  public Collection<InfoNode> getInfoNodes() {
	Collection<InfoNode> nodes = new ArrayList<InfoNode>();
	nodes.add(new InfoNode("Package", getClass().getPackage().getName()));
	nodes.add(new InfoNode("Class", getClass().getSimpleName()));
	nodes.add(new InfoNode("Id", this.id));
	nodes.add(new InfoNode("Name", getName()));
	nodes.add(new InfoNode("Context Session", getSession().getInfoNodes()));
	return nodes;
  }

}
