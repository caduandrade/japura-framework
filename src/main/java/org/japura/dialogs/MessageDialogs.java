package org.japura.dialogs;

import java.awt.Component;
import java.awt.Image;

/**
 * <P>
 * Copyright (C) 2012-2014 Carlos Eduardo Leite de Andrade
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
public interface MessageDialogs {

  public Boolean showQuestionDialog(Component owner, String title, String msg,
    String detailedMsg, DialogConfiguration dialogConfiguration);

  public Boolean showQuestionDialog(Component owner, String title, String msg,
    String detailedMsg, String yesButtonText, String noButtonText,
    DialogConfiguration dialogConfiguration);

  public Boolean showWarnDialog(Component owner, String title, String msg,
    String detailedMsg, DialogConfiguration dialogConfiguration);

  public Boolean showErrorDialog(Component owner, String title, String msg,
    String detailedMsg, DialogConfiguration dialogConfiguration);

  public Boolean showInfoDialog(Component owner, String title, String msg,
    String detailedMsg, DialogConfiguration dialogConfiguration);

  public Boolean showDialog(MessageType messageType, PromptType promptType,
    Component owner, String title, String msg, String detailedMsg,
    String firstButton, String secondButton, String thirdButton,
    DialogConfiguration dialogConfiguration);

  public int showDialog(Component owner, Image icon, String title, String msg,
    String detailedMsg, String... buttons);
}
