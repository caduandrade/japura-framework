package task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.japura.Application;
import org.japura.task.Task;
import org.japura.task.TaskStatus;
import org.japura.task.manager.DefaultTaskManager;
import org.japura.task.session.TaskSession;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TaskExecutorsTest{

  private static List<String> doInBackgroundMethods = new ArrayList<String>();
  private static List<String> submittedMethods = new ArrayList<String>();
  private static List<String> doneMethods = new ArrayList<String>();
  private static List<String> willExecuteMethods = new ArrayList<String>();
  private static List<String> handleExceptionMethods = new ArrayList<String>();
  private static List<String> canceledMethods = new ArrayList<String>();
  private static List<String> discardedTasks = new ArrayList<String>();
  private static Collection<String> taskSessions = new HashSet<String>();

  @Before
  public void beforeTest() throws InterruptedException {
	doInBackgroundMethods.clear();
	submittedMethods.clear();
	doneMethods.clear();
	willExecuteMethods.clear();
	handleExceptionMethods.clear();
	canceledMethods.clear();
	discardedTasks.clear();
	taskSessions.clear();

	Application.reset();

	DefaultTaskManager dtm = new DefaultTaskManager();
	dtm.setInvokeTaskMethodsOnEDT(false);
	Application.setTaskManager(dtm);

	// Application.setTaskManager(new MockTaskManager());
  }

  private void waitForTaskExecutions() {
	while (Application.getTaskManager().getGlobalExecutor().hasTask()) {
	  Thread.yield();
	}
  }

  private void contains(List<String> list, TaskTest... tasks) {
	Assert.assertEquals(tasks.length, list.size());
	for (int i = 0; i < tasks.length; i++) {
	  Assert.assertTrue(list.contains(tasks[i].getId()));
	}
  }

  private void test(List<String> list, TaskTest... tasks) {
	Assert.assertEquals(tasks.length, list.size());
	for (int i = 0; i < tasks.length; i++) {
	  Assert.assertEquals(tasks[i].getId(), list.get(i));
	}
  }

  private void testSameTaskSession(TaskTest... tasks) {
	// TODO rafazer
  }

  @Test(timeout = 5000)
  public void errorTest() throws InterruptedException {
	TaskTest t1 = new TaskErrorTest("task1", 500);
	TaskTest t2 = new TaskTest("task2");
	TaskTest t3 = new TaskTest("task3");

	Application.getTaskManager().getGlobalExecutor().submitTask(t1);
	Application.getTaskManager().getGlobalExecutor().submitTask(t2);
	Application.getTaskManager().getGlobalExecutor().submitTask(t3);

	waitForTaskExecutions();

	test(canceledMethods);
	test(handleExceptionMethods, t1);
	contains(submittedMethods, t1, t2, t3);
	test(willExecuteMethods, t1, t2, t3);
	test(doInBackgroundMethods, t1, t2, t3);
	test(doneMethods, t2, t3);

	Assert.assertEquals(3, taskSessions.size());
  }

  @Test(timeout = 5000)
  public void errorTest2() throws InterruptedException {
	TaskTest t1 = new TaskErrorTest("task1", 500);
	TaskTest t2 = new TaskTest("task2");
	TaskTest t3 = new TaskTest("task3");
	TaskTest t4 = new TaskTest("task4");

	Application.getTaskManager().getGlobalExecutor().submitTask(t1, t2, t3);
	Application.getTaskManager().getGlobalExecutor().submitTask(t4);

	waitForTaskExecutions();

	test(canceledMethods);
	test(discardedTasks, t2, t3);
	test(handleExceptionMethods, t1);
	contains(submittedMethods, t1, t2, t3, t4);
	test(willExecuteMethods, t1, t4);
	test(doInBackgroundMethods, t1, t4);
	test(doneMethods, t4);
	testSameTaskSession(t1, t2, t3);

	Assert.assertEquals(2, taskSessions.size());
  }

  @Test(timeout = 5000)
  public void errorTest3() throws InterruptedException {
	List<TaskTest> tasksList = new ArrayList<TaskTest>();
	tasksList.add(new TaskErrorTest("task1", 0));
	for (int i = 2; i < 400; i++) {
	  tasksList.add(new TaskTest("task" + i));
	}

	TaskTest[] allTasks = tasksList.toArray(new TaskTest[0]);
	tasksList.remove(0);
	TaskTest[] discardedTasks = tasksList.toArray(new TaskTest[0]);

	Application.getTaskManager().getGlobalExecutor().submitTask(allTasks);

	waitForTaskExecutions();

	test(TaskExecutorsTest.discardedTasks, discardedTasks);

  }

  @Test(timeout = 5000)
  public void nestedTest1() throws InterruptedException {
	TaskTest tn1 = new TaskTest("task2.1");
	TaskTest tn2 = new TaskTest("task2.2");
	TaskTest tn3 = new TaskTest("task2.3");
	TaskTest tn4 = new TaskTest("task2.4");
	Task[] nestedTasks1 = new Task[] { tn1, tn2 };
	Task[] nestedTasks2 = new Task[] { tn3, tn4 };

	TaskTest t1 = new TaskTest("task1", 500);
	TaskNestedTest t2 = new TaskNestedTest("task2", nestedTasks1, nestedTasks2);
	TaskTest t3 = new TaskTest("task3");

	Application.getTaskManager().getGlobalExecutor().submitTask(t1);
	Application.getTaskManager().getGlobalExecutor().submitTask(t2);
	Application.getTaskManager().getGlobalExecutor().submitTask(t3);

	waitForTaskExecutions();

	test(canceledMethods);
	test(handleExceptionMethods);
	contains(submittedMethods, t1, t2, t3, tn1, tn2, tn3, tn4);
	test(willExecuteMethods, t1, t2, tn1, tn2, tn3, tn4, t3);
	test(doInBackgroundMethods, t1, t2, tn1, tn2, tn3, tn4, t3);
	test(doneMethods, t1, t2, tn1, tn2, tn3, tn4, t3);
	testSameTaskSession(t2, tn1, tn2, tn3, tn4);

	Assert.assertEquals(3, taskSessions.size());
  }

  @Test(timeout = 5000)
  public void nestedTest2() throws InterruptedException {
	TaskTest tn1 = new TaskTest("task2.1");
	TaskTest tn2 = new TaskTest("task2.2");
	TaskTest tn3 = new TaskTest("task2.3");
	TaskTest tn4 = new TaskTest("task2.4");
	Task[] nestedTasks1 = new Task[] { tn1, tn2 };
	Task[] nestedTasks2 = new Task[] { tn3, tn4 };

	TaskTest t1 = new TaskTest("task1", 500);
	TaskNestedTest t2 = new TaskNestedTest("task2", nestedTasks1, nestedTasks2);
	TaskTest t3 = new TaskTest("task3");
	TaskTest t4 = new TaskTest("task4");

	Application.getTaskManager().getGlobalExecutor().submitTask(t1);
	Application.getTaskManager().getGlobalExecutor().submitTask(t2);
	Application.getTaskManager().getGlobalExecutor().submitTask(t3);
	Application.getTaskManager().getGlobalExecutor().submitTask(t4);

	waitForTaskExecutions();

	test(canceledMethods);
	test(handleExceptionMethods);
	contains(submittedMethods, t1, t2, t3, t4, tn1, tn2, tn3, tn4);
	test(willExecuteMethods, t1, t2, tn1, tn2, tn3, tn4, t3, t4);
	test(doInBackgroundMethods, t1, t2, tn1, tn2, tn3, tn4, t3, t4);
	test(doneMethods, t1, t2, tn1, tn2, tn3, tn4, t3, t4);
	testSameTaskSession(t2, tn1, tn2, tn3, tn4);

	Assert.assertEquals(4, taskSessions.size());
  }

  @Test(timeout = 5000)
  public void nestedTest3() throws InterruptedException {
	TaskTest tn1 = new TaskTest("task1.1");
	TaskTest tn2 = new TaskTest("task1.2");
	TaskTest tn3 = new TaskTest("task1.3");
	TaskTest tn4 = new TaskTest("task1.4");
	Task[] nestedTasks1 = new Task[] { tn1, tn2 };
	Task[] nestedTasks2 = new Task[] { tn3, tn4 };

	TaskNestedTest t1 = new TaskNestedTest("task1", nestedTasks1, nestedTasks2);
	TaskTest t2 = new TaskTest("task2");

	Application.getTaskManager().getGlobalExecutor().submitTask(t1);
	Application.getTaskManager().getGlobalExecutor().submitTask(t2);

	waitForTaskExecutions();

	test(canceledMethods);
	test(handleExceptionMethods);
	contains(submittedMethods, t1, t2, tn1, tn2, tn3, tn4);
	test(willExecuteMethods, t1, tn1, tn2, tn3, tn4, t2);
	test(doInBackgroundMethods, t1, tn1, tn2, tn3, tn4, t2);
	test(doneMethods, t1, tn1, tn2, tn3, tn4, t2);
	testSameTaskSession(t1, tn1, tn2, tn3, tn4);

	Assert.assertEquals(2, taskSessions.size());
  }

  @Test(timeout = 5000)
  public void nestedTest4() throws InterruptedException {
	TaskTest tn1 = new TaskTest("task2.1");
	TaskTest tn2 = new TaskTest("task2.2");
	TaskTest tn3 = new TaskTest("task2.3");
	TaskTest tn4 = new TaskTest("task2.4");
	Task[] nestedTasks1 = new Task[] { tn1, tn2 };
	Task[] nestedTasks2 = new Task[] { tn3, tn4 };

	TaskTest t1 = new TaskTest("task1", 500);
	TaskNestedTest t2 = new TaskNestedTest("task2", nestedTasks1, nestedTasks2);

	Application.getTaskManager().getGlobalExecutor().submitTask(t1);
	Application.getTaskManager().getGlobalExecutor().submitTask(t2);

	waitForTaskExecutions();

	test(canceledMethods);
	test(handleExceptionMethods);
	contains(submittedMethods, t1, t2, tn1, tn2, tn3, tn4);
	test(willExecuteMethods, t1, t2, tn1, tn2, tn3, tn4);
	test(doInBackgroundMethods, t1, t2, tn1, tn2, tn3, tn4);
	test(doneMethods, t1, t2, tn1, tn2, tn3, tn4);
	testSameTaskSession(t2, tn1, tn2, tn3, tn4);

	Assert.assertEquals(2, taskSessions.size());
  }

  @Test(timeout = 5000)
  public void nestedTest5() throws InterruptedException {
	TaskTest tn1 = new TaskTest("task1.1");
	TaskTest tn2 = new TaskTest("task1.2");
	TaskTest tn3 = new TaskTest("task1.3");
	TaskTest tn4 = new TaskTest("task1.4");
	Task[] nestedTasks1 = new Task[] { tn1, tn2 };
	Task[] nestedTasks2 = new Task[] { tn3, tn4 };

	TaskNestedTest t1 = new TaskNestedTest("task1", nestedTasks1, nestedTasks2);

	Application.getTaskManager().getGlobalExecutor().submitTask(t1);

	waitForTaskExecutions();

	test(canceledMethods);
	test(handleExceptionMethods);
	contains(submittedMethods, t1, tn1, tn2, tn3, tn4);
	test(willExecuteMethods, t1, tn1, tn2, tn3, tn4);
	test(doInBackgroundMethods, t1, tn1, tn2, tn3, tn4);
	test(doneMethods, t1, tn1, tn2, tn3, tn4);
	testSameTaskSession(t1, tn1, tn2, tn3, tn4);

	Assert.assertEquals(1, taskSessions.size());
  }

  @Test(timeout = 5000)
  public void nestedCancelTest() throws InterruptedException {
	TaskTest tn1 = new TaskTest("task1.1");
	TaskTest tn2 = new TaskTest("task1.2");
	TaskTest t2 = new TaskTest("task2");
	TaskTest t3 = new TaskTest("task3");
	Task[] nestedTasks = new Task[] { tn1, tn2 };

	TaskNestedCancelTest t1 = new TaskNestedCancelTest("task1", nestedTasks);

	Application.getTaskManager().getGlobalExecutor().submitTask(t1, t2, t3);

	waitForTaskExecutions();

	test(canceledMethods, t1, tn1, tn2, t2, t3);
	test(handleExceptionMethods);
	contains(submittedMethods, t1, tn1, tn2, t2, t3);
	test(willExecuteMethods, t1);
	test(doInBackgroundMethods, t1);
	test(doneMethods);
	testSameTaskSession(t1, tn1, tn2, t2, t3);

	Assert.assertEquals(1, taskSessions.size());
  }

  @Test(timeout = 5000)
  public void statusTest() throws InterruptedException {
	TaskStatusTest t1 = new TaskStatusTest(false, false);
	TaskStatusTest t2 = new TaskStatusTest(true, false);
	TaskStatusTest t3 = new TaskStatusTest(false, true);

	Application.getTaskManager().getGlobalExecutor().submitTask(t1);
	Application.getTaskManager().getGlobalExecutor().submitTask(t2);
	Application.getTaskManager().getGlobalExecutor().submitTask(t3);
  }

  private static class TaskTest extends Task{

	private int waitTime = 0;

	public TaskTest(String name) {
	  this(name, 0);
	}

	public TaskTest(String name, int waitTime) {
	  setName(name);
	  this.waitTime = waitTime;
	}

	@Override
	public void submitted(TaskSession session) {
	  taskSessions.add(session.getId());
	  submittedMethods.add(getId());
	}

	@Override
	public void handleException(TaskSession session, Exception e,
								Collection<Task> discardedTasks) {
	  // canceledMethods.clear();
	  handleExceptionMethods.add(getId());
	  for (Task task : discardedTasks) {
		TaskExecutorsTest.discardedTasks.add(task.getId());
	  }
	}

	@Override
	public void canceled(TaskSession session) {
	  canceledMethods.add(getId());
	}

	@Override
	public void willExecute(TaskSession session) throws Exception {
	  willExecuteMethods.add(getId());
	}

	@Override
	public void done(TaskSession session) {
	  doneMethods.add(getId());
	}

	@Override
	public void doInBackground(TaskSession session) throws Exception {
	  Thread.sleep(waitTime);
	  doInBackgroundMethods.add(getId());
	}
  }

  private static class TaskErrorTest extends TaskTest{

	public TaskErrorTest(String name, int waitTime) {
	  super(name, waitTime);
	}

	@Override
	public void doInBackground(TaskSession session) throws Exception {
	  super.doInBackground(session);
	  throw new RuntimeException("Expected error");
	}
  }

  private static class TaskNestedTest extends TaskTest{

	private Task[] nestedTasks1;
	private Task[] nestedTasks2;

	public TaskNestedTest(String name, Task[] nestedTasks1, Task[] nestedTasks2) {
	  super(name);
	  this.nestedTasks1 = nestedTasks1;
	  this.nestedTasks2 = nestedTasks2;
	}

	@Override
	public void doInBackground(TaskSession session) throws Exception {
	  super.doInBackground(session);
	  submitNestedTask(nestedTasks1);
	  submitNestedTask(nestedTasks2);
	}
  }

  private static class TaskNestedCancelTest extends TaskTest{

	private Task[] nestedTasks;

	public TaskNestedCancelTest(String name, Task[] nestedTasks) {
	  super(name);
	  this.nestedTasks = nestedTasks;
	}

	@Override
	public void doInBackground(TaskSession session) throws Exception {
	  super.doInBackground(session);
	  submitNestedTask(nestedTasks);
	  session.forceCancel();
	}
  }

  private static class TaskStatusTest extends Task{

	private boolean error;
	private boolean cancel;

	public TaskStatusTest(boolean error, boolean cancel) {
	  this.error = error;
	  this.cancel = cancel;
	}

	@Override
	public void willExecute(TaskSession session) throws Exception {
	  if (cancel) {
		session.forceCancel();
	  }
	}

	@Override
	public void doInBackground(TaskSession session) throws Exception {
	  Assert.assertEquals(true, getStatus().equals(TaskStatus.EXECUTING));
	  if (error) {
		throw new Exception();
	  }
	}

	@Override
	public void handleException(TaskSession session, Exception e,
								Collection<Task> discardedTasks) {
	  Assert.assertEquals(true, getStatus().equals(TaskStatus.ERROR));
	}

	@Override
	public void canceled(TaskSession session) {
	  Assert.assertEquals(true, getStatus().equals(TaskStatus.CANCELED));
	}

	@Override
	public void done(TaskSession session) {
	  Assert.assertEquals(true, getStatus().equals(TaskStatus.DONE));
	}

	@Override
	public void submitted(TaskSession session) {
	  Assert.assertEquals(true, getStatus().equals(TaskStatus.SUBMITTED));
	}
  }

}
