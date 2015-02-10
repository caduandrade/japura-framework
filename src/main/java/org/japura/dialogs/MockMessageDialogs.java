package org.japura.dialogs;

import java.awt.Component;
import java.awt.Image;

/**
 * <P>
 * Copyright (C) 2014 Carlos Eduardo Leite de Andrade
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
public class MockMessageDialogs implements MessageDialogs{

  private int count;
  private Boolean returnBooleanValue;
  private int returnIntValue;

  public Boolean getReturnBooleanValue() {
	return returnBooleanValue;
  }

  public int getReturnIntValue() {
	return returnIntValue;
  }

  public void setReturnIntValue(int returnIntValue) {
	this.returnIntValue = returnIntValue;
  }

  public void setReturnBooleanValue(Boolean returnBooleanValue) {
	this.returnBooleanValue = returnBooleanValue;
  }


  @Override
  public Boolean showQuestionDialog(Component owner, String title, String msg,
									String detailedMsg, DialogConfiguration dialogConfiguration) {
	this.count++;
	return getReturnBooleanValue();
  }

  @Override
  public Boolean showQuestionDialog(Component owner, String title, String msg,
									String detailedMsg, String yesButtonText,
									String noButtonText, DialogConfiguration dialogConfiguration) {
	this.count++;
	return getReturnBooleanValue();
  }

  @Override
  public Boolean showWarnDialog(Component owner, String title, String msg,
								String detailedMsg, DialogConfiguration dialogConfiguration) {
	this.count++;
	return getReturnBooleanValue();
  }


  @Override
  public Boolean showErrorDialog(Component owner, String title, String msg,
								 String detailedMsg, DialogConfiguration dialogConfiguration) {
	this.count++;
	return getReturnBooleanValue();
  }


  @Override
  public Boolean showInfoDialog(Component owner, String title, String msg,
								String detailedMsg, DialogConfiguration dialogConfiguration) {
	this.count++;
	return getReturnBooleanValue();
  }


  @Override
  public Boolean showDialog(MessageType messageType, PromptType promptType,
							Component owner, String title, String msg,
							String detailedMsg, String firstButton,
							String secondButton, String thirdButton, DialogConfiguration dialogConfiguration) {
	this.count++;
	return getReturnBooleanValue();
  }


  @Override
  public int showDialog(Component owner, Image icon, String title, String msg,
						String detailedMsg, String... buttons) {
	this.count++;
	return getReturnIntValue();
  }

  public int getCount() {
	return count;
  }

  public void clearCount() {
	this.count = 0;
  }

}
