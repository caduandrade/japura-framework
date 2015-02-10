package task;

import org.japura.Application;
import org.japura.task.Task;
import org.japura.task.manager.DefaultTaskManager;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TaskSessionsTest{

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Before
  public void beforeTest() throws InterruptedException {
	DefaultTaskManager taskManager = new DefaultTaskManager();
	taskManager.setInvokeTaskMethodsOnEDT(false);
	Application.setTaskManager(taskManager);
  }

  private void waitForTaskExecutions() {
	while (Application.getTaskManager().getGlobalExecutor().hasTask()) {
	  Thread.yield();
	}
  }

  @Test(timeout = 5000)
  public void test1() throws InterruptedException {
	Application.getTaskManager().getGlobalExecutor().submitTask(new TaskA());
	waitForTaskExecutions();
  }

  @Test(timeout = 5000)
  public void test2() throws InterruptedException {
	Application.getTaskManager().getGlobalExecutor().submitTask(new TaskB());
	waitForTaskExecutions();
  }

  @Test(timeout = 5000)
  public void test3() throws InterruptedException {
	Application.getTaskManager().getGlobalExecutor()
		.submitTask(new TaskA(), new TaskA());
	waitForTaskExecutions();
  }

  @Test(timeout = 5000)
  public void test4() throws InterruptedException {
	Application.getTaskManager().getGlobalExecutor()
		.submitTask(new TaskB(), new TaskB());
	waitForTaskExecutions();
  }

  @Test(timeout = 5000)
  public void test5() throws InterruptedException {
	Application.getTaskManager().getGlobalExecutor()
		.submitTask(new TaskA(), new TaskB());
	waitForTaskExecutions();
  }

  @Test(timeout = 5000)
  public void test6() throws InterruptedException {
	Application.getTaskManager().getGlobalExecutor()
		.submitTask(new TaskB(), new TaskA());
	waitForTaskExecutions();
  }

  public static class TaskA extends Task{}

  public static class TaskB extends Task{}

}
