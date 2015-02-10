package org.japura.dialogs;

import javax.swing.JButton;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
public class MessageDialogButton{
  private ResultProvider resultProvider;
  private JButton button;

  public MessageDialogButton(JButton button) {
	this.button = button;
  }

  public MessageDialogButton(JButton button, ResultProvider resultProvider) {
	this(button);
	this.resultProvider = resultProvider;
  }

  public MessageDialogButton(JButton button, Object result) {
	this(button);
	resultProvider = new UserResultProvider(result);
  }

  public MessageDialogButton(JButton button, JTextComponent textComponent,
	  boolean required, boolean applyTrim) {
	this(button);

	resultProvider = new TextComponentResultProvider(textComponent, applyTrim);

	if (required) {
	  textComponent.getDocument().addDocumentListener(new DocumentListener() {
		@Override
		public void removeUpdate(DocumentEvent e) {
		  ((TextComponentResultProvider) resultProvider).update(getButton());
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
		  ((TextComponentResultProvider) resultProvider).update(getButton());
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
		  ((TextComponentResultProvider) resultProvider).update(getButton());
		}
	  });
	  ((TextComponentResultProvider) resultProvider).update(getButton());
	}
  }

  public Object getResult() {
	if (resultProvider != null) {
	  return resultProvider.getResult();
	}
	return null;
  }

  public JButton getButton() {
	return button;
  }

  public void initializeButton(final DefaultMessageDialog dialog) {
	getButton().addActionListener(new ActionListener() {

	  @Override
	  public void actionPerformed(ActionEvent e) {
		dialog.setResult(getResult());
		dialog.dispose();
	  }
	});
  }

  private static class TextComponentResultProvider implements ResultProvider{
	private JTextComponent component;
	private boolean applyTrim;

	private TextComponentResultProvider(JTextComponent component,
		boolean applyTrim) {
	  this.applyTrim = applyTrim;
	  this.component = component;
	}

	@Override
	public Object getResult() {
	  String text = component.getText();
	  if (applyTrim) {
		text = text.trim();
	  }
	  return text;
	}

	public void update(JButton button) {
	  boolean enabled = true;
	  String text = (String) getResult();
	  if (text.length() == 0) {
		enabled = false;
	  }
	  button.setEnabled(enabled);
	}
  }

  private static class UserResultProvider implements ResultProvider{
	private Object result;

	public UserResultProvider(Object result) {
	  this.result = result;
	}

	@Override
	public Object getResult() {
	  return result;
	}
  }

  public static interface ResultProvider{
	public Object getResult();
  }
}
