package org.japura.controller.messages;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import org.japura.message.Message;

/**
 * 
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
 * 
 */
public class ScreenShotMessage extends Message{

  private BufferedImage screenShot;
  private JLabel warningLabel;

  public void perform(Component component) {
	if (component != null) {
	  printOnImage(component);
	}
  }

  private void printOnImage(Component component) {
	if (component.getWidth() > 0 && component.getHeight() > 0) {
	  screenShot =
		  new BufferedImage(component.getWidth(), component.getHeight(),
			  BufferedImage.TYPE_INT_ARGB);
	  Graphics2D g2 = (Graphics2D) screenShot.getGraphics();
	  component.print(g2);
	} else {
	  screenShot = null;
	}
  }

  private JLabel getWarningLabel() {
	if (warningLabel == null) {
	  warningLabel = new JLabel();
	  warningLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	  warningLabel.setOpaque(false);
	  warningLabel.setText("Unavailable image");
	  warningLabel.setSize(warningLabel.getPreferredSize());
	}
	return warningLabel;
  }

  public void clearScreenShot() {
	this.screenShot = null;
  }

  public BufferedImage getScreenShot() {
	if (screenShot == null) {
	  printOnImage(getWarningLabel());
	}
	return screenShot;
  }

}
