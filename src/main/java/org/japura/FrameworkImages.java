package org.japura;

import java.net.URL;

import org.japura.gui.GUIImages;

/**
 * Copyright (C) 2008-2013 Carlos Eduardo Leite de Andrade
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
public final class FrameworkImages{

  public static final URL WARNING = FrameworkImages.class
	  .getResource("/images/jpr_alert.png");
  public static final URL INFORMATION = FrameworkImages.class
	  .getResource("/images/jpr_info.png");
  public static final URL ERROR = FrameworkImages.class
	  .getResource("/images/jpr_error.png");
  public static final URL QUESTION = FrameworkImages.class
	  .getResource("/images/jpr_question.png");
  public static final URL PROGRESS_BAR = GUIImages.class
	  .getResource("/images/jpr_progressbar.gif");
}
