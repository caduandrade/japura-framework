package org.japura.dialogs;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Scrollable;

import org.japura.gui.LabelSeparator;

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
public class MessagePanel extends ContentPanel{

  private static final long serialVersionUID = 1L;

  public MessagePanel(String msg) {
	this(msg, null, null);
  }

  public MessagePanel(String msg, String detailedTitle, String detailedMsg) {
	InnerPanel panel = new InnerPanel();
	InnerScrollPane sp = new InnerScrollPane(panel);

	sp.getViewport().setOpaque(false);
	sp.setOpaque(false);
	sp.setBorder(BorderFactory.createEmptyBorder());
	addContent(sp);

	panel.textArea.setText(msg);
	if (detailedTitle != null && detailedMsg != null) {
	  panel.separator.setText(detailedTitle);
	  panel.detailedTextArea.setText(detailedMsg);
	} else {
	  panel.separator.setVisible(false);
	  panel.detailedTextArea.setVisible(false);
	}
  }

  private static class InnerScrollPane extends JScrollPane{

	private static final long serialVersionUID = 1L;

	private InnerScrollPane(Component view) {
	  super(view);
	}

	@Override
	public Dimension getPreferredSize() {
	  Dimension dim = super.getPreferredSize();
	  if (isPreferredSizeSet() == false) {
		dim.height = Math.min(dim.height, 100);
	  }
	  return dim;
	}
  }

  private static class InnerPanel extends JPanel implements Scrollable{

	private static final long serialVersionUID = 1L;
	private LabelSeparator separator = new LabelSeparator();
	private JTextArea textArea;
	private JTextArea detailedTextArea;

	public InnerPanel() {
	  setOpaque(false);
	  textArea = buildTextArea();
	  detailedTextArea = buildTextArea();

	  setLayout(new GridBagLayout());
	  GridBagConstraints gbc = new GridBagConstraints();
	  gbc.gridx = 0;
	  gbc.gridy = 0;
	  gbc.weightx = 1;
	  gbc.weighty = 1;
	  gbc.fill = GridBagConstraints.HORIZONTAL;
	  add(textArea, gbc);
	  gbc.weighty = 0;
	  gbc.gridy++;
	  gbc.insets = new Insets(0, 5, 0, 5);
	  add(separator, gbc);
	  gbc.gridy++;
	  gbc.insets = new Insets(0, 0, 0, 0);
	  add(detailedTextArea, gbc);
	}

	private JTextArea buildTextArea() {
	  JTextArea textArea = new JTextArea(0, 30);
	  textArea.setWrapStyleWord(true);
	  textArea.setActionMap(new ActionMap());
	  textArea.setEditable(false);
	  textArea.setOpaque(false);
	  textArea.setLineWrap(true);
	  textArea.setMargin(new Insets(5, 5, 5, 5));
	  return textArea;
	}

	@Override
	public Dimension getPreferredScrollableViewportSize() {
	  return getPreferredSize();
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect,
										  int orientation, int direction) {
	  return 10;
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect,
										   int orientation, int direction) {
	  return 100;
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
	  return true;
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {
	  return false;
	}

  }

}
