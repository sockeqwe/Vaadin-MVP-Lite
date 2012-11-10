package test;

import com.mvplite.event.Event;

public class TestEvent1 extends Event {

	private static final long serialVersionUID = 5199602648963300772L;

	public TestEvent1(String val1, int val2){
		this.val1 = val1;
		this.val2 = val2;
	}
	
	public String val1;
	public int val2;
	
	
	public String toString(){
		return super.toString()+" "+val1+" "+val2;
	}
}
