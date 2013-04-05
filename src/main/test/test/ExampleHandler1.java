package test;

import com.mvplite.event.EventHandler;

public class ExampleHandler1 {
	
	
	@EventHandler
	public void onTestEvent1(ExampleEvent1 e){
		System.out.println("Received 1 "+e+" "+this);
	}
	
	@EventHandler
	public void testit(ExampleEvent1 e){
		System.out.println("Received 2 "+e+" "+this);
	}
	
	@EventHandler
	public void onTest2(ExampleEvent2 e){
		System.out.println("on TestEvent2 "+this);
	}
	

}
