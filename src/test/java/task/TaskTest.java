package task;

import java.util.Collection;
import java.util.HashMap;

import org.japura.task.Task;
import org.japura.task.TaskDescription;
import org.japura.task.TaskExeception;
import org.japura.task.TaskStatus;
import org.japura.task.executors.ExecutionType;
import org.japura.task.executors.TaskExecutor;
import org.japura.task.session.TaskSession;
import org.japura.task.session.TaskSessionListener;
import org.japura.task.ui.TaskExecutionUI;
import org.japura.util.info.IdentifierNode;
import org.japura.util.info.InfoNode;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TaskTest{

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void name() {
	Task task = new Task();
	task.setName("name");
	Assert.assertEquals("name", task.getName());
	task = new Task("name2");
	Assert.assertEquals("name2", task.getName());
  }

  @Test
  public void id() {
	Task task = new Task();
	Assert.assertNotNull(task.getId());
	Assert.assertEquals(task.hashCode(), task.getId().hashCode());
  }

  @Test
  public void equals() {
	Task task = new Task();
	Task task2 = new Task();
	Assert.assertEquals(false, task.equals(null));
	Assert.assertEquals(false, task.equals(""));
	Assert.assertEquals(false, task.equals(task2));
	Assert.assertEquals(true, task.equals(task));
  }

  @Test
  public void initialValues() {
	Task task = new Task();
	Assert.assertEquals(0, task.getBackgroundTimeSpent());
	Assert.assertNull(task.getTaskExecutor());
	Assert.assertNull(task.getMessage());
	Assert.assertNull(task.getParentId());
	Assert.assertNull(task.getException());
	Assert.assertEquals("", task.getName());
	Assert.assertEquals(false, task.isWaitForEDT());
  }

  @Test
  public void extra() {
	Task task = new Task();
	task.canceled(new TaskSession(null));
	Assert.assertNotNull(task.toString());
  }

  @Test
  public void changingMessage() {
	Task task = new Task();
	task.setMessage("message");
	Assert.assertNotNull(task.getMessage());
	Assert.assertEquals("message", task.getMessage());
  }

  @Test
  public void changingWaitForEDT() {
	Task task = new Task();
	task.setWaitForEDT(true);
	Assert.assertEquals(true, task.isWaitForEDT());
  }

  @Test
  public void registeringOwner1() {
	Task task = new Task();
	TaskExecutorFake executor = new TaskExecutorFake();
	task.registerTaskExecutor(executor);
	Assert.assertNotNull(task.getTaskExecutor());
	Assert.assertEquals(executor, task.getTaskExecutor());
  }

  @Test
  public void registeringOwner2() {
	thrown.expect(TaskExeception.class);
	Task task = new Task();
	task.registerTaskExecutor(null);
  }

  @Test
  public void registeringException() {
	Task task = new Task();
	task.registerStatus(TaskStatus.SUBMITTED);
	task.registerStatus(TaskStatus.EXECUTING);
	Exception exception = new Exception();
	task.registerException(exception);
	Assert.assertNotNull(task.getException());
	Assert.assertEquals(exception, task.getException());
	Assert.assertEquals(TaskStatus.ERROR, task.getStatus());
  }

  @Test
  public void submitNestedTask1() {
	Task parentTask = new Task();
	parentTask.registerExecutionType(ExecutionType.SERIAL);
	parentTask.setMessage("message");
	TaskExecutorFake executor = new TaskExecutorFake();
	parentTask.registerTaskExecutor(executor);
	Task task = new Task();
	parentTask.registerStatus(TaskStatus.SUBMITTED);
	parentTask.registerStatus(TaskStatus.EXECUTING);
	Assert.assertNull(executor.getNestedTasks());
	parentTask.submitNestedTask(task);
	Assert.assertNotNull(executor.getNestedTasks());
	Assert.assertNotNull(task.getParentId());
	Assert.assertEquals(parentTask.getId(), task.getParentId());
	Assert.assertNotNull(task.getMessage());
	Assert.assertEquals("message", task.getMessage());
  }

  @Test
  public void submitNestedTask2() {
	// keeping current message
	Task parentTask = new Task();
	parentTask.registerExecutionType(ExecutionType.SERIAL);
	parentTask.setMessage("message");
	parentTask.registerTaskExecutor(new TaskExecutorFake());
	Task task = new Task();
	task.setMessage("message2");
	parentTask.registerStatus(TaskStatus.SUBMITTED);
	parentTask.registerStatus(TaskStatus.EXECUTING);
	parentTask.submitNestedTask(task);
	Assert.assertEquals("message2", task.getMessage());
  }

  /**
   * parent Task status = PENDING must be EXECUTING
   */
  @Test
  public void submitNestedTask3() {
	// parent task status =
	thrown.expect(TaskExeception.class);
	Task parentTask = buildTaskWithOwner(); //
	parentTask.submitNestedTask(new Task());
  }

  /**
   * parent Task status = SUBMITTED must be EXECUTING
   */
  @Test
  public void submitNestedTask4() {
	thrown.expect(TaskExeception.class);
	Task parentTask = buildTaskWithOwner();
	parentTask.registerStatus(TaskStatus.SUBMITTED);
	Task task = new Task();
	parentTask.submitNestedTask(task);
  }

  /**
   * parent Task status = ERROR must be EXECUTING
   */
  @Test
  public void submitNestedTask5() {
	thrown.expect(TaskExeception.class);
	Task parentTask = buildTaskWithOwner();
	parentTask.registerStatus(TaskStatus.SUBMITTED);
	parentTask.registerStatus(TaskStatus.EXECUTING);
	parentTask.registerStatus(TaskStatus.ERROR);
	parentTask.submitNestedTask(new Task());
  }

  /**
   * parent Task status = DONE must be EXECUTING
   */
  @Test
  public void submitNestedTask6() {
	thrown.expect(TaskExeception.class);
	Task parentTask = buildTaskWithOwner();
	parentTask.registerStatus(TaskStatus.SUBMITTED);
	parentTask.registerStatus(TaskStatus.EXECUTING);
	parentTask.registerStatus(TaskStatus.DONE);
	parentTask.submitNestedTask(new Task());
  }

  /**
   * parent Task status = CANCELED must be EXECUTING
   */
  @Test
  public void submitNestedTask7() {
	thrown.expect(TaskExeception.class);
	Task parentTask = buildTaskWithOwner();
	parentTask.registerStatus(TaskStatus.SUBMITTED);
	parentTask.registerStatus(TaskStatus.CANCELED);
	parentTask.submitNestedTask(new Task());
  }

  @Test
  public void submitNestedTask8() {
	thrown.expect(TaskExeception.class);

	Task parentTask = buildExecutingTask();

	Task task = new Task();
	task.registerStatus(TaskStatus.SUBMITTED);
	parentTask.submitNestedTask(task);
  }

  @Test
  public void submitNestedTask9() {
	thrown.expect(TaskExeception.class);

	Task parentTask = buildExecutingTask();

	Task task = new Task();
	task.registerStatus(TaskStatus.SUBMITTED);
	task.registerStatus(TaskStatus.EXECUTING);
	parentTask.submitNestedTask(task);
  }

  @Test
  public void submitNestedTask10() {
	Task parentTask = buildExecutingTask();

	Task task = new Task();
	task.registerStatus(TaskStatus.SUBMITTED);
	task.registerStatus(TaskStatus.EXECUTING);
	task.registerStatus(TaskStatus.ERROR);
	parentTask.submitNestedTask(task);
  }

  @Test
  public void submitNestedTask11() {
	Task parentTask = buildExecutingTask();

	Task task = new Task();
	task.registerStatus(TaskStatus.SUBMITTED);
	task.registerStatus(TaskStatus.EXECUTING);
	task.registerStatus(TaskStatus.DONE);
	parentTask.submitNestedTask(task);
  }

  @Test
  public void submitNestedTask12() {
	Task parentTask = buildExecutingTask();

	Task task = new Task();
	task.registerStatus(TaskStatus.SUBMITTED);
	task.registerStatus(TaskStatus.CANCELED);
	parentTask.submitNestedTask(task);
  }

  @Test
  public void descriptionAnnotationTest() {
	TaskWithDescription task = new TaskWithDescription();
	Assert.assertNotNull(task.getDescription());
	Assert.assertEquals("description", task.getDescription());
  }

  private Task buildExecutingTask() {
	Task task = new Task();
	task.registerExecutionType(ExecutionType.SERIAL);
	task.registerTaskExecutor(new TaskExecutorFake());
	task.registerStatus(TaskStatus.SUBMITTED);
	task.registerStatus(TaskStatus.EXECUTING);
	return task;
  }

  private Task buildTaskWithOwner() {
	Task task = new Task();
	task.registerTaskExecutor(new TaskExecutorFake());
	return task;
  }

  @TaskDescription(description = "description")
  private static class TaskWithDescription extends Task{

  }

  private static class TaskExecutorFake implements TaskExecutor{

	private Task[] nestedTasks;

	@Override
	public void cancel() {}

	@Override
	public Object getOwner() {
	  return null;
	}

	@Override
	public String getTaskSubmitterId() {
	  return null;
	}

	@Override
	public TaskSession submitTask(Task... tasks) {
	  return null;
	}

	@Override
	public TaskSession submitTask(boolean isParallel, Task... tasks) {
	  return null;
	}

	@Override
	public TaskSession submitTask(TaskSessionListener listener, Task... tasks) {
	  return null;
	}

	@Override
	public TaskSession submitTask(boolean isParallel,
								  TaskSessionListener listener, Task... tasks) {
	  return null;
	}

	@Override
	public void submitNestedTask(Task... tasks) {
	  this.nestedTasks = tasks;
	}

	public Task[] getNestedTasks() {
	  return nestedTasks;
	}

	@Override
	public HashMap<String, Object> getHandlerExceptionParameters(Task task) {
	  return new HashMap<String, Object>();
	}

	@Override
	public boolean hasTask() {
	  return false;
	}

	@Override
	public boolean isShutdown() {
	  return false;
	}

	@Override
	public void shutdown() {}

	@Override
	public void setTaskExecutionUI(TaskExecutionUI ui) {}

	@Override
	public TaskExecutionUI getTaskExecutionUI() {
	  return null;
	}

	@Override
	public Collection<InfoNode> getInfoNodes() {
	  return null;
	}

	@Override
	public Collection<IdentifierNode> getIdentifierNodes() {
	  return null;
	}

  }

}
