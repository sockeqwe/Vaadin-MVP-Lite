package com.mvplite.view;

import com.mvplite.view.ui.Breadcrumbs;

/**
 * This Listener is used by ui components (i.e. {@link Breadcrumbs}) to 
 * listen to a {@link NavigationController} of any navigation changes to update the gui
 * @author Hannes Dorfmann
 *
 */
public interface NavigationControllerListener{
	
	/**
	 * The current view
	 * @param view
	 */
	public void onNavigatedTo(NavigateableView view);
}