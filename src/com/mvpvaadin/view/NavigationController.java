package com.mvpvaadin.view;

import com.mvpvaadin.event.EventBus;
import com.mvpvaadin.view.LiteNavigationController.NavigationControllerListener;

public interface NavigationController {

	public abstract void setShowErrorMessageOnUnknownUriFragment(
			boolean showErrorMessage);

	public abstract void addListener(NavigationControllerListener l);

	public abstract void removeListener(NavigationControllerListener l);

	public abstract void setCurrentView(NavigateableView view);
	
	public abstract EventBus getEventBus();

	/**
	 * Sets the view, that should be called, 
	 * if the empty Fragment (url without "#" or with hash but without continued Fragment)
	 * @param view
	 */
	public abstract void setStartView(NavigateableView view);

}