package com.mvpvaadin.presenter;

import java.io.Serializable;

import com.mvpvaadin.event.EventBus;
import com.mvpvaadin.view.View;

public class Presenter <T extends View> implements Serializable {
	
	private static final long serialVersionUID = -8062395775037001922L;
	
	private T view;
	private EventBus eventBus;
	
	public Presenter(T view, EventBus eventBus)
	{
		setView(view);
		setEventBus(eventBus);
	}
	
	public T getView(){
		return view;
	}
	
	
	public void setView(T view){
		this.view = view;
	}

	public EventBus getEventBus() {
		return eventBus;
	}

	public void setEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
	}

}
