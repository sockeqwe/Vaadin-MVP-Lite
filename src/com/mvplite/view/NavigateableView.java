package com.mvplite.view;

import com.mvplite.event.ShowViewEvent;

/**
 * A {@link NavigateableView} is a {@link View} that is used in combination with an {@link NavigationController}.
 * @author Hannes Dorfmann
 *
 */
public interface NavigateableView extends View{
	
	/**
	 * The piece after the # (hash) in the url
	 * @return
	 */
	public String getUriFragment();
	
	
	public String getBreadcrumbTitle();
	
	
	/**
	 * This method is called by the {@link LiteNavigationController} to get the Event, that is needed to re
	 * @return
	 */
	public ShowViewEvent getEventToShowThisView();
	
	
}
