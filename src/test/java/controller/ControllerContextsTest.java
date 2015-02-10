package controller;

import javax.swing.JComponent;

import org.japura.Application;
import org.japura.controller.Context;
import org.japura.controller.Controller;
import org.japura.controller.ControllerException;
import org.japura.controller.DefaultController;
import org.japura.controller.annotations.ContextName;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ControllerContextsTest{

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  public static class AbstractControllerTest extends
	  DefaultController<JComponent>{

	public AbstractControllerTest(Context context, Controller parentController) {
	  super(context, parentController);
	}

	@Override
	public JComponent buildComponent() {
	  return null;
	}

  }

  public static class ControllerA extends AbstractControllerTest{
	public ControllerA(Context context, Controller parentController) {
	  super(context, parentController);
	}
  }

  @ContextName(name = "MAIN")
  public static class ControllerB extends AbstractControllerTest{
	public ControllerB(Context context, Controller parentController) {
	  super(context, parentController);
	}
  }

  @ContextName(name = "TEST")
  public static class ControllerC extends AbstractControllerTest{
	public ControllerC(Context context, Controller parentController) {
	  super(context, parentController);
	}
  }

  @Before
  public void init() {
	Application.reset();
  }

  @Test
  public void test1() {
	Application.getControllerManager().buildController(ControllerA.class);
	Assert.assertEquals(1, Application.getControllerManager()
		.getContextsCount());
  }

  @Test
  public void test2() {
	Application.getControllerManager().buildController(ControllerB.class);
	Assert.assertEquals(1, Application.getControllerManager()
		.getContextsCount());
  }

  @Test
  public void test3() {
	Application.getControllerManager().buildController(ControllerC.class);
	Assert.assertEquals(1, Application.getControllerManager()
		.getContextsCount());
  }

  @Test
  public void test4() {
	Application.getControllerManager().buildController(ControllerC.class);
	Application.getControllerManager().buildController(ControllerC.class);
	Assert.assertEquals(2, Application.getControllerManager()
		.getContextsCount());
	Assert.assertEquals(2,
		Application.getControllerManager().getContexts("TEST").size());
  }

  @Test
  public void test5() {
	thrown.expect(ControllerException.class);
	Context context = new Context("TEST2");
	Application.getControllerManager().buildController(ControllerC.class,
		context);
  }

  @Test
  public void test6() {
	Context mainContext = Context.getMainContext();
	Application.getControllerManager().buildController(ControllerA.class,
		mainContext);
	Assert.assertEquals(1, Application.getControllerManager()
		.getContextsCount());
  }

  @Test
  public void test7() {
	Context context = new Context("TEST");
	Application.getControllerManager().buildController(ControllerC.class,
		context);
	Assert.assertEquals(1, Application.getControllerManager()
		.getContextsCount());
  }

}
