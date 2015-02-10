package org.japura.message;

import java.util.Collection;

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
public interface MessageManager{

  public boolean isRegistered(Subscriber subscriber);

  public void register(Subscriber subscriber);

  public void unregister(Subscriber subscriber);

  public void publish(boolean synchronous, Message message, Object publisher);

  public void publish(boolean synchronous, Message message);

  public void addMessageFilter(Subscriber subscriber, MessageFilter filter);

  public void removeMessageFilter(Subscriber subscriber, MessageFilter filter);

  public Collection<MessageFilter> getMessageFilters(Subscriber subscriber);

  public void printSubscribers();

  public int getSize();

  public boolean isPublishing();

}
