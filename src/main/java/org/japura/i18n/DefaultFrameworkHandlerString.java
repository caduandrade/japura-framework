package org.japura.i18n;

import org.japura.util.i18n.HandlerString;

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
public class DefaultFrameworkHandlerString implements HandlerString{

  @Override
  public String getString(String key) {
	if (key == null) {
	  return null;
	}
	if (FrameworkStringKeys.CANCEL.getKey().equals(key)) {
	  return "Cancel";
	}
	if (FrameworkStringKeys.CLOSE.getKey().equals(key)) {
	  return "Close";
	}
	if (FrameworkStringKeys.CONFIRM.getKey().equals(key)) {
	  return "Confirm";
	}
	if (FrameworkStringKeys.DETAILS.getKey().equals(key)) {
	  return "Details";
	}
	if (FrameworkStringKeys.NO.getKey().equals(key)) {
	  return "No";
	}
	if (FrameworkStringKeys.YES.getKey().equals(key)) {
	  return "Yes";
	}
	if (FrameworkStringKeys.TIME_ELAPSED.getKey().equals(key)) {
	  return "Time spent";
	}
	if (FrameworkStringKeys.TIME_ELAPSED.getKey().equals(key)) {
	  return "";
	}
	if (FrameworkStringKeys.HOUR_ACRONYM.getKey().equals(key)) {
	  return "h";
	}
	if (FrameworkStringKeys.MINUTE_ACRONYM.getKey().equals(key)) {
	  return "m";
	}
	if (FrameworkStringKeys.SECOND_ACRONYM.getKey().equals(key)) {
	  return "s";
	}
	if (FrameworkStringKeys.TASK_UI_CANCEL_LINK.getKey().equals(key)) {
	  return "Cancel";
	}
	return null;
  }

}
