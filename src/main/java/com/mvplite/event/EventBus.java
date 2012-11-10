package com.mvplite.event;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * @author Hannes Dorfmann
 *
 */
class EventMethodCache implements Serializable{
	
	
	private static final long serialVersionUID = -3835595439788993624L;
	
	private final Map<Class<?>, Set<Method>> methodMap;
	
	public EventMethodCache(){
		methodMap = new ConcurrentHashMap<Class<?>, Set<Method>>();
	}
	
	
	/**
	 * Adds a Method to the {@link #eventMethodsChache}
	 * @param c
	 * @param m
	 */
	public void addMethodToCache(Class<?> c, Method m){
		Set<Method> methods = methodMap.get(c);
		
		if (methods == null){
			methods = new LinkedHashSet<Method>();
			methodMap.put(c, methods);
		}
		
		methods.add(m);
	}
	
	
	public void clear(){
		methodMap.clear();
	}
	
	
	public void removeCachedOf(Class<?> c){
		methodMap.remove(c);
	}
	
	
	public Set<Method> getMethods(Class<?> c)
	{
		return methodMap.get(c);
	}
	
	
	public boolean isClassCached(Class<?> c){
		return methodMap.containsKey(c);
	}
	
}

/**
 * The {@link EventDispatcher} is responsible for dispatching / delivering
 * a Event to the corresponding {@link EventHandler} - Method.
 * This is realized by using reflections, especially {@link Method#invoke(Object, Object...)}
 * @author Hannes Dorfmann
 *
 */
class EventDispatcher implements Serializable{
	
	private static final long serialVersionUID = -7359501691640084178L;
	
	private final Object target;
	private final Method method;
	
	
	public EventDispatcher(Object target, Method method){
		this.target = target;
		this.method = method;
		this.method.setAccessible(true);
	}
	
	public Object getTarget(){
		return target;
	}
	
	public void dispatchEvent(Object event){
	    
		try {
	      method.invoke(target, new Object[] { event });
	    } catch (IllegalArgumentException e) {
	      throw new Error("Method rejected target/argument: " + event, e);
	    } catch (IllegalAccessException e) {
	      throw new Error("Method became inaccessible: " + event, e);
	    } catch (InvocationTargetException e) {
	      if (e.getCause() instanceof Error) {
	        throw (Error) e.getCause();
	      }
	      else
	    	  throw new Error (e);
	    }
	}
	
	@Override 
	public int hashCode() {
	    final int PRIME = 31;
	    return (PRIME + method.hashCode()) * PRIME
	        + System.identityHashCode(target);
	  }

	  @Override 
	  public boolean equals(Object o) {
	    if (o instanceof EventDispatcher) {
	    	EventDispatcher other = (EventDispatcher) o;
	    	return target == other.target && method.equals(other.method);
	    }
	    
	    return false;
	  }
}


/**
 * {@link Event}s can be fired to the {@link EventBus} and the {@link EventBus}
 * is the component, that deliver / dispatch the {@link Event}s to the registered {@link EventHandler}.
 * @see #fireEvent(Event)
 * @see #addHandler(EventType, EventHandler)
 * @see #removeHandler(EventHandler)
 * @author Hannes Dorfmann
 *
 */
public class EventBus implements Serializable {
	
	private static final long serialVersionUID = 5500479291713928578L;
	
	private static final EventMethodCache eventMethodChache = new EventMethodCache();
	private final Map<Class<? extends Event>, Set<EventDispatcher>> handlerMap;
	
	private static boolean caching = true;
	
	
	public EventBus(){
		 handlerMap = new ConcurrentHashMap<Class<? extends Event>, Set<EventDispatcher>>();
	}
	
	/**
	 * Enable or disable caching 
	 * @param caching
	 */
	public void setUseCache(boolean caching)
	{
		EventBus.caching = true;
	}
	
	/**
	 * Get the Class of the {@link Event}.
	 * The passed {@link Method} must be a valid {@link EventHandler}-annotated
	 * method with exactly one parameter (the {@link Event}).
	 * This method is a little helper method and is only used by the {@link EventBus} internally.
	 * @param m
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Class<? extends Event> getEventClass(Method m){
		return (Class<? extends Event>) m.getParameterTypes()[0];
	}
	
	/**
	 * Registers an  {@link EventHandler}  for the specified {@link EventType}
	 * @param handler
	 */
	public void addHandler(Object handler){
		
		boolean added = false;
		
		if (caching){
			if (!eventMethodChache.isClassCached(handler.getClass())) 
				added = scanHandlerAndCreateEventDispatcher(handler); // This class is not cached, so scan the class
			else
				// This class has been cached (has been already scanned), so build the EventDispatcher from Cache
				added = createEventDispatchersFromCache(handler);
		}
		else
			added = scanHandlerAndCreateEventDispatcher(handler);
		
		if (!added)
			throw new Error("No @EventHandler annotated Method found in "+handler.getClass().getName());
	
	}
	
	
	private boolean scanHandlerAndCreateEventDispatcher(Object handler){
		boolean added = false;
		for (Method m : handler.getClass().getMethods())
		{
			if (!m.isAnnotationPresent(EventHandler.class))
				continue;
			
			Class<?> params [] = m.getParameterTypes();
			if (params.length == 1 && isEventClass(params[0])){
				// This Method is a Valid EventHandler
				EventDispatcher disp = new EventDispatcher(handler, m);
				addEventDispatcher(getEventClass(m), disp);
				added = true;
				
				if (caching)
					eventMethodChache.addMethodToCache(handler.getClass(), m);
			}
			else
				throw new Error("You have annotated the Method "+m.getName()+" with @EventHandler, " +
						"but this method did not match the required one Parameter (exactly one) of the type Event");
		}
		
		return added;
	}
	
	
	private boolean isEventClass(Class<?> clazz){
		
		Class<?> c = clazz;
		while (c!=null){
			if (c.equals(Event.class))
				return true;
			
			c = c.getSuperclass();
		}
		
		return false;
		
	}
	
	/**
	 * Creates {@link EventDispatcher}s by unsing the {@link EventMethodCache}.
	 * That means, that the class of the passed handler has already be scanned for
	 * {@link EventHandler} annotations and all information about building the
	 * {@link EventDispatcher}s are present in the {@link EventMethodCache}.
	 * @param handler
	 */
	private boolean createEventDispatchersFromCache(Object handler){
	
		Set<Method> methods = eventMethodChache.getMethods(handler.getClass());
		
		if (methods == null)
			throw new Error("The class "+handler.getClass().getName()+" has not been cached until now. However the EventBus tries to create a EventDispatcher from the cache.");
		
		for (Method m : methods){
			EventDispatcher disp = new EventDispatcher(handler, m);
			addEventDispatcher(getEventClass(m), disp);
		}
		
		return !methods.isEmpty();
	}
	
	/**
	 * Add a {@link EventDispatcher} for the passed {@link Event}-Class
	 * @param eventClass
	 * @param disp
	 */
	private void addEventDispatcher(Class<? extends Event> eventClass, 
			EventDispatcher disp){
		
		Set<EventDispatcher> dispatchers = handlerMap.get(eventClass);
		
		if(dispatchers == null){
			dispatchers = new LinkedHashSet<EventDispatcher>();
			handlerMap.put(eventClass, dispatchers);
		}
		
		dispatchers.add(disp);
	}
	
	
	
	/**
	 * Removes an Handler (a Object with {@link EventHandler} annotated methods) from
	 * the {@link EventBus}. That means, that future fired {@link Event}s are no longer
	 * dispatched / delivered to the passed handler
	 * @param handler
	 */
	public void removeHandler(Object handler){
		
		Set<EventDispatcher> toRemove = new LinkedHashSet<EventDispatcher>();
		for (Set<EventDispatcher> dispatchers : handlerMap.values()){
			for (EventDispatcher d: dispatchers)
				if (d.getTarget() == d)
					toRemove.add(d);
			
			dispatchers.removeAll(toRemove);
			toRemove.clear();
		}
	}
	
	
	/**
	 * Fires a Event to the EventBus to inform all registered EventHandlers about this Event
	 * @param event
	 * @return true if at least one {@link EventHandler} is registered and has received the passed event,
	 * otherwise false
	 */
	public boolean fireEvent(Event event){
		
		Set<EventDispatcher> dispatchers = handlerMap.get(event.getClass());
		if (dispatchers == null || dispatchers.isEmpty())
			return false;
		
		for (EventDispatcher disp : dispatchers)
			disp.dispatchEvent(event);
		
		return true;
	}
	
	
	
}
