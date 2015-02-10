package org.japura.controller;

import java.awt.Component;
import java.awt.Window;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.SwingUtilities;

import org.japura.Application;
import org.japura.controller.annotations.ChildController;
import org.japura.controller.annotations.ContextName;
import org.japura.controller.annotations.ControllerDescription;
import org.japura.controller.annotations.GroupModelClass;
import org.japura.controller.annotations.GroupName;
import org.japura.controller.annotations.RootController;
import org.japura.controller.annotations.Singleton;
import org.japura.controller.listeners.ControllerListener;
import org.japura.controller.messages.ControllerRegisterMessage;
import org.japura.controller.messages.ScreenShotMessage;
import org.japura.message.Message;
import org.japura.message.MessageFilter;
import org.japura.message.MessageManager;
import org.japura.task.Task;
import org.japura.task.session.TaskSession;
import org.japura.task.session.TaskSessionListener;
import org.japura.util.info.IdentifierNode;
import org.japura.util.info.InfoNode;

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
public abstract class DefaultController<V> implements Controller{

  private V component;
  private DefaultControllerCollection children;
  private Controller parentController;
  private String id;
  private Group group;
  private String controllerName;
  private String description;
  private Context context;
  private ControllerStatus status;
  private List<ControllerListener> listeners;

  public DefaultController(Context context, Controller parentController) {
	this.controllerName = "";

	this.description = "";
	ControllerDescription controllerDescription =
		getClass().getAnnotation(ControllerDescription.class);
	if (controllerDescription != null) {
	  this.description = controllerDescription.description().trim();
	}

	ContextName contextName = getClass().getAnnotation(ContextName.class);
	if (context == null) {
	  if (contextName != null) {
		if (contextName.name().trim().equals(Context.MAIN_CONTEXT)) {
		  context = Context.getMainContext();
		} else {
		  context = new Context(contextName.name().trim());
		}
	  } else {
		context = Context.getMainContext();
	  }
	} else {
	  if (contextName != null
		  && context.getName().equals(contextName.name().trim()) == false) {
		throw new ControllerException(getClass().getName()
			+ " must be instantiated with a context name ["
			+ contextName.name().trim() + "] but was [" + context.getName()
			+ "]");
	  }
	}

	if (parentController != null) {
	  if (context.equals(parentController.getContext()) == false) {
		throw new ControllerException(
			"The context must be the same as the parent controller.");
	  }
	  if (getClass().isAnnotationPresent(RootController.class)) {
		throw new ControllerException("[" + getClass().getName()
			+ "] This controller must be a root.");
	  }
	} else if (getClass().isAnnotationPresent(ChildController.class)) {
	  throw new ControllerException(
		  "["
			  + getClass().getName()
			  + "] Child controller must be instantiated through method createChild");
	}

	Class<?> cl = getClass();
	if (cl.isAnnotationPresent(Singleton.class)
		&& Application.getControllerManager().count(cl) > 0) {
	  throw new ControllerException("[" + cl.getName()
		  + "] Singleton Controller");
	}

	this.context = context;
	this.id = Application.buildId();
	this.listeners =
		Collections.synchronizedList(new ArrayList<ControllerListener>());
	this.children = new DefaultControllerCollection();

	this.parentController = parentController;
	if (parentController != null) {
	  parentController.registerChild(this);
	  this.group = parentController.getGroup();
	} else {
	  Class<? extends GroupModel> groupModelClass = GroupModel.class;
	  String groupName = "";

	  GroupModelClass groupModelClassAnnotation =
		  getClass().getAnnotation(GroupModelClass.class);
	  if (groupModelClassAnnotation != null) {
		groupModelClass = groupModelClassAnnotation.modelClass();
	  }

	  GroupName groupNameAnnotation = getClass().getAnnotation(GroupName.class);
	  if (groupNameAnnotation != null) {
		groupName = groupNameAnnotation.name().trim();
	  }

	  this.group = new Group(groupName, context, groupModelClass);
	}

	Application.getControllerManager().register(this);
	this.context.register(this);
	this.group.register(this);

	setControllerStatus(ControllerStatus.REGISTERED);
	MessageManager messageManaer = Application.getMessageManager();
	messageManaer.register(this);
	messageManaer.addMessageFilter(this, new MessageFilter() {
	  @Override
	  public boolean accepts(Message message) {
		if (getControllerStatus().equals(ControllerStatus.REGISTERED)) {
		  return true;
		}
		return false;
	  }
	});
	messageManaer.publish(false, new ControllerRegisterMessage(this), this);
  }

  /**
   * Indicates whether the controller's component is instantiated.
   * 
   * @return boolean
   */
  public boolean isComponentInstancied() {
	if (this.component != null) {
	  return true;
	}
	return false;
  }

  public abstract V buildComponent();

  /**
   * Get the controlled component.
   * 
   * @return V the component
   */
  @SuppressWarnings("unchecked")
  public V getComponent() {
	if (this.component == null) {
	  try {
		this.component =
			(V) Application.getControllerManager().getComponentBuilder()
				.build(this);
	  } catch (Exception e) {
		throw new ControllerException("Component Build Error", e);
	  }

	  if (this.component == null) {
		throw new ControllerException("NULL Component.");
	  }
	}
	return this.component;
  }

  @Override
  public final void registerChild(Controller controller) {
	children.register(controller);
  }

  @Override
  public final void unregisterChild(Controller controller) {
	children.unregister(controller);
  }

  @Override
  public final void unregisterChildren() {
	children.unregisterAll();
  }

  @Override
  public final void unregisterParent() {
	this.parentController = null;
  }

  @Override
  public final void setControllerStatus(ControllerStatus status) {
	this.status = status;
	fireStatusChangedListeners(this);
  }

  @Override
  public final ControllerStatus getControllerStatus() {
	return this.status;
  }

  @Override
  public Context getContext() {
	return context;
  }

  @Override
  public String getControllerDescription() {
	return this.description;
  }

  @Override
  public void setControllerName(String name) {
	if (name == null) {
	  name = "";
	}
	this.controllerName = name;
  }

  @Override
  public void addControllerListener(ControllerListener listener) {
	this.listeners.add(listener);
  }

  @Override
  public void removeControllerListener(ControllerListener listener) {
	this.listeners.remove(listener);
  }

  @Override
  public Collection<ControllerListener> getControllerListeners() {
	return Collections.unmodifiableList(this.listeners);
  }

  private void fireStatusChangedListeners(Controller controller) {
	for (ControllerListener listener : listeners) {
	  listener.statusChanged(controller);
	}
  }

  @Override
  public String stringToDebugComponent() {
	if (getControllerName().length() > 0l) {
	  return "Id:" + getControllerId() + " - Name: " + getControllerName();
	}
	return "Id:" + getControllerId();
  }

  @Override
  public final <E> E createChild(Class<E> controllerClass) {
	if (Application.getControllerManager().contains(this) == false) {
	  throw new ControllerException("The controller " + getClass() + " id ("
		  + getControllerId() + ") already had been removed.. ");
	}
	return Application.getControllerManager().buildController(controllerClass,
		context, this);
  }

  @Override
  public <E> Collection<E> getChildren(Class<E> clss) {
	return children.getAll(clss);
  }

  /**
   * Indicates whether the child controller is instantiated.
   * 
   * @param clss
   *          controller's class
   * @return boolean
   */
  @Override
  public boolean containsChild(Class<?> clss) {
	return children.contains(clss);
  }

  @Override
  public Collection<Controller> getChildren() {
	return children.getAll();
  }

  @Override
  public <E> E getChild(Class<E> clss) {
	return children.get(clss);
  }

  @Override
  public Controller getChild(String id) {
	return children.get(id);
  }

  @Override
  public final String getControllerId() {
	return id;
  }

  @Override
  public final Group getGroup() {
	return group;
  }

  @Override
  public final String getGroupId() {
	return getGroup().getId();
  }

  @Override
  public String getControllerName() {
	return controllerName;
  }

  /**
   * Get the parent controller's identifier.
   * 
   * @return Integer the identifier or <CODE>NULL</CODE> if its not exist.
   */
  @Override
  public final String getParentId() {
	if (getParent() != null) {
	  return getParent().getControllerId();
	}
	return null;
  }

  @Override
  public final void free() {
	Application.getControllerManager().free(this);
  }

  @Override
  public void freeChildren() {
	Collection<Controller> controllers = getChildren();
	Application.getControllerManager().free(
		controllers.toArray(new Controller[controllers.size()]));
  }

  /**
   * Get the parent controller
   * 
   * @return the parent Controller or <CODE>NULL</CODE> if its not exist.
   */
  @Override
  public final Controller getParent() {
	return parentController;
  }

  @Override
  public final boolean isRoot() {
	if (getParent() == null) {
	  return true;
	}
	return false;
  }

  /**
   * Get the root controller in the hierarchy group.
   * 
   * @return the root controller in the hierarchy or the self controller if not
   *         exists a parent controller.
   */
  @Override
  public final Controller getRoot() {
	Controller superController = this;
	while (superController.getParent() != null) {
	  superController = superController.getParent();
	}
	return superController;
  }

  @Override
  public final void publish(boolean synchronous, Message message) {
	Application.getMessageManager().publish(synchronous, message, this);
  }

  @Override
  public TaskSession submitTask(Task... tasks) {
	return submitTask(null, tasks);
  }

  @Override
  public TaskSession submitTask(TaskSessionListener listener, Task... tasks) {
	if (getControllerStatus().equals(ControllerStatus.UNREGISTERED)) {
	  throw new ControllerException("Controller is unregistered");
	}
	for (Task task : tasks) {
	  task.registerTaskSubmitter(this);
	}
	return getGroup().submitTask(listener, tasks);
  }

  @Override
  public TaskSession submitTask(boolean isParallel, Task... tasks) {
	return submitTask(isParallel, null, tasks);
  }

  @Override
  public TaskSession submitTask(boolean isParallel,
								TaskSessionListener listener, Task... tasks) {
	if (getControllerStatus().equals(ControllerStatus.UNREGISTERED)) {
	  throw new ControllerException("Controller is unregistered");
	}
	for (Task task : tasks) {
	  task.registerTaskSubmitter(this);
	}
	return getGroup().submitTask(isParallel, listener, tasks);
  }

  @Override
  public String getTaskSubmitterId() {
	return getControllerId();
  }

  @Override
  public String toString() {
	return getClass().getSimpleName() + " [ ID: " + getControllerId() + " ]";
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
	DefaultController<?> other = (DefaultController<?>) obj;
	if (id == null) {
	  if (other.id != null)
		return false;
	} else if (!id.equals(other.id))
	  return false;
	return true;
  }

  public GroupModel getGroupModel() {
	return getGroup().getModel();
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
	nodes.add(new InfoNode("Name", getControllerName()));
	nodes.add(new InfoNode("Description", getControllerDescription()));
	return nodes;
  }

  @Override
  public void subscribe(Message message, Object publisher) {
	if (message instanceof ScreenShotMessage) {
	  ScreenShotMessage ssm = (ScreenShotMessage) message;
	  if (isComponentInstancied() && getComponent() instanceof Component) {
		Component comp = (Component) getComponent();
		ssm.perform(comp);
	  }
	}
  }

  protected void disposeWindow() {
	if (isComponentInstancied() && getComponent() instanceof Window) {
	  Runnable disposer = new Runnable() {
		@Override
		public void run() {
		  Window window = (Window) getComponent();
		  window.dispose();
		}
	  };
	  if (SwingUtilities.isEventDispatchThread()) {
		disposer.run();
	  } else {
		SwingUtilities.invokeLater(disposer);
	  }
	}
  }

}
