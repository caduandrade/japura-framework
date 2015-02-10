package org.japura.dialogs;

import org.japura.FrameworkImages;
import org.japura.i18n.FrameworkStringKeys;
import org.japura.util.i18n.I18nAdapter;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

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
public class DefaultMessageDialogs implements MessageDialogs {

  private ButtonFactory buttonFactory = new DefaultButtonFactory();

  public DefaultMessageDialog buildDialog(Component owner, String title,
    Image dialogIcon, Image icon, String msg, String detailedMsg,
    List<MessageDialogButton> buttons, DialogConfiguration dialogConfiguration) {
    Window window = null;
    if (owner != null) {
      window = SwingUtilities.getWindowAncestor(owner);
    }
    return new DefaultMessageDialog(window, title, dialogIcon, icon, msg,
      detailedMsg, buttons);
  }

  public void setButtonFactory(ButtonFactory buttonFactory) {
    if (buttonFactory != null) {
      this.buttonFactory = buttonFactory;
    }
  }

  public ButtonFactory getButtonFactory() {
    return buttonFactory;
  }

  @Override
  public int showDialog(Component owner, Image icon, String title, String msg,
    String detailedMsg, String... buttons) {
    if (buttons.length == 0) {
      throw new RuntimeException("No buttons defined.");
    }

    Image dialogIcon = icon;

    List<MessageDialogButton> list = new ArrayList<MessageDialogButton>();

    for (int i = 0; i < buttons.length; i++) {
      list.add(new MessageDialogButton(getButtonFactory().buildButton(
        buttons[i], i), new Integer(i)));
    }

    DefaultMessageDialog dialog =
      buildDialog(owner, title, dialogIcon, icon, msg, detailedMsg, list,
        new DialogConfiguration());

    dialog.setVisible(true);

    Integer result = (Integer) dialog.getResult();
    return result.intValue();
  }

  @Override
  public Boolean showDialog(MessageType messageType, PromptType promptType,
    Component owner, String title, String msg, String detailedMsg,
    String firstButton, String secondButton, String thirdButton,
    DialogConfiguration dialogConfiguration) {
	  if (dialogConfiguration==null) {
		  dialogConfiguration=new DialogConfiguration();
	  }

    if (promptType.equals(PromptType.YES) && firstButton == null) {
      throw new RuntimeException("First button text non defined. PromptType: "
        + promptType.name());
    }

    if (promptType.equals(PromptType.YES_NO)
      && (firstButton == null || secondButton == null)) {
      throw new RuntimeException(
        "First or second button text non defined. PromptType: "
          + promptType.name());
    }

    if (promptType.equals(PromptType.YES_NO_CANCEL)
      && (firstButton == null || secondButton == null || thirdButton == null)) {
      throw new RuntimeException(
        "First, second or third button text non defined. PromptType: "
          + promptType.name());
    }

    Image icon = getIcon(messageType);
    Image dialogIcon = icon;

    List<MessageDialogButton> buttons = new ArrayList<MessageDialogButton>();
    if (promptType.equals(PromptType.YES)) {
      buttons.add(new MessageDialogButton(getButtonFactory().buildButton(
        firstButton, 0), new Boolean(true)));
    }
    else if (promptType.equals(PromptType.YES_NO)) {
      buttons.add(new MessageDialogButton(getButtonFactory().buildButton(
        firstButton, 0), new Boolean(true)));
      buttons.add(new MessageDialogButton(getButtonFactory().buildButton(
        secondButton, 1), new Boolean(false)));
    }
    else if (promptType.equals(PromptType.YES_NO_CANCEL)) {
      buttons.add(new MessageDialogButton(getButtonFactory().buildButton(
        firstButton, 0), new Boolean(true)));
      buttons.add(new MessageDialogButton(getButtonFactory().buildButton(
        secondButton, 1), new Boolean(false)));
      buttons.add(new MessageDialogButton(getButtonFactory().buildButton(
        thirdButton, 2), null));
    }

    DefaultMessageDialog dialog =
      buildDialog(owner, title, dialogIcon, icon, msg, detailedMsg, buttons,
        dialogConfiguration);

	  for(int i = 0 ; i < dialog.getButtons().size() ;i++){
		  if (i==dialogConfiguration.getFocusedButton()) {
			  dialog.getButtons().get(i).getButton().requestFocus();
		  }
		  final int actionKey = dialogConfiguration.getButtonActionKey(i);
		  if(actionKey>-1) {
			final JButton button = dialog.getButtons().get(i).getButton();
			  KeyListener listener = new KeyAdapter() {
				  @Override
				  public void keyPressed(KeyEvent e) {
					  if (e.getKeyCode() == actionKey) {
						  button.doClick();
					  }
				  }
			  };
			  button.addKeyListener(listener);
		  }
	  }

    dialog.setVisible(true);

    return (Boolean) dialog.getResult();
  }


  private Image getIcon(MessageType messageType) {
    if (messageType.equals(MessageType.ERROR)) {
      return new ImageIcon(FrameworkImages.ERROR).getImage();
    }
    else if (messageType.equals(MessageType.INFO)) {
      return new ImageIcon(FrameworkImages.INFORMATION).getImage();
    }
    else if (messageType.equals(MessageType.QUESTION)) {
      return new ImageIcon(FrameworkImages.QUESTION).getImage();
    }
    else if (messageType.equals(MessageType.WARN)) {
      return new ImageIcon(FrameworkImages.WARNING).getImage();
    }
    return null;
  }

  @Override
  public Boolean showQuestionDialog(Component owner, String title, String msg,
    String detailedMsg, DialogConfiguration dialogConfiguration) {
    String yes =
      I18nAdapter.getAdapter().getString(FrameworkStringKeys.YES.getKey());
    String no =
      I18nAdapter.getAdapter().getString(FrameworkStringKeys.NO.getKey());
    return showDialog(MessageType.QUESTION, PromptType.YES_NO, owner, title,
      msg, detailedMsg, yes, no, null, dialogConfiguration);
  }

  @Override
  public Boolean showQuestionDialog(Component owner, String title, String msg,
    String detailedMsg, String yesButtonText, String noButtonText,
    DialogConfiguration dialogConfiguration) {
    return showDialog(MessageType.QUESTION, PromptType.YES_NO, owner, title,
      msg, detailedMsg, yesButtonText, noButtonText, null, dialogConfiguration);
  }

  @Override
  public Boolean showWarnDialog(Component owner, String title, String msg,
    String detailedMsg, DialogConfiguration dialogConfiguration) {
    String close =
      I18nAdapter.getAdapter().getString(FrameworkStringKeys.CLOSE.getKey());
    return showDialog(MessageType.WARN, PromptType.YES, owner, title, msg,
      detailedMsg, close, null, null, dialogConfiguration);
  }

  @Override
  public Boolean showErrorDialog(Component owner, String title, String msg,
    String detailedMsg, DialogConfiguration dialogConfiguration) {
    String close =
      I18nAdapter.getAdapter().getString(FrameworkStringKeys.CLOSE.getKey());
    return showDialog(MessageType.ERROR, PromptType.YES, owner, title, msg,
      detailedMsg, close, null, null, dialogConfiguration);
  }

  @Override
  public Boolean showInfoDialog(Component owner, String title, String msg,
    String detailedMsg, DialogConfiguration dialogConfiguration) {
    String close =
      I18nAdapter.getAdapter().getString(FrameworkStringKeys.CLOSE.getKey());
    return showDialog(MessageType.INFO, PromptType.YES, owner, title, msg,
      detailedMsg, close, null, null, dialogConfiguration);
  }

  public static class DefaultButtonFactory implements ButtonFactory {
    @Override
    public JButton buildButton(String buttonText, int buttonOrder) {
      return new JButton(buttonText);
    }
  }

}
