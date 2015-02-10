package controller;

import java.util.Collection;

import javax.swing.JComponent;

import org.japura.controller.Context;
import org.japura.controller.Controller;
import org.japura.controller.ControllerStatus;
import org.japura.controller.DefaultController;
import org.japura.controller.DefaultControllerCollection;
import org.japura.controller.listeners.ControllerCollectionListener;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ControllerContainerTest{

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private static class ControllerA_Parent extends DefaultController<JComponent>{

	public ControllerA_Parent() {
	  this(null, null);
	}

	public ControllerA_Parent(Context context, Controller parentController) {
	  super(context, parentController);
	}

	@Override
	public JComponent buildComponent() {
	  return null;
	}

  }

  private static class ControllerA_Child extends ControllerA_Parent{

	public ControllerA_Child() {
	  this(null, null);
	}

	public ControllerA_Child(Context context, Controller parentController) {
	  super(context, parentController);
	}
  }

  private static class ControllerB extends DefaultController<JComponent>{
	public ControllerB() {
	  this(null, null);
	}

	public ControllerB(Context context, Controller parentController) {
	  super(context, parentController);
	}

	@Override
	public JComponent buildComponent() {
	  return null;
	}

  }

  private static class TestListener implements ControllerCollectionListener{

	public boolean unregisteringCalled;
	public boolean unregisteredCalled;
	public boolean registeredCalled;
	private Controller controllerParameter;

	@Override
	public void unregistering(Controller controller) {
	  this.unregisteringCalled = true;
	  this.controllerParameter = controller;
	}

	@Override
	public void unregistered(Controller controller) {
	  this.unregisteredCalled = true;
	  this.controllerParameter = controller;
	}

	@Override
	public void registered(Controller controller) {
	  this.registeredCalled = true;
	  this.controllerParameter = controller;
	}
  };

  private DefaultControllerCollection collection;

  @Before
  public void beforeTest() {
	collection = new DefaultControllerCollection();

	collection.register(new ControllerA_Parent());
	collection.register(new ControllerA_Parent());
	collection.register(new ControllerA_Child());
  }

  @Test
  public void testClass1() {
	thrown.expect(IllegalArgumentException.class);
	collection.testControllerClass(String.class);
  }

  @Test
  public void testClass2() {
	collection.testControllerClass(Controller.class);
  }

  @Test
  public void testClass3() {
	collection.testControllerClass(ControllerB.class);
  }

  @Test
  public void testListeners() {
	Assert.assertEquals(0, collection.getListeners().size());

	TestListener listener = new TestListener();
	collection.addListener(listener);

	Assert.assertEquals(1, collection.getListeners().size());

	ControllerB controller = new ControllerB();

	Assert.assertEquals(false, listener.registeredCalled);
	Assert.assertEquals(false, listener.unregisteringCalled);
	Assert.assertEquals(false, listener.unregisteredCalled);
	collection.fireListeners(controller, ControllerStatus.REGISTERED);
	Assert.assertEquals(true, listener.registeredCalled);
	Assert.assertEquals(controller, listener.controllerParameter);
	collection.fireListeners(controller, ControllerStatus.UNREGISTERING);
	Assert.assertEquals(true, listener.unregisteringCalled);
	collection.fireListeners(controller, ControllerStatus.UNREGISTERED);
	Assert.assertEquals(true, listener.unregisteredCalled);

	collection.removeListener(listener);
	Assert.assertEquals(0, collection.getListeners().size());

	listener = new TestListener();
	collection.addListener(listener);

	Assert.assertEquals(false, listener.registeredCalled);
	Assert.assertEquals(false, listener.unregisteringCalled);
	Assert.assertEquals(false, listener.unregisteredCalled);
	collection.register(controller);
	Assert.assertEquals(true, listener.registeredCalled);
	collection.unregister(controller);
	Assert.assertEquals(true, listener.unregisteredCalled);
  }

  @Test
  public void testContains() {
	Assert.assertEquals(false, collection.contains(ControllerB.class));

	ControllerB controller = new ControllerB();
	collection.register(controller);

	Assert.assertEquals(true, collection.contains(controller));
	Assert.assertEquals(true, collection.contains(ControllerB.class));
  }

  @Test
  public void testUnregister() {
	ControllerA_Child controller = collection.get(ControllerA_Child.class);
	collection.unregister(controller);

	Collection<Controller> all = collection.getAll();
	Assert.assertEquals(2, all.size());

	Assert.assertEquals(false, collection.contains(ControllerA_Child.class));

	Assert.assertEquals(true, collection.contains(Controller.class));
	collection.unregisterAll();
	Assert.assertEquals(false, collection.contains(Controller.class));
  }

  @Test
  public void testGet() {
	ControllerB controllerB = collection.get(ControllerB.class);
	Assert.assertNull(controllerB);

	ControllerA_Parent controllerA_Parent =
		collection.get(ControllerA_Parent.class);
	Assert.assertNotNull(controllerA_Parent);

	Controller controller = collection.get(Controller.class);
	Assert.assertNotNull(controller);

	Controller controller2 = collection.get(controller.getControllerId());
	Assert.assertEquals(controller, controller2);

	Collection<Controller> all = collection.getAll();
	Assert.assertEquals(3, all.size());

	Collection<Controller> allControllers = collection.getAll(Controller.class);
	Assert.assertEquals(3, allControllers.size());

	Collection<ControllerA_Child> allControllerA_Parents =
		collection.getAll(ControllerA_Child.class);
	Assert.assertEquals(1, allControllerA_Parents.size());

	Collection<ControllerB> allControllerBs =
		collection.getAll(ControllerB.class);
	Assert.assertEquals(0, allControllerBs.size());
  }

  @Test
  public void testCount() {
	Assert.assertEquals(3, collection.count(Controller.class));
	Assert.assertEquals(3, collection.count(ControllerA_Parent.class));
	Assert.assertEquals(1, collection.count(ControllerA_Child.class));
	Assert.assertEquals(0, collection.count(ControllerB.class));
  }
}
