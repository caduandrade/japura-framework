package task;

import javax.swing.JPanel;

import org.japura.Application;
import org.japura.controller.Context;
import org.japura.controller.Controller;
import org.japura.controller.DefaultController;
import org.japura.controller.Group;
import org.japura.task.Task;
import org.japura.task.executors.DefaultTaskExecutor;
import org.junit.Assert;
import org.junit.Test;

public class TaskSubmitterTest{

  @Test
  public void testTaskSubmitter() {
	ControllerTest c =
		Application.getControllerManager().get(ControllerTest.class);
	Task task = new Task();
	c.submitTask(task);
	while (c.getGroup().getGroupTaskExecutor().hasTask()) {
	  Thread.yield();
	}
	String id = task.getTaskSubmitterId(ControllerTest.class);
	Assert.assertNotNull(id);
	Assert.assertEquals(c.getControllerId(), id);

	id = task.getTaskSubmitterId(Group.class);
	Assert.assertNotNull(id);
	Assert.assertEquals(c.getGroupId(), id);

	id = task.getTaskSubmitterId(DefaultTaskExecutor.class);
	Assert.assertNull(id);
  }

  public static class ControllerTest extends DefaultController<JPanel>{

	public ControllerTest(Context context, Controller parentController) {
	  super(context, parentController);
	}

	@Override
	public JPanel buildComponent() {
	  return null;
	}

  }

}
