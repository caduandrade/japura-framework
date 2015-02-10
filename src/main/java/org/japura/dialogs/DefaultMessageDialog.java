package org.japura.dialogs;

import java.awt.Component;
import java.awt.Image;
import java.awt.Window;
import java.util.Collections;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JDialog;

import org.japura.i18n.FrameworkStringKeys;
import org.japura.util.i18n.I18nAdapter;

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
public class DefaultMessageDialog extends JDialog{
  private static final long serialVersionUID = 2L;

  private Object result;
  private List<MessageDialogButton> buttons;
  private MessagePanel messagePanel;

  public DefaultMessageDialog(Window owner, String title, Image dialogIcon,
	  Image icon, String msg, String detailedMsg,
	  List<MessageDialogButton> buttons) {
	super(owner);
	this.buttons = Collections.unmodifiableList(buttons);
	setTitle(title);

	String detailedMsgTitle = null;
	if (detailedMsg != null) {
	  detailedMsgTitle =
		  I18nAdapter.getAdapter().getString(FrameworkStringKeys.DETAILS.getKey());
	}
	messagePanel = new MessagePanel(msg, detailedMsgTitle, detailedMsg);
	if (icon != null) {
	  messagePanel.setIcon(new ImageIcon(icon));
	}

	for (MessageDialogButton mdb : buttons) {
	  mdb.initializeButton(this);
	  messagePanel.getButtonsPanel().add(mdb.getButton());
	}

	add(messagePanel);
	setModal(true);
	setResizable(false);

	if (dialogIcon != null) {
	  setIconImage(dialogIcon);
	}
	pack();
	pack();

	setLocationRelativeTo(owner);
  }

  public void addContent(Component content, int topMargin) {
	messagePanel.addContent(content, topMargin);
	pack();
	pack();
  }

  public void addContent(Component content) {
	messagePanel.addContent(content);
	pack();
	pack();
  }

  void setResult(Object result) {
	this.result = result;
  }

  public Object getResult() {
	return result;
  }

  public List<MessageDialogButton> getButtons() {
	return buttons;
  }
}
