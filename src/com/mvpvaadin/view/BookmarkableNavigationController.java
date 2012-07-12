package com.mvpvaadin.view;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

import com.mvpvaadin.bookmarkable.Bookmarkable;
import com.mvpvaadin.event.EventBus;
import com.mvpvaadin.event.ShowViewEvent;
import com.mvpvaadin.view.LiteNavigationController.NavigationControllerListener;
import com.vaadin.ui.UriFragmentUtility;
import com.vaadin.ui.UriFragmentUtility.FragmentChangedListener;


/**
 * This NavigationController uses the {@link Bookmarkable} annotation to 
 * @author Hannes Dorfmann
 *
 */
public class BookmarkableNavigationController extends 	UriFragmentUtility 
											implements 	NavigationController,
														FragmentChangedListener{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 564992821512011236L;
	private EventBus eventBus;
	private Set<NavigationControllerListener> listeners;
	private boolean showErrorOnUnknownUriFragment;
	
	private Map <String, Class<? extends ShowViewEvent>> viewEventMap;
	
	private static final String fragmentSeparator = "/";
	
	
	
	public BookmarkableNavigationController(EventBus eventBus, String packageToScan){
		this.eventBus = eventBus;
		this.setImmediate(true);
		scanAnotations(packageToScan);
		listeners = new LinkedHashSet<LiteNavigationController.NavigationControllerListener>();
	}
	

	private void scanAnotations(String packagePrefix){
		Reflections ref = new Reflections(packagePrefix);
		viewEventMap = new LinkedHashMap<String, Class<? extends ShowViewEvent>>();
		
		Set<Class<?>> annotated = 
	               ref.getTypesAnnotatedWith(Bookmarkable.class);
		
		for (Class<?> c: annotated){
			Bookmarkable bookmarkable = c.getAnnotation(Bookmarkable.class);
			Class<? extends ShowViewEvent> eventClass = (Class<? extends ShowViewEvent>) bookmarkable.eventClass();
			String uriFragment = bookmarkable.uriFragment();
			
			viewEventMap.put(uriFragment, eventClass);
		}
	
		
	}

	@Override
	public void setShowErrorMessageOnUnknownUriFragment(boolean showErrorMessage) {
		this.showErrorOnUnknownUriFragment = showErrorMessage;
	}

	@Override
	public void addListener(NavigationControllerListener l) {
		listeners.add(l);
	}

	@Override
	public void removeListener(NavigationControllerListener l) {
		listeners.remove(l);
	}

	@Override
	public void setCurrentView(NavigateableView view) {
	
		String uri = calculateUri(view);
		setFragment(uri, false);
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
	@Override
	public EventBus getEventBus() {
		return eventBus;
	}

	@Override
	public void setStartView(NavigateableView view) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fragmentChanged(FragmentChangedEvent source) {
		
		String fragment = source.getUriFragmentUtility().getFragment();
		System.out.println(fragment);
		
	}

}
