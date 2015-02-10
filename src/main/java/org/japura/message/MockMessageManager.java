package org.japura.message;

public class MockMessageManager extends DefaultMessageManager{
  
  @Override
  public void publish(boolean synchronous, Message message) {
    super.publish(true, message);
  }
  
  @Override
  public void publish(boolean synchronous, Message message, Object publisher) {
    super.publish(true, message, publisher);
  }

}
