package org.japura.task;

/**
 * <P>
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
 */
public enum TaskStatus {

  /** Initial state. */
  PENDING,
  SUBMITTED,
  EXECUTING,
  CANCELED,
  ERROR,
  DISCARDED,
  /** State after doInBackground method is finished. */
  DONE;

  public static void validate(TaskStatus oldStatus, TaskStatus newStatus) {
	if (newStatus == null) {
	  throw new TaskExeception("newStatus can't be NULL");
	}

	switch (oldStatus) {
	  case PENDING:
		switch (newStatus) {
		  case CANCELED:
			// OK
			return;
		  case SUBMITTED:
			// OK
			return;
		  case PENDING:
			invalidChange(oldStatus, newStatus);
		  case DONE:
			invalidChange(oldStatus, newStatus);
		  case ERROR:
			invalidChange(oldStatus, newStatus);
		  case DISCARDED:
			invalidChange(oldStatus, newStatus);
		  case EXECUTING:
			invalidChange(oldStatus, newStatus);
		}

	  case CANCELED:
		switch (newStatus) {
		  case CANCELED:
			invalidChange(oldStatus, newStatus);
		  case SUBMITTED:
			invalidChange(oldStatus, newStatus);
		  case PENDING:
			invalidChange(oldStatus, newStatus);
		  case DONE:
			invalidChange(oldStatus, newStatus);
		  case ERROR:
			invalidChange(oldStatus, newStatus);
		  case DISCARDED:
			invalidChange(oldStatus, newStatus);
		  case EXECUTING:
			invalidChange(oldStatus, newStatus);
		}

	  case DONE:
		switch (newStatus) {
		  case SUBMITTED:
			// OK
			return;
		  case CANCELED:
			invalidChange(oldStatus, newStatus);
		  case PENDING:
			invalidChange(oldStatus, newStatus);
		  case DONE:
			invalidChange(oldStatus, newStatus);
		  case ERROR:
			invalidChange(oldStatus, newStatus);
		  case DISCARDED:
			invalidChange(oldStatus, newStatus);
		  case EXECUTING:
			invalidChange(oldStatus, newStatus);
		}

	  case ERROR:
		switch (newStatus) {
		  case SUBMITTED:
			// OK
			return;
		  case CANCELED:
			invalidChange(oldStatus, newStatus);
		  case PENDING:
			invalidChange(oldStatus, newStatus);
		  case DONE:
			invalidChange(oldStatus, newStatus);
		  case ERROR:
			invalidChange(oldStatus, newStatus);
		  case DISCARDED:
			invalidChange(oldStatus, newStatus);
		  case EXECUTING:
			invalidChange(oldStatus, newStatus);
		}

	  case EXECUTING:
		switch (newStatus) {
		  case DONE:
			// OK
			return;
		  case ERROR:
			// OK
			return;
		  case CANCELED:
			// OK
			return;
		  case PENDING:
			invalidChange(oldStatus, newStatus);
		  case SUBMITTED:
			invalidChange(oldStatus, newStatus);
		  case DISCARDED:
			invalidChange(oldStatus, newStatus);
		  case EXECUTING:
			invalidChange(oldStatus, newStatus);
		}

	  case SUBMITTED:
		switch (newStatus) {
		  case CANCELED:
			// OK
			return;
		  case EXECUTING:
			// OK
			return;
		  case DISCARDED:
			// OK
			return;
		  case PENDING:
			invalidChange(oldStatus, newStatus);
		  case DONE:
			invalidChange(oldStatus, newStatus);
		  case ERROR:
			invalidChange(oldStatus, newStatus);
		  case SUBMITTED:
			invalidChange(oldStatus, newStatus);
		}

	  case DISCARDED:
		switch (newStatus) {
		  case SUBMITTED:
			// OK
			return;
		  case CANCELED:
			invalidChange(oldStatus, newStatus);
		  case PENDING:
			invalidChange(oldStatus, newStatus);
		  case DONE:
			invalidChange(oldStatus, newStatus);
		  case ERROR:
			invalidChange(oldStatus, newStatus);
		  case DISCARDED:
			invalidChange(oldStatus, newStatus);
		  case EXECUTING:
			invalidChange(oldStatus, newStatus);
		}
	}

	// /CLOVER:OFF
	throw new TaskExeception("Status not supported at "
		+ TaskStatus.class.getName());
	// /CLOVER:ON
  }

  private static void invalidChange(TaskStatus oldStatus, TaskStatus newStatus) {
	throw new TaskExeception("It's not possible change from status "
		+ oldStatus + " to " + newStatus);
  }

}
