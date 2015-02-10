package org.japura.dialogs;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Copyright (C) 2012 Carlos Eduardo Leite de Andrade
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
public class ContentPanel extends JPanel{

  private static final long serialVersionUID = 1L;

  private ButtonPanel buttonsPanel;
  private Color buttonBackground;
  private JLabel iconLabel;
  private LinkedHashMap<Component, Integer> contents;
  private int defaultGap = 7;
  private int margin = 7;

  public ContentPanel() {
	buttonBackground = new Color(215, 215, 215);
	setBackground(new Color(245, 245, 245));
	contents = new LinkedHashMap<Component, Integer>();
	buttonsPanel = new ButtonPanel();
	buttonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
	super.add(buttonsPanel);
  }

  public final JPanel getButtonsPanel() {
	return buttonsPanel;
  }

  public void setIcon(Icon icon) {
	if (icon != null) {
	  if (iconLabel == null) {
		iconLabel = new JLabel();
		super.add(iconLabel);
	  }
	  iconLabel.setIcon(icon);
	} else if (iconLabel != null) {
	  super.remove(iconLabel);
	}
  }

  public void addContent(Component content) {
	addContent(content, defaultGap);
  }

  public void addContent(Component content, int topMargin) {
	super.add(content);
	contents.put(content, Math.max(topMargin, 0));
  }

  public void removeContent(Component content) {
	super.remove(content);
	contents.remove(content);
  }

  @Override
  public Component add(Component comp) {
	addContent(comp);
	return comp;
  }

  @Override
  public Component add(Component comp, int index) {
	addContent(comp);
	return comp;
  }

  @Override
  public void add(Component comp, Object constraints) {
	addContent(comp);
  }

  @Override
  public void add(Component comp, Object constraints, int index) {
	addContent(comp);
  }

  @Override
  public void remove(Component comp) {
	removeContent(comp);
  }

  @Override
  public void remove(int index) {}

  @Override
  public void removeAll() {}

  @Override
  public final void doLayout() {
	int width = getWidth();
	int height = getHeight();
	int x = margin;
	int y = margin;
	if (iconLabel != null) {
	  Dimension iconDim = iconLabel.getPreferredSize();
	  iconLabel.setBounds(x, y, iconDim.width, iconDim.height);
	  x += iconDim.width + defaultGap;
	  width -= iconDim.width + defaultGap;
	}

	if (buttonsPanel.isVisible()) {
	  Dimension bpDim = buttonsPanel.getPreferredSize();
	  buttonsPanel
		  .setBounds(0, height - bpDim.height, getWidth(), bpDim.height);
	  height -= bpDim.height;
	}

	height -= margin;
	width -= (2 * margin);

	for (Component content : contents.keySet()) {
	  content.setBounds(0, 0, 0, 0);
	}

	boolean first = true;
	for (Entry<Component, Integer> entry : contents.entrySet()) {
	  Component content = entry.getKey();
	  int gap = entry.getValue();
	  Dimension dim = content.getPreferredSize();
	  if (y > height + gap) {
		break;
	  }

	  if (first == false) {
		y += gap;
	  }
	  int limitHeight = height - y;
	  content.setBounds(x, y, width, Math.min(dim.height, limitHeight));
	  y += dim.height;
	  first = false;
	}
  }

  @Override
  public Dimension getPreferredSize() {
	if (isPreferredSizeSet()) {
	  return super.getPreferredSize();
	}
	Dimension dim = new Dimension(2 * margin, 2 * margin);

	int max = 0;
	boolean first = true;
	for (Entry<Component, Integer> entry : contents.entrySet()) {
	  Component content = entry.getKey();
	  int gap = entry.getValue();
	  Dimension contentDim = content.getPreferredSize();
	  max = Math.max(max, contentDim.width);
	  dim.height += contentDim.height;
	  if (first == false) {
		dim.height += gap;
	  }
	  first = false;
	}
	dim.width += max;

	if (iconLabel != null) {
	  Dimension iconDim = iconLabel.getPreferredSize();
	  dim.height = Math.max(dim.height, iconDim.height);
	  dim.width += defaultGap + iconDim.width;
	}

	if (buttonsPanel.isVisible()) {
	  Dimension bpDim = buttonsPanel.getPreferredSize();
	  dim.width = Math.max(dim.width, bpDim.width);
	  dim.height += bpDim.height;
	}
	return dim;
  }

  private class ButtonPanel extends JPanel{

	private static final long serialVersionUID = 1L;

	@Override
	protected void paintComponent(Graphics g) {
	  if (isOpaque()) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setPaint(new GradientPaint(new Point2D.Double(0, 0),
			ContentPanel.this.getBackground(),
			new Point2D.Double(getWidth(), 0), buttonBackground));
		g2d.fillRect(0, 0, getWidth(), getHeight());

		g2d.setPaint(new GradientPaint(new Point2D.Double(getWidth(), 0),
			Color.gray, new Point2D.Double(0, 0), ContentPanel.this
				.getBackground()));
		g2d.fillRect(0, 0, getWidth(), 1);
	  }
	}
  }

}
