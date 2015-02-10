package message;

import java.util.HashSet;
import java.util.Set;

import org.japura.Application;
import org.japura.message.Message;
import org.japura.message.MessageFilter;
import org.japura.message.Subscriber;
import org.japura.message.SubscriberFilter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MessageTest{

  @Before
  public void init() {
	Application.reset();
  }

  @Test
  public void registerTest() {
	SubscriberTest st1 = new SubscriberTest();
	SubscriberTest st2 = new SubscriberTest();

	Assert.assertEquals(false, Application.getMessageManager()
		.isRegistered(st1));
	Assert.assertEquals(false, Application.getMessageManager()
		.isRegistered(st2));

	Application.getMessageManager().register(st1);
	Application.getMessageManager().register(st2);

	Assert
		.assertEquals(true, Application.getMessageManager().isRegistered(st1));
	Assert
		.assertEquals(true, Application.getMessageManager().isRegistered(st2));

	// 2 + ApplicationSubscriber
	Assert.assertEquals(3, Application.getMessageManager().getSize());

	Application.getMessageManager().unregister(st2);

	Assert.assertEquals(2, Application.getMessageManager().getSize());
  }

  @Test
  public void publisherTest() {
	SubscriberTest st = new SubscriberTest();
	Application.getMessageManager().register(st);

	Message msg1 = new Message();
	Application.getMessageManager().publish(true, msg1);
	Assert.assertNotNull(st.getLastPublisher());
	Assert.assertEquals(st.getLastPublisher(), Application.getMessageManager());

	String publisher = "PUBLISHER";
	Message msg2 = new Message();
	Application.getMessageManager().publish(true, msg2, publisher);
	Assert.assertNotNull(st.getLastPublisher());
	Assert.assertEquals(st.getLastPublisher(), publisher);
  }

  @Test(timeout = 5000)
  public void multiPublisherTest() {
	DelaySubscriberTest dst = new DelaySubscriberTest(150);

	Application.getMessageManager().register(dst);

	Message msg = new Message();

	String p1 = "1";
	String p2 = "2";

	Application.getMessageManager().publish(false, msg, p1);
	Application.getMessageManager().publish(false, msg, p2);

	while (Application.getMessageManager().isPublishing()) {
	  Thread.yield();
	}

	Assert.assertNotNull(dst.getFirstPublisher());
	Assert.assertEquals(p1, dst.getFirstPublisher());
	Assert.assertNotNull(dst.getLastPublisher());
	Assert.assertEquals(p2, dst.getLastPublisher());
  }

  @Test(timeout = 5000)
  public void massivePublisherTest() {
	MassiveSubscriberTest s = new MassiveSubscriberTest();
	Application.getMessageManager().register(s);

	int total = 5000;

	Message msg = new Message();
	String publisher = "publisher";
	for (int i = 0; i < total; i++) {
	  Application.getMessageManager().publish(true, msg, publisher);
	}
	Assert.assertEquals(1, s.getClasses().size());
  }

  @Test
  public void publishTest() {
	SubscriberTest st = new SubscriberTest();
	Application.getMessageManager().register(st);
	Message msg = new Message();
	Application.getMessageManager().publish(true, msg);
	Assert.assertEquals(msg, st.getReceivedMessage());
  }

  @Test
  public void subscriberFilterTest() {
	SubscriberTest st = new SubscriberTest();
	Application.getMessageManager().register(st);
	Message msg = new Message();
	msg.addSubscriberFilter(new SubscriberFilter() {
	  @Override
	  public boolean accepts(Subscriber subscriber) {
		return false;
	  }
	});
	Application.getMessageManager().publish(true, msg);
	Assert.assertNull(st.getReceivedMessage());
  }

  @Test
  public void messageFilterTest() {
	SubscriberTest st = new SubscriberTest();
	Application.getMessageManager().register(st);
	Application.getMessageManager().addMessageFilter(st, new MessageFilter() {
	  @Override
	  public boolean accepts(Message message) {
		return false;
	  }
	});

	Message msg = new Message();
	Application.getMessageManager().publish(true, msg);
	Assert.assertNull(st.getReceivedMessage());
  }

  private static class MassiveSubscriberTest implements Subscriber{
	private Set<Class<?>> classes = new HashSet<Class<?>>();

	@Override
	public void subscribe(Message message, Object publisher) {
	  classes.add(publisher.getClass());
	}

	public Set<Class<?>> getClasses() {
	  return classes;
	}
  }

  private static class SubscriberTest implements Subscriber{
	private Message receivedMessage;
	private Object lastPublisher;
	private Object firstPublisher;

	@Override
	public void subscribe(Message message, Object publisher) {
	  this.receivedMessage = message;
	  this.lastPublisher = publisher;
	  if (this.firstPublisher == null) {
		this.firstPublisher = publisher;
	  }
	}

	public Message getReceivedMessage() {
	  return receivedMessage;
	}

	public Object getFirstPublisher() {
	  return firstPublisher;
	}

	public Object getLastPublisher() {
	  return lastPublisher;
	}
  }

  private static class DelaySubscriberTest extends SubscriberTest{
	private int delay;

	public DelaySubscriberTest(int delay) {
	  this.delay = delay;
	}

	@Override
	public void subscribe(Message message, Object publisher) {
	  try {
		Thread.sleep(delay);
	  } catch (InterruptedException e) {
		e.printStackTrace();
	  }
	  super.subscribe(message, publisher);
	}

  }

}