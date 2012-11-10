package com.mvplite.event;

import java.util.List;
import com.github.wolfie.refresher.Refresher;
import com.github.wolfie.refresher.Refresher.RefreshListener;


public class RefresherGlobalEventBusDispatcher extends Refresher 
											  implements GlobalEventBusDispatcher,
											  			 RefreshListener{

	private static final long serialVersionUID = -3752482867332351643L;
	
	
	private class RefreshThread extends Thread{
		
		public void run(){
			
			isRunning = true;
			
			while(isRunning)
			{
				receivedEvents = GlobalEventBus.getEventsFor(username, sessionId);
				try {
					sleep(SLEEP_TIME);
				} catch (InterruptedException e) {
					isRunning = false;
				}
			}
		}
	}
	

	private Thread refreshThread;
	private EventBus localEventBus;
	private boolean isRunning;
	private List<String> groupNames;
	private String username, sessionId;
	
	private List<com.mvplite.event.Event> receivedEvents;
	
	public static final long REFRESH_INTERVAL = 1500;
	private static final long SLEEP_TIME = REFRESH_INTERVAL;
	
	public RefresherGlobalEventBusDispatcher(String username, String sessionId, 
			List<String> groupNames, EventBus localEventBus){
		this.localEventBus = localEventBus;
		this.sessionId = sessionId;
		this.username = username;
		this.groupNames = groupNames;
		this.addListener(this);
		this.setRefreshInterval(REFRESH_INTERVAL);
	}
	
	
	
	public void start(){
		GlobalEventBus.addClient(username, sessionId, groupNames);
		refreshThread = new RefreshThread();
		refreshThread.start();
	}
	
	
	public void stop(){
		GlobalEventBus.removeClient(username, sessionId);
		refreshThread.interrupt();
		
	}
	
	
	
	@Override
	public void refresh(Refresher source) {
		if (receivedEvents!=null){
			for (com.mvplite.event.Event e : receivedEvents)
				localEventBus.fireEvent(e);
		}
	}

}
