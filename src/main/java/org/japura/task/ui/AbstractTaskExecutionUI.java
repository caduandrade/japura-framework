package org.japura.task.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Timer;

import org.japura.gui.util.InputEventBlocker;
import org.japura.task.executors.ExecutionType;
import org.japura.task.session.TaskSession;

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
public abstract class AbstractTaskExecutionUI implements TaskExecutionUI{

  private int delay = 500;
  private Set<String> sessionIds;
  private Starter starter;
  private InputEventBlocker eventHook = new InputEventBlocker();
  private boolean disposeOnSessionCancel = true;
  private boolean newSession;
  private boolean ignoreParallelExecutions = true;

  public AbstractTaskExecutionUI() {
	this.sessionIds = new HashSet<String>();
  }

  public void setIgnoreParallelExecutions(boolean ignoreParallelExecutions) {
	this.ignoreParallelExecutions = ignoreParallelExecutions;
  }

  public boolean isIgnoreParallelExecutions() {
	return ignoreParallelExecutions;
  }

  public void setDisposeOnSessionCancel(boolean disposeOnSessionCancel) {
	this.disposeOnSessionCancel = disposeOnSessionCancel;
  }

  public boolean isDisposeOnSessionCancel() {
	return disposeOnSessionCancel;
  }

  /**
   * Sets a delay for the {@link #delayedStart()} method.
   * 
   * @param delay
   *          the delay
   * 
   * @see #delayedStart()
   */
  public void setDelay(int delay) {
	delay = Math.max(delay, 0);
	this.delay = delay;
  }

  /**
   * Gets the delay for the {@link #delayedStart()} method.
   * 
   * @return the delay
   * 
   * @see #delayedStart()
   */
  public int getDelay() {
	return delay;
  }

  /**
   * Invoked when the first task is submitted.
   * 
   * @param event
   */
  protected abstract void createUI(TaskExecutionUIEvent event);

  /**
   * Invoked when a new session is submitted.
   * 
   * @param event
   */
  protected abstract void updateForNewSession(TaskExecutionUIEvent event);

  /**
   * Invoked in the event dispatch thread after a specified delay whether the
   * tasks are not finished.
   * 
   * @see #getDelay()
   * @see #setDelay(int)
   */
  protected abstract void delayedStart();

  /**
   * Invoked in the event dispatch thread before a task execution.
   * 
   * @param event
   */
  protected abstract void updateForNewTask(TaskExecutionUIEvent event);

  protected abstract void updateForFinishedTask(TaskExecutionUIEvent event);

  /**
   * Invoked in the event dispatch thread after tasks executions.
   */
  protected abstract void finalizeUI();

  @Override
  public void sessionCreated(TaskExecutionUIEvent event) {
	if (isIgnoreParallelExecutions()
		&& event.getExecutionType().equals(ExecutionType.PARALLEL)) {
	  return;
	}

	boolean start = false;
	if (sessionIds.size() == 0) {
	  start = true;
	}

	this.sessionIds.add(event.getSession().getId());

	if (start) {
	  eventHook.apply();
	  createUI(event);
	  updateForNewSession(event);
	  starter = new Starter();
	  starter.start();
	}
  }

  @Override
  public void beforeTaskExecution(TaskExecutionUIEvent event) {
	if (isIgnoreParallelExecutions()
		&& event.getExecutionType().equals(ExecutionType.PARALLEL)) {
	  return;
	}

	TaskSession session = event.getSession();
	if (this.sessionIds.contains(session.getId())) {
	  if (newSession) {
		updateForNewSession(event);
		newSession = false;
	  }
	  updateForNewTask(event);
	}
  }

  @Override
  public void afterTaskExecution(TaskExecutionUIEvent event) {
	if (isIgnoreParallelExecutions()
		&& event.getExecutionType().equals(ExecutionType.PARALLEL)) {
	  return;
	}

	TaskSession session = event.getSession();
	if (this.sessionIds.contains(session.getId())) {
	  updateForFinishedTask(event);
	}
  }

  @Override
  public void sessionFinalized(TaskExecutionUIEvent event) {
	if (isIgnoreParallelExecutions()
		&& event.getExecutionType().equals(ExecutionType.PARALLEL)) {
	  return;
	}

	TaskSession session = event.getSession();
	if (this.sessionIds.contains(session.getId())) {
	  this.sessionIds.remove(session.getId());
	  if (this.sessionIds.size() == 0) {
		starter.stop();
		starter = null;
		eventHook.remove();
		finalizeUI();
	  } else {
		newSession = true;
	  }
	}
  }

  public void stopAndFinalizeUI() {
	this.sessionIds.clear();
	if (starter != null) {
	  starter.stop();
	  starter = null;
	}
	eventHook.remove();
	finalizeUI();
  }

  private class Starter extends Timer implements ActionListener{

	private static final long serialVersionUID = 1L;
	private boolean stop;

	public Starter() {
	  super(AbstractTaskExecutionUI.this.getDelay(), null);
	  addActionListener(this);
	  setRepeats(false);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	  if (stop == false) {
		AbstractTaskExecutionUI.this.delayedStart();
		eventHook.remove();
	  }
	}

	@Override
	public void stop() {
	  stop = true;
	  super.stop();
	}
  }

}
