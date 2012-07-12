package com.mvplite.view;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mvplite.event.EventBus;
import com.mvplite.event.EventHandler;
import com.vaadin.ui.UriFragmentUtility;
import com.vaadin.ui.UriFragmentUtility.FragmentChangedListener;
import com.vaadin.ui.Window.Notification;


/**
 * The {@link LiteNavigationController} is the simplest {@link NavigationController}.
 * The {@link LiteNavigationController} supports Browser history (back and forward) by generating 
 * uri fragments. <br />
 * <b> Note: </b> the uri fragments does not contain any state and therefore <b>bookmarks are 
 * not supported</b> by the {@link LiteNavigationController}. 
 * In the future a BookmarkableNavigationController will be implemented to support bookmarks.  
 * @author Hannes Dorfmann
 *
 */
public class LiteNavigationController extends UriFragmentUtility 
									implements NavigationController,
												FragmentChangedListener {
	
	
	private static final long serialVersionUID = 2585755661712329836L;

	
	
	private class HistoryEntry implements Serializable{
		
		private static final long serialVersionUID = 6852274305903891448L;
		
		public List<com.mvplite.event.Event<? extends EventHandler>> eventsToFire;
		
		public HistoryEntry(){
			eventsToFire = new LinkedList<com.mvplite.event.Event<? extends EventHandler>>();
		}
	}

	private EventBus eventBus;
	private Map<String, HistoryEntry> historyStack;
	private Set<NavigationControllerListener> listeners;
	private boolean errorMessageOnUnknownUriFragment;
	private boolean setCurrentViewCausedByHistoryChange;

	
	public LiteNavigationController(EventBus eventBus){
		super();
		this.setImmediate(true);
		this.eventBus = eventBus;
		historyStack = new LinkedHashMap<String, HistoryEntry>();
		listeners = new LinkedHashSet<NavigationControllerListener>();
		setCurrentViewCausedByHistoryChange = false;
		setShowErrorMessageOnUnknownUriFragment(false);
		this.addListener(this);
	}
	
	
	public EventBus getEventBus(){
		return eventBus;
	}
	
	/* (non-Javadoc)
	 * @see com.mvplite.view.NavigationController#setShowErrorMessageOnUnknownUriFragment(boolean)
	 */
	@Override
	public void setShowErrorMessageOnUnknownUriFragment(boolean showErrorMessage){
		this.errorMessageOnUnknownUriFragment = showErrorMessage;
	}
	

	
	/* (non-Javadoc)
	 * @see com.mvplite.view.NavigationController#addListener(com.mvplite.view.LiteNavigationController.NavigationControllerListener)
	 */
	@Override
	public void addListener(NavigationControllerListener l){
		listeners.add(l);
	}
	
	
	/* (non-Javadoc)
	 * @see com.mvplite.view.NavigationController#removeListener(com.mvplite.view.LiteNavigationController.NavigationControllerListener)
	 */
	@Override
	public void removeListener(NavigationControllerListener l){
		listeners.remove(l);
	}
	
	
	private List<com.mvplite.event.Event<? extends EventHandler>> calculateEventsToFireList(NavigateableView view){
		List<com.mvplite.event.Event<? extends EventHandler>> events = 
				new LinkedList<com.mvplite.event.Event<? extends EventHandler>>();
		
		while (view != null)
		{
			events.add(0, view.getEventToShowThisView());
			
			if (view instanceof NavigateableSubView)
				view = ((NavigateableSubView) view).getParentView();
			else
				view = null;
		}
		
		return events;
	}
	
	public void clearUriFragments(){
		setFragment("", false);
		requestRepaint();
	}
	
	private String calculateUri(NavigateableView view){
		String uri ="";
		
		while (view != null){
			uri = "/"+view.getUriFragment()+uri;
			
			if (view instanceof NavigateableSubView)
				view = ((NavigateableSubView) view).getParentView();
			else
				view = null;
		}
		
		return uri;
	}
	
	/* (non-Javadoc)
	 * @see com.mvplite.view.NavigationController#setCurrentView(com.mvplite.view.NavigateableView)
	 */
	@Override
	public void setCurrentView(NavigateableView view){
		
		if (!setCurrentViewCausedByHistoryChange){
			// Add url fragmentHistory support
			String uriFragment = calculateUri(view);
			HistoryEntry entry = new HistoryEntry();
			entry.eventsToFire = calculateEventsToFireList(view);
			
			if (historyStack.isEmpty())
				historyStack.put("", entry);
			
			historyStack.put(uriFragment, entry);
			 setFragment(uriFragment, false); // Seems not to work at first call
			 requestRepaint();
			//setUriFragmentJSImplementation(uriFragment);
		}
		
		fireNavigatedTo(view);
	}
	
	/**
	 * This is a native javascript implementation, 
	 * because {@link #setFragment(String)} seems not to work with the first call
	 * @param fragment
	
	private void setUriFragmentJSImplementation(String fragment){
		this.removeListener(this);
			getWindow().executeJavaScript("window.location.hash=\""+fragment+"\"");
			fireEvent(new FragmentChangedEvent(this));
		this.addListener(this);
	}
 */
	
	
	private void fireNavigatedTo(NavigateableView view){
		for (NavigationControllerListener l : listeners)
			l.onNavigatedTo(view);
	}
	
	
	/* (non-Javadoc)
	 * @see com.mvplite.view.NavigationController#setStartView(com.mvplite.view.NavigateableView)
	 */
	@Override
	public void setStartView(NavigateableView view){
		HistoryEntry entry = new HistoryEntry();
		entry.eventsToFire = calculateEventsToFireList(view);
		
		historyStack.put("", entry);
	}
	
	
	
	@Override
	public void fragmentChanged(FragmentChangedEvent source) {
		
		// used by the back and forward browser button
		
		if (source == null ||  source.getUriFragmentUtility()== null 
				|| source.getUriFragmentUtility().getFragment()==null)
			return;
		
		
		String uriFragment = source.getUriFragmentUtility().getFragment();
		
		HistoryEntry entry = historyStack.get(uriFragment);
		
		if (entry == null){
			
			if (errorMessageOnUnknownUriFragment){
				source.getComponent().getWindow().showNotification("Unknown history state", 
						"You try to access an unknown view via the url. Please don't change the url manualy",
						Notification.TYPE_ERROR_MESSAGE);
			}
			
		}
		else
		{
			setCurrentViewCausedByHistoryChange = true;
			
			// fire the events that are needed to get to the state of uri fragment
			for (com.mvplite.event.Event<? extends EventHandler> e : entry.eventsToFire){
				eventBus.fireEvent(e);
			}
			
			setCurrentViewCausedByHistoryChange = false;
		}
		
		
	}
	
	
	
	
	
	

}
