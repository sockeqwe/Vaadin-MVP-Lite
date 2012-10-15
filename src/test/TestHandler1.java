package test;

import com.mvplite.event.EventHandler;

public class TestHandler1 {
	
	
	@EventHandler
	public void onTestEvent1(TestEvent1 e){
		System.out.println("Received 1 "+e.getClass().getName());
	}
	
	@EventHandler
	public void testit(TestEvent1 e){
		System.out.println("Received 2 "+e.getClass().getName());
	}

}
