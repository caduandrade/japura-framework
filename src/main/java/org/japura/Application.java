package org.japura;

import java.util.Random;

import org.japura.controller.manager.ControllerManager;
import org.japura.controller.manager.DefaultControllerManager;
import org.japura.dialogs.DefaultMessageDialogs;
import org.japura.dialogs.MessageDialogs;
import org.japura.exception.DefaultHandlerExceptionManager;
import org.japura.exception.HandlerExceptionManager;
import org.japura.message.DefaultMessageManager;
import org.japura.message.Message;
import org.japura.message.MessageManager;
import org.japura.message.Subscriber;
import org.japura.task.manager.DefaultTaskManager;
import org.japura.task.manager.TaskManager;
import org.japura.task.messages.ExecutorPerformedMessage;

/**
 * <P>
 * Copyright (C) 2012-2013 Carlos Eduardo Leite de Andrade
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
public final class Application{

  private static Random randomID = new Random();

  private static ApplicationSubscriber applicationSubscriber =
	  new ApplicationSubscriber();

  private static MessageDialogs messageDialogs;
  private static ControllerManager controlllerManager;
  private static HandlerExceptionManager handlerExceptionManager;
  private static TaskManager taskManager;
  private static MessageManager messageManager;

  private Application() {}

  public static void reset() {
	Application.messageDialogs = new DefaultMessageDialogs();
	Application.controlllerManager = new DefaultControllerManager();
	Application.handlerExceptionManager = new DefaultHandlerExceptionManager();
	Application.taskManager = new DefaultTaskManager();

	if (Application.messageManager != null) {
	  Application.messageManager.unregister(getApplicationSubscriber());
	}
	Application.messageManager = new DefaultMessageManager();
	Application.messageManager.register(getApplicationSubscriber());
  }

  private static ApplicationSubscriber getApplicationSubscriber() {
	return Application.applicationSubscriber;
  }

  public static MessageManager getMessageManager() {
	if (Application.messageManager == null) {
	  Application.messageManager = new DefaultMessageManager();
	  Application.messageManager.register(getApplicationSubscriber());
	}
	return Application.messageManager;
  }

  public static void setMessageManager(MessageManager messageManager) {
	if (messageManager == null) {
	  messageManager = new DefaultMessageManager();
	}
	if (Application.messageManager != null) {
	  Application.messageManager.unregister(getApplicationSubscriber());
	}
	Application.messageManager = messageManager;
	Application.messageManager.register(getApplicationSubscriber());
  }

  public static MessageDialogs getMessageDialogs() {
	if (Application.messageDialogs == null) {
	  Application.messageDialogs = new DefaultMessageDialogs();
	}
	return Application.messageDialogs;
  }

  public static void setMessageDialogs(MessageDialogs messageDialogs) {
	if (messageDialogs == null) {
	  messageDialogs = new DefaultMessageDialogs();
	}
	Application.messageDialogs = messageDialogs;
  }

  public static ControllerManager getControllerManager() {
	if (Application.controlllerManager == null) {
	  Application.controlllerManager = new DefaultControllerManager();
	}
	return Application.controlllerManager;
  }

  public static void setControllerManager(ControllerManager controllerManager) {
	if (controllerManager == null) {
	  controllerManager = new DefaultControllerManager();
	}
	Application.controlllerManager = controllerManager;
  }

  public static HandlerExceptionManager getHandlerExceptionManager() {
	if (Application.handlerExceptionManager == null) {
	  Application.handlerExceptionManager =
		  new DefaultHandlerExceptionManager();
	}
	return Application.handlerExceptionManager;
  }

  public static void setHandlerExceptionManager(HandlerExceptionManager handlerExceptionManager) {
	if (handlerExceptionManager == null) {
	  handlerExceptionManager = new DefaultHandlerExceptionManager();
	}
	Application.handlerExceptionManager = handlerExceptionManager;
  }

  public static TaskManager getTaskManager() {
	if (Application.taskManager == null) {
	  Application.taskManager = new DefaultTaskManager();
	}
	return Application.taskManager;
  }

  public static void setTaskManager(TaskManager taskManager) {
	if (taskManager == null) {
	  taskManager = new DefaultTaskManager();
	}
	Application.taskManager = taskManager;
  }

  public static String buildId() {
	int randomInt = randomID.nextInt();
	int c = randomID.nextInt(24) + 97;
	long nanoTime = System.nanoTime();
	return Integer.toHexString(randomInt) + ":" + (char) c + ":"
		+ Long.toHexString(nanoTime);
  }

  private static class ApplicationSubscriber implements Subscriber{
	@Override
	public void subscribe(Message message, Object publisher) {
	  if (message instanceof ExecutorPerformedMessage) {
		Application.getControllerManager().purge();
	  }
	}
  }

}
