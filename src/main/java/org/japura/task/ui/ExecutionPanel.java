package org.japura.task.ui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.Border;

import org.japura.FrameworkImages;
import org.japura.gui.LinkLabel;
import org.japura.gui.WrapLabel;
import org.japura.i18n.FrameworkStringKeys;
import org.japura.util.i18n.I18nAdapter;

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
public class ExecutionPanel extends JPanel{

  private static final long serialVersionUID = 3;
  private WrapLabel taskMessageLabel;
  private WrapLabel messageLabel;
  private JLabel timerLabel;
  private long elapsedStartTime = 0;
  private DecimalFormat integerTimeFormatter;
  private int width = 300;
  private String taskMessage;
  private String message;
  private StringBuilder textTimer;
  private Timer timer;
  private LinkLabel cancelLink;

  public ExecutionPanel(String message) {
	this(message, null);
  }

  public ExecutionPanel(String message, Icon progressBarIcon) {
	if (progressBarIcon == null) {
	  URL url = FrameworkImages.PROGRESS_BAR;
	  progressBarIcon = new ImageIcon(url);
	}
	textTimer = new StringBuilder();
	integerTimeFormatter = new DecimalFormat("00");
	this.taskMessage = "";
	if (message == null) {
	  message = "";
	}
	this.message = message;

	Border out = BorderFactory.createLineBorder(Color.BLACK, 2);
	Border in = BorderFactory.createEmptyBorder(8, 8, 8, 8);
	setBorder(BorderFactory.createCompoundBorder(out, in));
	setBackground(Color.WHITE);
	setLayout(new GridBagLayout());

	GridBagConstraints gbc = new GridBagConstraints();
	gbc.gridx = 0;
	gbc.gridy = 0;
	gbc.anchor = GridBagConstraints.NORTHWEST;
	gbc.weighty = 1;
	gbc.weightx = 1;
	add(getMessageLabel(), gbc);

	gbc.gridx = 0;
	gbc.gridy = 1;
	gbc.anchor = GridBagConstraints.NORTHWEST;
	gbc.weighty = 1;
	gbc.weightx = 1;
	add(getTaskMessageLabel(), gbc);

	gbc.insets = new Insets(20, 0, 0, 0);
	gbc.gridx = 0;
	gbc.gridy = 2;
	gbc.anchor = GridBagConstraints.SOUTH;
	gbc.weighty = 1;
	gbc.weightx = 1;

	add(getTimeLabel(), gbc);

	gbc.insets = new Insets(0, 0, 0, 0);
	gbc.gridx = 0;
	gbc.gridy = 3;
	gbc.anchor = GridBagConstraints.CENTER;
	gbc.weightx = 1;

	JLabel progressBar = new JLabel(progressBarIcon);
	progressBar.setName("progressComponent");
	add(progressBar, gbc);

	gbc.insets = new Insets(0, 0, 0, 0);
	gbc.gridx = 0;
	gbc.gridy = 4;
	gbc.anchor = GridBagConstraints.CENTER;
	gbc.weightx = 1;

	add(getCancelLink(), gbc);
  }

  public void setCancelLinkListener(ActionListener listener) {
	ActionListener[] listeners =
		getCancelLink().getListeners(ActionListener.class);
	for (ActionListener currentListener : listeners) {
	  getCancelLink().removeActionListener(currentListener);
	}
	getCancelLink().addActionListener(listener);
  }

  public LinkLabel getCancelLink() {
	if (cancelLink == null) {
	  cancelLink = new LinkLabel();
	  String text =
		  I18nAdapter.getAdapter().getString(
			  FrameworkStringKeys.TASK_UI_CANCEL_LINK.getKey());
	  cancelLink.setText(text);
	  cancelLink.setVisible(false);
	}
	return cancelLink;
  }

  public void setTaskMessage(String text) {
	getTaskMessageLabel().setText(text);
  }

  public void setMessage(String text) {
	getMessageLabel().setText(text);
  }

  private JLabel getTimeLabel() {
	if (timerLabel == null) {
	  timerLabel = new JLabel();
	  timerLabel.setName("timerLabel");
	  timerLabel.setText(getTimerString(0));
	}
	return timerLabel;
  }

  protected Timer getTimer() {
	if (timer == null) {
	  timer = new Timer(1000, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
		  long seconds = System.currentTimeMillis() - elapsedStartTime;
		  getTimeLabel().setText(getTimerString(seconds));
		}
	  });
	}
	return timer;
  }

  public void start() {
	elapsedStartTime = System.currentTimeMillis();
	getTimer().start();
  }

  public void stop() {
	getTimer().stop();
  }

  private WrapLabel getMessageLabel() {
	if (messageLabel == null) {
	  messageLabel = new WrapLabel(message);
	  messageLabel.setWrapWidth(width);
	}
	return messageLabel;
  }

  private WrapLabel getTaskMessageLabel() {
	if (taskMessageLabel == null) {
	  taskMessageLabel = new WrapLabel(taskMessage);
	  taskMessageLabel.setWrapWidth(width);
	  taskMessageLabel.setName("messageLabel");
	}
	return taskMessageLabel;
  }

  private String getTimerString(long seconds) {
	if (seconds < 0) {
	  return "-";
	}
	seconds = seconds / 1000L;

	textTimer.setLength(0);
	textTimer.append(I18nAdapter.getAdapter().getString(
		FrameworkStringKeys.TIME_ELAPSED.getKey()));
	textTimer.append(" ");

	long sec = seconds;
	long min = 0;
	long hou = 0;
	if (sec >= 60) {
	  min = (int) sec / 60;
	  sec = sec - (min * 60);
	}
	if (min >= 60) {
	  hou = (int) min / 60;
	  min = min - (hou * 60);
	}
	textTimer.append(integerTimeFormatter.format(hou));
	textTimer.append(I18nAdapter.getAdapter().getString(
		FrameworkStringKeys.HOUR_ACRONYM.getKey()));
	textTimer.append(" ");
	textTimer.append(integerTimeFormatter.format(min));
	textTimer.append(I18nAdapter.getAdapter().getString(
		FrameworkStringKeys.MINUTE_ACRONYM.getKey()));
	textTimer.append(" ");
	textTimer.append(integerTimeFormatter.format(sec));
	textTimer.append(I18nAdapter.getAdapter().getString(
		FrameworkStringKeys.SECOND_ACRONYM.getKey()));

	return textTimer.toString();
  }

}
