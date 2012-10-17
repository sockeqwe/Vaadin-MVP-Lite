package com.mvplite.view;

import com.mvplite.event.EventBus;
import com.mvplite.event.EventHandler;
import com.mvplite.event.Show404ViewEvent;
import com.mvplite.view.ui.Breadcrumbs;

public interface NavigationController {

	/**
	 * Should a {@link Show404ViewEvent} be fired,
	 * if the URL is unknown?
	 * The {@link Show404ViewEvent} can be handled via the normal {@link EventBus} 
	 * and {@link EventHandler} mechanisms 
	 * @param fireShow404ViewEvent true or false
	 */
	public abstract void setFire404OnUnknownUriFragment(
			boolean fireShow404ViewEvent);

	public abstract void addListener(NavigationControllerListener l);

	public abstract void removeListener(NavigationControllerListener l);

	/**
	 * Set the current View ({@link NavigateableView}).
	 * This method also invokes the URL generation and
	 * the corresponding {@link Breadcrumbs} elements.
	 * @param view
	 */
	public abstract void setCurrentView(NavigateableView view);
	
	public abstract EventBus getEventBus();

	/**
	 * BETA: 
	 * Sets the view, that should be called, 
	 * if the empty Fragment (url without "#" or with hash but without continued Fragment)
	 * @param view
	 */
	public abstract void setStartView(NavigateableView view);

}