package controller;

import org.japura.controller.Context;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ContextTest{

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void test1() {
	thrown.expect(IllegalArgumentException.class);
	new Context(null);
  }

  @Test
  public void test2() {
	thrown.expect(IllegalArgumentException.class);
	new Context("");
  }

  @Test
  public void test3() {
	thrown.expect(IllegalArgumentException.class);
	new Context(" ");
  }

  @Test
  public void test4() {
	thrown.expect(IllegalArgumentException.class);
	new Context(Context.MAIN_CONTEXT);
  }

  @Test
  public void test5() {
	Context context = new Context("TEST");
	Assert.assertEquals("TEST", context.getName());
  }

  @Test
  public void test6() {
	Context context = new Context("TEST");
	Assert.assertNotNull(context.getId());
  }

  @Test
  public void test7() {
	Context context = new Context("TEST");
	Assert.assertEquals(true, context.equals(context));
  }

  @Test
  public void test8() {
	Context context1 = new Context("TEST");
	Context context2 = new Context("TEST");
	Assert.assertEquals(false, context1.equals(context2));
  }

  @Test
  public void test9() {
	Context context = new Context("TEST");
	Assert.assertEquals(false, context.equals(null));
  }

  @Test
  public void test10() {
	Context context = new Context("TEST");
	Assert.assertEquals(false, context.equals(""));
  }

}
