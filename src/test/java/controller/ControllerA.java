package controller;

import javax.swing.JComponent;

import org.japura.controller.Context;
import org.japura.controller.Controller;
import org.japura.controller.DefaultController;

public class ControllerA extends DefaultController<JComponent>{

  public ControllerA() {
	this(null, null);
  }

  public ControllerA(Context context, Controller parentController) {
	super(context, parentController);
  }

  @Override
  public boolean isComponentInstancied() {
	return false;
  }
  
  @Override
  public JComponent buildComponent() {
    return null;
  }

}
