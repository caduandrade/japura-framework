package controller;

import org.japura.Application;
import org.japura.controller.ControllerException;
import org.japura.controller.ControllerStatus;
import org.japura.task.Task;
import org.japura.task.session.TaskSession;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ControllerTaskTest{

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Before
  public void init() {
	Application.reset();
  }

  @Test
  public void freeTest() {
	ControllerA controller = new ControllerA();
	controller.submitTask(new Task() {
	  @Override
	  public void doInBackground(TaskSession session) throws Exception {
		submitNestedTask(new Task() {
		  @Override
		  public void doInBackground(TaskSession session) throws Exception {
			Thread.sleep(500);
		  }
		});
	  }
	});
	controller.free();

	// task running, controller still in pool
	Assert.assertEquals(ControllerStatus.UNREGISTERING,
		controller.getControllerStatus());
	Assert.assertEquals(1, Application.getControllerManager().count());

	while (controller.getGroup().getGroupTaskExecutor().hasTask()) {
	  Thread.yield();
	}

	// tasks finish, executor publish message to dispose controllers
	while (Application.getMessageManager().isPublishing()) {
	  Thread.yield();
	}

	// controller has been disposed
	Assert.assertEquals(ControllerStatus.UNREGISTERED,
		controller.getControllerStatus());
	Assert.assertEquals(0, Application.getControllerManager().count());
  }

  @Test
  public void freeTest2() {
	ControllerA controller = new ControllerA();
	controller.submitTask(new Task() {
	  @Override
	  public void doInBackground(TaskSession session) throws Exception {
		Thread.sleep(500);
	  }
	});

	controller.free();

	thrown.expect(ControllerException.class);

	// can't submit new tasks
	controller.submitTask(new Task() {
	  @Override
	  public void doInBackground(TaskSession session) throws Exception {
		Thread.sleep(500);
	  }
	});

  }

}
