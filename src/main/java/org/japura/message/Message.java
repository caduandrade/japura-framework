package org.japura.message;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * <P>
 * Copyright (C) 2011-2013 Carlos Eduardo Leite de Andrade
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
public class Message{

  private List<SubscriberFilter> filters;
  private int type;
  private boolean consumed;
  private boolean ignorePublisherAsSubscriber;

  public Message() {
	this(-1);
  }

  public Message(int type) {
	this.type = type;
  }

  private List<SubscriberFilter> getFilters() {
	if (filters == null) {
	  filters = new ArrayList<SubscriberFilter>();
	}
	return filters;
  }

  public boolean accepts(Object publisher, Subscriber subscriber) {
	if (subscriber == null) {
	  return false;
	}

	if (publisher == null) {
	  return false;
	}

	if (this.ignorePublisherAsSubscriber && publisher.equals(subscriber)) {
	  return false;
	}

	if (this.filters != null) {
	  for (SubscriberFilter filter : this.filters) {
		if (filter.accepts(subscriber) == false) {
		  return false;
		}
	  }
	}
	return true;
  }

  public Collection<SubscriberFilter> getSubscriberFilters() {
	return Collections.unmodifiableList(getFilters());
  }

  public void removeSubscriberFilters() {
	getFilters().clear();
  }

  public void removeSubscriberFilter(SubscriberFilter filter) {
	if (filter != null) {
	  getFilters().remove(filter);
	}
  }

  public boolean containsSubscriberFilter(SubscriberFilter filter) {
	if (filter != null) {
	  return getFilters().contains(filter);
	}
	return false;
  }

  public void addSubscriberFilter(SubscriberFilter filter) {
	if (filter != null && getFilters().contains(filter) == false) {
	  getFilters().add(filter);
	}
  }

  public int getType() {
	return type;
  }

  public void setType(int type) {
	this.type = type;
  }

  public boolean isConsumed() {
	return consumed;
  }

  public void consume() {
	this.consumed = true;
  }

  public void ignorePublisherAsSubscriber() {
	this.ignorePublisherAsSubscriber = true;
  }

  public boolean isPublisherAssignableFrom(Object publisher, Class<?> clss) {
	return clss.isAssignableFrom(publisher.getClass());
  }
}
