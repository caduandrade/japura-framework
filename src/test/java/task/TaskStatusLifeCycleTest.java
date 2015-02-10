package task;

import org.japura.Application;
import org.japura.task.Task;
import org.japura.task.TaskExeception;
import org.japura.task.TaskStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TaskStatusLifeCycleTest{

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Before
  public void init() {
	Application.reset();
  }

  @Test
  public void pendingToNull() {
	Task task = new Task();
	Assert.assertEquals(TaskStatus.PENDING, task.getStatus());
	thrown.expect(TaskExeception.class);
	task.registerStatus(null);
  }

  @Test
  public void pendingToSubmitted() {
	Task task = new Task();
	Assert.assertEquals(TaskStatus.PENDING, task.getStatus());
	task.registerStatus(TaskStatus.SUBMITTED);
	Assert.assertEquals(TaskStatus.SUBMITTED, task.getStatus());
  }

  @Test
  public void pendingToCancelled() {
	Task task = new Task();
	Assert.assertEquals(TaskStatus.PENDING, task.getStatus());
	task.registerStatus(TaskStatus.CANCELED);
	Assert.assertEquals(TaskStatus.CANCELED, task.getStatus());
  }

  @Test()
  public void pendingToExecuting() {
	Task task = new Task();
	thrown.expect(TaskExeception.class);
	task.registerStatus(TaskStatus.EXECUTING);
  }

  @Test()
  public void pendingToDiscarded() {
	Task task = new Task();
	thrown.expect(TaskExeception.class);
	task.registerStatus(TaskStatus.DISCARDED);
  }

  @Test()
  public void pendingToError() {
	Task task = new Task();
	thrown.expect(TaskExeception.class);
	task.registerStatus(TaskStatus.ERROR);
  }

  @Test(expected = TaskExeception.class)
  public void pendingToDone() {
	Task task = new Task();
	task.registerStatus(TaskStatus.DONE);
  }

  @Test(expected = TaskExeception.class)
  public void pendingToPending() {
	Task task = new Task();
	task.registerStatus(TaskStatus.PENDING);
  }

  @Test
  public void submittedToExecuting() {
	Task task = new Task();
	task.registerStatus(TaskStatus.SUBMITTED);
	task.registerStatus(TaskStatus.EXECUTING);
	Assert.assertEquals(TaskStatus.EXECUTING, task.getStatus());
  }

  @Test
  public void submittedToCanceled() {
	Task task = new Task();
	task.registerStatus(TaskStatus.SUBMITTED);
	task.registerStatus(TaskStatus.CANCELED);
	Assert.assertEquals(TaskStatus.CANCELED, task.getStatus());
  }

  @Test(expected = TaskExeception.class)
  public void submittedToSubmitted() {
	Task task = new Task();
	task.registerStatus(TaskStatus.SUBMITTED);
	task.registerStatus(TaskStatus.SUBMITTED);
  }

  @Test(expected = TaskExeception.class)
  public void submittedToPending() {
	Task task = new Task();
	task.registerStatus(TaskStatus.SUBMITTED);
	task.registerStatus(TaskStatus.PENDING);
  }

  @Test(expected = TaskExeception.class)
  public void submittedToError() {
	Task task = new Task();
	task.registerStatus(TaskStatus.SUBMITTED);
	task.registerStatus(TaskStatus.ERROR);
  }

  @Test(expected = TaskExeception.class)
  public void submittedToDone() {
	Task task = new Task();
	task.registerStatus(TaskStatus.SUBMITTED);
	task.registerStatus(TaskStatus.DONE);
  }

  @Test()
  public void submittedToDiscarded() {
	Task task = new Task();
	task.registerStatus(TaskStatus.SUBMITTED);
	task.registerStatus(TaskStatus.DISCARDED);
  }

  @Test(expected = TaskExeception.class)
  public void canceledToSubmitted() {
	Task task = new Task();
	task.registerStatus(TaskStatus.SUBMITTED);
	task.registerStatus(TaskStatus.CANCELED);
	task.registerStatus(TaskStatus.SUBMITTED);
  }

  @Test(expected = TaskExeception.class)
  public void canceledToDone() {
	Task task = new Task();
	task.registerStatus(TaskStatus.SUBMITTED);
	task.registerStatus(TaskStatus.CANCELED);
	task.registerStatus(TaskStatus.DONE);
  }

  @Test(expected = TaskExeception.class)
  public void canceledToError() {
	Task task = new Task();
	task.registerStatus(TaskStatus.SUBMITTED);
	task.registerStatus(TaskStatus.CANCELED);
	task.registerStatus(TaskStatus.ERROR);
  }

  @Test(expected = TaskExeception.class)
  public void canceledToExecuting() {
	Task task = new Task();
	task.registerStatus(TaskStatus.SUBMITTED);
	task.registerStatus(TaskStatus.CANCELED);
	task.registerStatus(TaskStatus.EXECUTING);
  }

  @Test(expected = TaskExeception.class)
  public void canceledToPending() {
	Task task = new Task();
	task.registerStatus(TaskStatus.SUBMITTED);
	task.registerStatus(TaskStatus.CANCELED);
	task.registerStatus(TaskStatus.PENDING);
  }

  @Test(expected = TaskExeception.class)
  public void canceledToCanceled() {
	Task task = new Task();
	task.registerStatus(TaskStatus.SUBMITTED);
	task.registerStatus(TaskStatus.CANCELED);
	task.registerStatus(TaskStatus.CANCELED);
  }

  @Test(expected = TaskExeception.class)
  public void canceledToDiscarded() {
	Task task = new Task();
	task.registerStatus(TaskStatus.SUBMITTED);
	task.registerStatus(TaskStatus.CANCELED);
	task.registerStatus(TaskStatus.DISCARDED);
  }

  @Test
  public void executingToDone() {
	Task task = new Task();
	task.registerStatus(TaskStatus.SUBMITTED);
	task.registerStatus(TaskStatus.EXECUTING);
	task.registerStatus(TaskStatus.DONE);
	Assert.assertEquals(TaskStatus.DONE, task.getStatus());
  }

  @Test
  public void executingToError() {
	Task task = new Task();
	task.registerStatus(TaskStatus.SUBMITTED);
	task.registerStatus(TaskStatus.EXECUTING);
	task.registerStatus(TaskStatus.ERROR);
	Assert.assertEquals(TaskStatus.ERROR, task.getStatus());
  }

  @Test
  public void executingToCanceled() {
	Task task = new Task();
	task.registerStatus(TaskStatus.SUBMITTED);
	task.registerStatus(TaskStatus.EXECUTING);
	task.registerStatus(TaskStatus.CANCELED);
	Assert.assertEquals(TaskStatus.CANCELED, task.getStatus());
  }

  @Test(expected = TaskExeception.class)
  public void executingToPending() {
	Task task = new Task();
	task.registerStatus(TaskStatus.SUBMITTED);
	task.registerStatus(TaskStatus.EXECUTING);
	task.registerStatus(TaskStatus.PENDING);
  }

  @Test(expected = TaskExeception.class)
  public void executingToSubmitted() {
	Task task = new Task();
	task.registerStatus(TaskStatus.SUBMITTED);
	task.registerStatus(TaskStatus.EXECUTING);
	task.registerStatus(TaskStatus.SUBMITTED);
  }

  @Test(expected = TaskExeception.class)
  public void executingToExecuting() {
	Task task = new Task();
	task.registerStatus(TaskStatus.SUBMITTED);
	task.registerStatus(TaskStatus.EXECUTING);
	task.registerStatus(TaskStatus.EXECUTING);
  }

  @Test(expected = TaskExeception.class)
  public void executingToDiscarded() {
	Task task = new Task();
	task.registerStatus(TaskStatus.SUBMITTED);
	task.registerStatus(TaskStatus.EXECUTING);
	task.registerStatus(TaskStatus.DISCARDED);
  }

  @Test
  public void errorToSubmitted() {
	Task task = new Task();
	task.registerStatus(TaskStatus.SUBMITTED);
	task.registerStatus(TaskStatus.EXECUTING);
	task.registerStatus(TaskStatus.ERROR);
	task.registerStatus(TaskStatus.SUBMITTED);
	Assert.assertEquals(TaskStatus.SUBMITTED, task.getStatus());
  }

  @Test(expected = TaskExeception.class)
  public void errorToPending() {
	Task task = new Task();
	task.registerStatus(TaskStatus.SUBMITTED);
	task.registerStatus(TaskStatus.EXECUTING);
	task.registerStatus(TaskStatus.ERROR);
	task.registerStatus(TaskStatus.PENDING);
  }

  @Test(expected = TaskExeception.class)
  public void errorToCanceled() {
	Task task = new Task();
	task.registerStatus(TaskStatus.SUBMITTED);
	task.registerStatus(TaskStatus.EXECUTING);
	task.registerStatus(TaskStatus.ERROR);
	task.registerStatus(TaskStatus.CANCELED);
  }

  @Test(expected = TaskExeception.class)
  public void errorToExecuting() {
	Task task = new Task();
	task.registerStatus(TaskStatus.SUBMITTED);
	task.registerStatus(TaskStatus.EXECUTING);
	task.registerStatus(TaskStatus.ERROR);
	task.registerStatus(TaskStatus.EXECUTING);
  }

  @Test(expected = TaskExeception.class)
  public void errorToDone() {
	Task task = new Task();
	task.registerStatus(TaskStatus.SUBMITTED);
	task.registerStatus(TaskStatus.EXECUTING);
	task.registerStatus(TaskStatus.ERROR);
	task.registerStatus(TaskStatus.DONE);
  }

  @Test(expected = TaskExeception.class)
  public void errorToError() {
	Task task = new Task();
	task.registerStatus(TaskStatus.SUBMITTED);
	task.registerStatus(TaskStatus.EXECUTING);
	task.registerStatus(TaskStatus.ERROR);
	task.registerStatus(TaskStatus.ERROR);
  }

  @Test(expected = TaskExeception.class)
  public void errorToDiscarded() {
	Task task = new Task();
	task.registerStatus(TaskStatus.SUBMITTED);
	task.registerStatus(TaskStatus.EXECUTING);
	task.registerStatus(TaskStatus.ERROR);
	task.registerStatus(TaskStatus.DISCARDED);
  }

  @Test
  public void doneToSubmitted() {
	Task task = new Task();
	task.registerStatus(TaskStatus.SUBMITTED);
	task.registerStatus(TaskStatus.EXECUTING);
	task.registerStatus(TaskStatus.DONE);
	task.registerStatus(TaskStatus.SUBMITTED);
	Assert.assertEquals(TaskStatus.SUBMITTED, task.getStatus());
  }

  @Test(expected = TaskExeception.class)
  public void doneToPending() {
	Task task = new Task();
	task.registerStatus(TaskStatus.SUBMITTED);
	task.registerStatus(TaskStatus.EXECUTING);
	task.registerStatus(TaskStatus.DONE);
	task.registerStatus(TaskStatus.PENDING);
  }

  @Test(expected = TaskExeception.class)
  public void doneToCanceled() {
	Task task = new Task();
	task.registerStatus(TaskStatus.SUBMITTED);
	task.registerStatus(TaskStatus.EXECUTING);
	task.registerStatus(TaskStatus.DONE);
	task.registerStatus(TaskStatus.CANCELED);
  }

  @Test(expected = TaskExeception.class)
  public void doneToExecuting() {
	Task task = new Task();
	task.registerStatus(TaskStatus.SUBMITTED);
	task.registerStatus(TaskStatus.EXECUTING);
	task.registerStatus(TaskStatus.DONE);
	task.registerStatus(TaskStatus.EXECUTING);
  }

  @Test(expected = TaskExeception.class)
  public void doneToError() {
	Task task = new Task();
	task.registerStatus(TaskStatus.SUBMITTED);
	task.registerStatus(TaskStatus.EXECUTING);
	task.registerStatus(TaskStatus.DONE);
	task.registerStatus(TaskStatus.ERROR);
  }

  @Test(expected = TaskExeception.class)
  public void doneToDone() {
	Task task = new Task();
	task.registerStatus(TaskStatus.SUBMITTED);
	task.registerStatus(TaskStatus.EXECUTING);
	task.registerStatus(TaskStatus.DONE);
	task.registerStatus(TaskStatus.DONE);
  }

  @Test(expected = TaskExeception.class)
  public void doneToDiscarded() {
	Task task = new Task();
	task.registerStatus(TaskStatus.SUBMITTED);
	task.registerStatus(TaskStatus.EXECUTING);
	task.registerStatus(TaskStatus.DONE);
	task.registerStatus(TaskStatus.DISCARDED);
  }

  // DISCARDED

  @Test()
  public void discardedToSubmitted() {
	Task task = new Task();
	task.registerStatus(TaskStatus.SUBMITTED);
	task.registerStatus(TaskStatus.DISCARDED);
	task.registerStatus(TaskStatus.SUBMITTED);
  }

  @Test(expected = TaskExeception.class)
  public void discardedToCanceled() {
	Task task = new Task();
	task.registerStatus(TaskStatus.SUBMITTED);
	task.registerStatus(TaskStatus.DISCARDED);
	task.registerStatus(TaskStatus.CANCELED);
  }

  @Test(expected = TaskExeception.class)
  public void discardedToDiscarded() {
	Task task = new Task();
	task.registerStatus(TaskStatus.SUBMITTED);
	task.registerStatus(TaskStatus.DISCARDED);
	task.registerStatus(TaskStatus.DISCARDED);
  }

  @Test(expected = TaskExeception.class)
  public void discardedToDone() {
	Task task = new Task();
	task.registerStatus(TaskStatus.SUBMITTED);
	task.registerStatus(TaskStatus.DISCARDED);
	task.registerStatus(TaskStatus.DONE);
  }

  @Test(expected = TaskExeception.class)
  public void discardedToError() {
	Task task = new Task();
	task.registerStatus(TaskStatus.SUBMITTED);
	task.registerStatus(TaskStatus.DISCARDED);
	task.registerStatus(TaskStatus.ERROR);
  }

  @Test()
  public void discardedToExecuting() {
	Task task = new Task();
	task.registerStatus(TaskStatus.SUBMITTED);
	task.registerStatus(TaskStatus.DISCARDED);
	thrown.expect(TaskExeception.class);
	task.registerStatus(TaskStatus.EXECUTING);
  }

  @Test()
  public void discardedToPending() {
	Task task = new Task();
	task.registerStatus(TaskStatus.SUBMITTED);
	task.registerStatus(TaskStatus.DISCARDED);
	thrown.expect(TaskExeception.class);
	task.registerStatus(TaskStatus.PENDING);
  }
}
