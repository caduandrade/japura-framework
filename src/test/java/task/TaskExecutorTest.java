package task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.japura.Application;
import org.japura.task.Task;
import org.japura.task.TaskStatus;
import org.japura.task.executors.TaskExecutor;
import org.japura.task.manager.DefaultTaskManager;
import org.japura.task.session.TaskSession;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TaskExecutorTest{

  private static List<String> doInBackgroundMethods = new ArrayList<String>();
  private static List<String> submittedMethods = new ArrayList<String>();
  private static List<String> doneMethods = new ArrayList<String>();
  private static List<String> willExecuteMethods = new ArrayList<String>();
  private static List<String> handleExceptionMethods = new ArrayList<String>();
  private static List<String> canceledMethods = new ArrayList<String>();
  private static Collection<String> taskSessions = new HashSet<String>();

  @Before
  public void beforeTest() throws InterruptedException {
	doInBackgroundMethods.clear();
	submittedMethods.clear();
	doneMethods.clear();
	willExecuteMethods.clear();
	handleExceptionMethods.clear();
	canceledMethods.clear();
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

  private void contains(List<String> list, List<TaskTest> tasks) {
	Assert.assertEquals(tasks.size(), list.size());
	for (int i = 0; i < tasks.size(); i++) {
	  Assert.assertTrue(list.contains(tasks.get(i).getId()));
	}
  }

  private void test(List<String> list, List<TaskTest> tasks) {
	Assert.assertEquals(tasks.size(), list.size());
	for (int i = 0; i < tasks.size(); i++) {
	  Assert.assertEquals(tasks.get(i).getId(), list.get(i));
	}
  }

  private void testSameTaskSession(TaskTest... tasks) {
	// TODO refazer
  }

  @Test
  public void batchTest() {
	TaskExecutor executor = Application.getTaskManager().getGlobalExecutor();

	int total = 50000;
	Counter counter = new Counter();
	for (int i = 0; i < total; i++) {
	  executor.submitTask(new CountTask(counter));
	}

	while (executor.hasTask()) {
	  Thread.yield();
	}

	Assert.assertEquals(total, counter.count);

	counter.count = 0;
	Task[] tasks = new Task[total];
	for (int i = 0; i < total; i++) {
	  tasks[i] = new CountTask(counter);
	}
	executor.submitTask(tasks);

	while (executor.hasTask()) {
	  Thread.yield();
	}

	Assert.assertEquals(total, counter.count);
  }

  @Test(timeout = 5000)
  public void fifo() throws InterruptedException {
	List<TaskTest> allTasks = new ArrayList<TaskTest>();

	List<TaskTest> tasks1 = new ArrayList<TaskTest>();
	for (int i = 0; i < 500; i++) {
	  TaskTest task = new TaskTest();
	  tasks1.add(task);
	  allTasks.add(task);
	}

	List<TaskTest> tasks2 = new ArrayList<TaskTest>();
	for (int i = 0; i < 250; i++) {
	  TaskTest task = new TaskTest();
	  tasks2.add(task);
	  allTasks.add(task);
	}
	TaskTest t = new TaskTest(500);
	tasks2.add(t);
	allTasks.add(t);
	for (int i = 0; i < 250; i++) {
	  TaskTest task = new TaskTest();
	  tasks2.add(task);
	  allTasks.add(task);
	}

	List<TaskTest> tasks3 = new ArrayList<TaskTest>();
	for (int i = 0; i < 500; i++) {
	  TaskTest task = new TaskTest();
	  tasks3.add(task);
	  allTasks.add(task);
	}

	TaskExecutor executor = Application.getTaskManager().getGlobalExecutor();

	executor.submitTask(tasks1.toArray(new TaskTest[0]));
	for (TaskTest task : tasks2) {
	  executor.submitTask(task);
	}
	executor.submitTask(tasks3.toArray(new TaskTest[0]));

	waitForTaskExecutions();

	test(canceledMethods, new ArrayList<TaskTest>());
	test(handleExceptionMethods, new ArrayList<TaskTest>());
	contains(submittedMethods, allTasks);
	test(willExecuteMethods, allTasks);
	test(doInBackgroundMethods, allTasks);
	test(doneMethods, allTasks);

	Assert.assertEquals(tasks2.size() + 2, taskSessions.size());
  }

  private static class TaskTest extends Task{

	private int waitTime = 0;

	public TaskTest() {
	  this(0);
	}

	public TaskTest(int waitTime) {
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
	  canceledMethods.clear();
	  handleExceptionMethods.add(getId());
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

	public TaskErrorTest(int waitTime) {
	  super(waitTime);
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

	public TaskNestedTest(Task[] nestedTasks1, Task[] nestedTasks2) {
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

  public static class Counter{
	public int count;
  }

  public static class CountTask extends Task{
	private Counter counter;

	public CountTask(Counter counter) {
	  this.counter = counter;
	}

	@Override
	public void doInBackground(TaskSession session) throws Exception {
	  counter.count++;
	}

  }

}
