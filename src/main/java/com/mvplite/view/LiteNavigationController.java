package com.mvplite.view;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mvplite.event.EventBus;
import com.mvplite.event.Show404ViewEvent;
import com.vaadin.server.Page;

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
public class LiteNavigationController implements NavigationController, Page.UriFragmentChangedListener {


	private static final long serialVersionUID = 2585755661712329836L;

    private class HistoryEntry implements Serializable{

		private static final long serialVersionUID = 6852274305903891448L;

		public List<com.mvplite.event.Event> eventsToFire;

		public HistoryEntry(){
			eventsToFire = new LinkedList<>();
		}
	}

	private EventBus eventBus;
	private final Map<String, HistoryEntry> historyStack;
	private final Set<NavigationControllerListener> listeners;
	private boolean fire404OnUnknownURI;
	private boolean setCurrentViewCausedByHistoryChange;
    private Page page;


	public LiteNavigationController(){
		super();
		historyStack = new LinkedHashMap<>();
		listeners = new LinkedHashSet<>();
		setCurrentViewCausedByHistoryChange = false;
        this.page = Page.getCurrent();
        page.addUriFragmentChangedListener(this);
//        page.getUriFragment()
		setFire404OnUnknownUriFragment(false);
	}

	public LiteNavigationController(EventBus eventBus){
		this();
		this.eventBus = eventBus;
	}



	@Override
	public EventBus getEventBus(){
		return eventBus;
	}

	public void setEventBus(EventBus eb){
		this.eventBus = eb;
	}

	/* (non-Javadoc)
	 * @see com.mvplite.view.NavigationController#setShowErrorMessageOnUnknownUriFragment(boolean)
	 */
	@Override
	public void setFire404OnUnknownUriFragment(boolean showErrorMessage){
		this.fire404OnUnknownURI = showErrorMessage;
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


	private List<com.mvplite.event.Event> calculateEventsToFireList(NavigateableView view){
		List<com.mvplite.event.Event> events =
				new LinkedList<com.mvplite.event.Event>();

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
        page.setUriFragment("", false);
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
			 page.setUriFragment(uriFragment, false); // Seems not to work at first call
		}
		fireNavigatedTo(view);
	}

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
        public void uriFragmentChanged(Page.UriFragmentChangedEvent source) {

		// used by the back and forward browser button

		if (source == null)
			return;

		String uriFragment = source.getUriFragment();

		HistoryEntry entry = historyStack.get(uriFragment);

		if (entry == null){

			if (fire404OnUnknownURI){
				eventBus.fireEvent(new Show404ViewEvent());
			}

		}
		else
		{
			setCurrentViewCausedByHistoryChange = true;

			// fire the events that are needed to get to the state of uri fragment
			for (com.mvplite.event.Event e : entry.eventsToFire){
				eventBus.fireEvent(e);
			}

			setCurrentViewCausedByHistoryChange = false;
		}
	}
}
