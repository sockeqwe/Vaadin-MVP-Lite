package com.mvplite.presenter;

import java.io.Serializable;

import com.mvplite.event.Event;
import com.mvplite.event.EventBus;
import com.mvplite.view.View;

/**
 * This is the base class for every {@link Presenter} and it provides the basic 
 * functionallity that is normally used with this model-view-presenter framework.
 * 
 * @see #getView()
 * @see #getEventBus()
 * @author Hannes Dorfmann
 *
 * @param <T>
 */
public class Presenter <T extends View> implements Serializable {
	
	private static final long serialVersionUID = -8062395775037001922L;
	
	private T view;
	private EventBus eventBus;

	public Presenter(){
		
	}
	
	public Presenter (T view)
	{
		setView(view);
	}

	public Presenter(T view, EventBus eventBus)
	{
		setView(view);
		setEventBus(eventBus);
	}
	
	/**
	 * Get the {@link View} that is associated to this presenter
	 * @return
	 */
	public T getView(){
		return view;
	}
	
	/**
	 * Set the view
	 * @param view
	 */
	public void setView(T view){
		this.view = view;
	}

	/**
	 * Get the {@link EventBus} to fire any {@link Event}s you want to
	 * @return
	 */
	public EventBus getEventBus() {
		return eventBus;
	}

	/**
	 * Set the {@link EventBus}
	 * @param eventBus
	 */
	public void setEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
	}

}
