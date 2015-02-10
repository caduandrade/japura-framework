package org.japura.exception;

import java.util.HashMap;
import java.util.Map;

/**
 * Pool of handler exception.
 * <P>
 * Registers handler for a specific or super class exception.
 * <P>
 * Copyright (C) 2009-2013 Carlos Eduardo Leite de Andrade
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
public final class DefaultHandlerExceptionManager implements
	HandlerExceptionManager{

  private static HashMap<String, HandlerException> handlers =
	  new HashMap<String, HandlerException>();

  /**
   * {@inheritDoc}
   */
  @Override
  public void register(Class<? extends Throwable> throwableClass,
					   HandlerException handler) {
	if (throwableClass != null && handler != null) {
	  handlers.put(throwableClass.getName(), handler);
	}
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void handle(Throwable throwable) {
	handle(throwable, new HashMap<String, Object>());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void handle(Throwable throwable, Map<String, Object> parameters) {
	if (handlers.size() == 0) {
	  register(Exception.class, new HandlerException() {
		@Override
		public void handle(Throwable throwable,
						   Map<String, Object> parameters) {
		  throwable.printStackTrace();
		}
	  });
	}

	if (throwable != null) {
	  Class<?> throwableClass = throwable.getClass();
	  while (throwableClass != null) {
		if (handlers.containsKey(throwableClass.getName())) {
		  HandlerException handler = handlers.get(throwableClass.getName());
		  if (parameters == null) {
			parameters = new HashMap<String, Object>();
		  }
		  handler.handle(throwable, parameters);
		  return;
		} else {
		  throwableClass = throwableClass.getSuperclass();
		}
	  }
	}
  }

}
