package com.mvplite.event;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;


public final class GlobalEventBus {
	
	/**
	 * The Client represents a Client, since a user can have more than one clients (Browser-windows)
	 * opened at the same time.
	 * @author Hannes Dorfmann
	 *
	 */
	public final static class Client implements Comparable<Client> {
		public String sessionID;
		public long lastAccess;
		public List<String> groupMemberships;
		public List<Event> queuedEvents;
		
		public Client(String sessionID, List<String> groupMemberships){
			this.sessionID = sessionID;
			this.groupMemberships = groupMemberships;
			this.lastAccess = new Date().getTime();
			this.queuedEvents = new ArrayList<Event>();
		}
		

		@Override
		public int compareTo(Client o) {
			return this.sessionID.compareTo(o.sessionID);
		}

		@Override
		public String toString(){
			return queuedEvents.size()+ " "+ queuedEvents.toString()+" "+super.toString();
		}
	
	}
	
	
	private static long MEMORY_CLEAN_TIMEOUT = 5 * 60 * 1000;
	private static long CLIENT_TIMEOUT = 1 * 60 * 1000;
	private static long lastCleanUp = new Date().getTime();
	
	
	private static HashMap<String, TreeSet<Client> > userClientMap = 
			new HashMap<String, TreeSet<Client>>();
	
	private static HashMap<String, List<Client>> groupClientMap = 
			new HashMap<String, List<Client>>();
	
	public static void addClient(String username, String sessionID, 
			List<String> groupMemberships){
	
		Client c = new Client(sessionID, groupMemberships);
		
		TreeSet<Client> clientsOfUser = userClientMap.get(username);
		
		if (clientsOfUser == null) // No other Client of the same user is present
		{	// TODO synchornized Thread Safe
			clientsOfUser = (new TreeSet<Client>());
			userClientMap.put(username, clientsOfUser);
		}
		
		clientsOfUser.add(c);
		
		if (groupMemberships!=null)
			for (String name : groupMemberships)
			{
				List<Client> group = groupClientMap.get(name);
				if (group == null){
					group = new ArrayList<GlobalEventBus.Client>();
					groupClientMap.put(name, group);
				}
				
				group.add(c);
			}
		
	}
	
	
	public static void removeClient(String username, String sessionID){
		
		TreeSet<Client> clientsOfUser = userClientMap.get(username);
		Client toRemove = null;
	
		if (clientsOfUser != null){
			toRemove = null;
			for (Client c: clientsOfUser){
				if (c.sessionID.equals(sessionID))
				{	
					toRemove = c;
					break;
				}
				
			}
			
			if (toRemove != null)
				clientsOfUser.remove(toRemove);
		}
		
		
		// remove group membershipments
		if (toRemove != null)
			for (String groupName : toRemove.groupMemberships)
			{
				List<Client> groups = groupClientMap.get(groupName);
				
				if (groups!=null)
				for (Client client : groups)
					if (client.sessionID.equals(sessionID))
					{
						toRemove = client;
						break;
					}
				
				groups.remove(toRemove);
			}	
	}
	
	
	
	
	/**
	 * Fire a event which is delivered to all clients of the user (identified by the username)
	 * @param username
	 * @param event
	 */
	public static void fireEventToUser(String username, Event event){
		
		memoryCleanUp();
		
		TreeSet<Client> clients = userClientMap.get(username);
		
		if (clients != null) // If there is at least one client
		{
			for (Client c : clients)
				c.queuedEvents.add(event);
		}
		
	}
	
	/**
	 * Fire a {@link Event} (broadcast) to every client, excepted the sender himself 
	 * (which is identified by the session Id)
	 * @param event
	 * @param sessionIdOfSender
	 */
	public static void fireBroadcastEvent(Event event,
			String sessionIdOfSender){
		
		memoryCleanUp();
		
		for (TreeSet<Client> e : userClientMap.values())
		{
			for (Client c: e)
				if (c.sessionID.equals(sessionIdOfSender))
					continue;
				else
					c.queuedEvents.add(event);
		}
		
	}

	/**
	 * Fire a {@link Event} (broadcast) to every registered Client.
	 * The {@link Event} is also delivered to the sender.
	 * @param event
	 */
	public static void fireBroadcastEvent(Event event){
		
		memoryCleanUp();
		
		for (TreeSet<Client> e : userClientMap.values())
		{
			for (Client c: e)
				c.queuedEvents.add(event);
		}
		
	}
	
	/**
	 * Fire the {@link Event} to all group-member-clients excluding the sender itself
	 * @param e
	 * @param sessionIdOfSender
	 */
	public static void fireGroupBroadcastEvent(Event e,
			String groupName, String sessionIdOfSender){
		
		memoryCleanUp();
		
		List<Client> clients = groupClientMap.get(groupName);
		
		if (clients!=null)
			for (Client c: clients)
				if (sessionIdOfSender.equals(c.sessionID))
						continue;
				else
					c.queuedEvents.add(e);
	}
	
	
	/**
	 * Fire a {@link Event} to every client,
	 * that is member of a group (identified by the passed groupName)
	 * @param e
	 */
	public static void fireGroupBroadcastEvent(Event e, 
			String groupName){
		
		memoryCleanUp();
		
		List<Client> clients = groupClientMap.get(groupName);
		if (clients!=null)
			for (Client c: clients)
				c.queuedEvents.add(e);
	}
	
	/**
	 * Retrieve all queued {@link Event}s for a client.
	 * This method is used by {@link GlobalEventBusDispatcher}.
	 * @param username The username
	 * @param sessionID the session id of the client
	 * @return {@link List} of {@link Event}s or null
	 */
	public static List<Event> getEventsFor(String username, String sessionID){
		
		TreeSet<Client> clients = userClientMap.get(username);
		
		if (clients != null)
		{
			for (Client c: clients)
				if (c.sessionID.equals(sessionID)){
					List<Event> ret = 
							new ArrayList<Event>(c.queuedEvents);
					
					c.queuedEvents.clear();
					c.lastAccess = new Date().getTime();
					return ret;
				}
					
		}
		
		
		return null;
	}
	
	
	private static void memoryCleanUp(){
		
		long timeStamp = new Date().getTime();
		
		if (lastCleanUp + MEMORY_CLEAN_TIMEOUT < timeStamp){
			
			for (TreeSet<Client> clients : userClientMap.values()){
				
				List<Client> toRemove = new ArrayList<GlobalEventBus.Client>();
				
				for (Client c: clients)
				if (c.lastAccess + CLIENT_TIMEOUT < timeStamp){
					toRemove.add(c);
					c.queuedEvents.clear();
					
					// Remove memberships
					for (String groupName : c.groupMemberships)
					{
						List<Client> s = groupClientMap.get(groupName);
						s.remove(c);
					}
				}
				
				if (!toRemove.isEmpty())
				{
					clients.removeAll(toRemove);
				}
			}
			
			lastCleanUp = new Date().getTime();
		}
		
	}

}
