package test;

import org.junit.BeforeClass;
import org.junit.Test;

import com.mvplite.event.EventBus;
import com.mvplite.event.EventHandler;
import com.mvplite.event.ShowViewEvent;

public class EventBusTest {

	private static EventBus eventBus;
	
	@BeforeClass
	public static void setUpBeforeClass() {
		eventBus = new EventBus();
	}
	
	
	public static void main(String args[]){
		setUpBeforeClass();
		new EventBusTest().test();
	}

	@Test
	public void test() {
		
		ExampleHandler1 h1 = new ExampleHandler1();
		ExampleHandler1 h2 = new ExampleHandler1();
		eventBus.addHandler(h1);
		eventBus.addHandler(h2);
		eventBus.addHandler(this);
		System.out.println(eventBus.fireEvent(new ExampleEvent1("First", 42)));
		System.out.println(eventBus.fireEvent(new ExampleEvent2()));
		System.out.println(eventBus.fireEvent(new ShowViewEvent()));
		System.out.println(eventBus.fireEvent(new ExampleEvent1("Second", 23)));

	}
	
	@EventHandler
	public void onTestEvent2(ExampleEvent2 e)
	{
		System.out.println("TestEvent2 "+this);
	}
}
