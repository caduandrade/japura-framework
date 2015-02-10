package org.japura.message;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.japura.Application;

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
public class DefaultMessageManager implements MessageManager{

  private Map<Subscriber, List<MessageFilter>> subscribers;

  public ExecutorService service = Executors.newSingleThreadExecutor();

  private AtomicInteger count;

  public DefaultMessageManager() {
	count = new AtomicInteger();
	this.subscribers = new HashMap<Subscriber, List<MessageFilter>>();
  }

  @Override
  public boolean isRegistered(Subscriber subscriber) {
	synchronized (this.subscribers) {
	  if (subscriber != null) {
		return this.subscribers.containsKey(subscriber);
	  }
	  return false;
	}
  }

  @Override
  public void register(Subscriber subscriber) {
	synchronized (this.subscribers) {
	  if (subscriber != null
		  && this.subscribers.containsKey(subscriber) == false) {
		this.subscribers.put(subscriber, new ArrayList<MessageFilter>());
	  }
	}
  }

  @Override
  public void unregister(Subscriber subscriber) {
	synchronized (this.subscribers) {
	  if (subscriber != null) {
		this.subscribers.remove(subscriber);
	  }
	}
  }

  @Override
  public void publish(boolean synchronous, Message message) {
	publish(synchronous, message, this);
  }

  @Override
  public void publish(boolean synchronous, Message message, Object publisher) {
	if (message == null) {
	  return;
	}

	if (message.isConsumed()) {
	  return;
	}

	count.incrementAndGet();

	if (publisher == null) {
	  publisher = this;
	}

	Map<Subscriber, List<MessageFilter>> copy =
		new HashMap<Subscriber, List<MessageFilter>>();

	synchronized (this.subscribers) {
	  for (Entry<Subscriber, List<MessageFilter>> entry : this.subscribers
		  .entrySet()) {
		Subscriber subscriber = entry.getKey();
		List<MessageFilter> filters = entry.getValue();
		copy.put(subscriber, new ArrayList<MessageFilter>(filters));
	  }
	}

	Publisher p = new Publisher(message, publisher, copy);
	if (synchronous) {
	  p.run();
	} else {
	  this.service.submit(p);
	}
  }

  @Override
  public void addMessageFilter(Subscriber subscriber, MessageFilter filter) {
	synchronized (this.subscribers) {
	  if (this.subscribers.containsKey(subscriber)) {
		List<MessageFilter> filters = this.subscribers.get(subscriber);
		if (filters.contains(filter) == false) {
		  filters.add(filter);
		}
	  }
	}
  }

  @Override
  public void removeMessageFilter(Subscriber subscriber, MessageFilter filter) {
	synchronized (this.subscribers) {
	  if (this.subscribers.containsKey(subscriber)) {
		List<MessageFilter> filters = this.subscribers.get(subscriber);
		filters.remove(filter);
	  }
	}
  }

  @Override
  public Collection<MessageFilter> getMessageFilters(Subscriber subscriber) {
	synchronized (this.subscribers) {
	  if (this.subscribers.containsKey(subscriber)) {
		return Collections.unmodifiableCollection(this.subscribers
			.get(subscriber));
	  }
	  return new ArrayList<MessageFilter>();
	}
  }

  @Override
  public void printSubscribers() {
	synchronized (this.subscribers) {
	  for (Subscriber subscriber : this.subscribers.keySet()) {
		System.out.println(subscriber);
	  }
	}
  }

  @Override
  public int getSize() {
	synchronized (this.subscribers) {
	  return this.subscribers.size();
	}
  }

  @Override
  public boolean isPublishing() {
	return (count.get() > 0);
  }

  private class Publisher implements Runnable{

	private Object publisher;
	private Map<Subscriber, List<MessageFilter>> subscribers;
	private Message message;

	public Publisher(Message message, Object publisher,
		Map<Subscriber, List<MessageFilter>> subscribers) {
	  this.message = message;
	  this.publisher = publisher;
	  this.subscribers = subscribers;
	}

	@Override
	public void run() {
	  try {
		for (Entry<Subscriber, List<MessageFilter>> entry : this.subscribers
			.entrySet()) {
		  Subscriber subscriber = entry.getKey();
		  List<MessageFilter> filters = entry.getValue();
		  if (message.accepts(publisher, subscriber) == false) {
			continue;
		  }

		  boolean accepts = true;
		  if (filters != null) {
			for (MessageFilter filter : filters) {
			  if (filter.accepts(message) == false) {
				accepts = false;
				break;
			  }
			}
		  }

		  if (message.isConsumed()) {
			break;
		  }

		  if (accepts) {
			subscriber.subscribe(message, publisher);
		  }

		  if (message.isConsumed()) {
			break;
		  }
		}
	  } catch (Exception e) {
		Application.getHandlerExceptionManager().handle(e);
	  } finally {
		count.decrementAndGet();
	  }
	}
  }

}
