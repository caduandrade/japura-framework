package controller;

import javax.swing.JComponent;

import org.japura.Application;
import org.japura.controller.Context;
import org.japura.controller.Controller;
import org.japura.controller.ControllerException;
import org.japura.controller.DefaultController;
import org.japura.controller.annotations.RootController;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ControllerTest{

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Before
  public void init() {
	Application.reset();
  }

  @Test
  public void defaultConstructorTest() {
	Context mainContext = Context.getMainContext();

	ControllerA controller = new ControllerA();
	Assert.assertEquals(true, mainContext.equals(controller.getContext()));
	Assert.assertNotNull(controller.getGroup());
	Assert.assertNull(controller.getParent());
  }

  @Test
  public void sameGroupDifContextsTest() {
	thrown.expect(ControllerException.class);

	Context mainContext = Context.getMainContext();
	Context newContext = new Context("NEW CONTEXT");

	ControllerA root = new ControllerA(newContext, null);
	new ControllerA(mainContext, root);
  }

  @Test
  public void childTest() {
	ControllerA parent = new ControllerA();
	ControllerB child = parent.createChild(ControllerB.class);
	Assert.assertEquals(true, child.getParent().equals(parent));
	Assert.assertEquals(true, child.getGroup().equals(parent.getGroup()));
	Assert.assertEquals(true, child.getRoot().equals(parent));
	Assert.assertEquals(true, parent.getChild(ControllerB.class).equals(child));
	Assert.assertEquals(true,
		parent.getChild(child.getControllerId()).equals(child));
	Assert.assertEquals(true, parent.containsChild(ControllerB.class));
	Assert.assertEquals(false, parent.containsChild(ControllerA.class));
	Assert.assertEquals(1, parent.getChildren().size());
  }

  @Test
  public void registerMessageTest() {
	ControllerA c = new ControllerA();
	Assert.assertEquals(true, Application.getMessageManager().isRegistered(c));
	c.free();
	Assert.assertEquals(false, Application.getMessageManager().isRegistered(c));
  }

  @Test
  public void freeTest() {
	Assert.assertEquals(0, Application.getControllerManager().count());
	ControllerA controllerA =
		Application.getControllerManager().buildController(ControllerA.class);
	Application.getControllerManager().buildController(ControllerB.class);
	Assert.assertEquals(2, Application.getControllerManager().count());
	controllerA.free();
	Assert.assertEquals(1, Application.getControllerManager().count());
  }

  @Test
  public void containsTest() {
	ControllerA controllerA =
		Application.getControllerManager().buildController(ControllerA.class);
	Assert.assertEquals(true,
		Application.getControllerManager().contains(ControllerA.class));
	controllerA.free();
	Assert.assertEquals(false,
		Application.getControllerManager().contains(ControllerA.class));
  }

  @Test
  public void rootCheckTest() {
	TestRootController c =
		Application.getControllerManager().buildController(
			TestRootController.class);
	thrown.expect(ControllerException.class);
	c.createChild(TestRootController.class);
  }

  @Test
  public void rootCheckTest2() {
	TestRootControllerB c =
		Application.getControllerManager().buildController(
			TestRootControllerB.class);
	thrown.expect(ControllerException.class);
	c.createChild(TestRootControllerB.class);
  }

  @RootController
  public static class TestRootController extends DefaultController<JComponent>{

	public TestRootController(Context context, Controller parentController) {
	  super(context, parentController);
	}

	@Override
	public JComponent buildComponent() {
	  return null;
	}

  }

  public static class TestRootControllerB extends TestRootController{

	public TestRootControllerB(Context context, Controller parentController) {
	  super(context, parentController);
	}

  }
}
