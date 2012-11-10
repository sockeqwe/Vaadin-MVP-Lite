package com.mvplite.event;

/**
 * This is the base event class for events that should show a View
 * @author Hannes Dorfmann
 *
 */
public class ShowViewEvent extends Event {

	
	private static final long serialVersionUID = 2227091598650502970L;


	/**
	 * The dataParameter is used for the upcomming bookmarkable feature.
	 * Meanwhile for the LiteNavigationController this is not in use so far.
	 */
	private String dataParameter;
	
	/**
	 * Set the dataParameter, that is extraceted by the uri. This 
	 * <b> Note: </b> This method is called by the BookmarkableNavigationController,
	 * which is currently not implementes. Therefore this method is currently not in use, 
	 * but will be used in
	 * the future when bookmarkable uris are supported 
	 * @param dataParameter
	 */
	public void setDataParamerter(String dataParameter){
		this.dataParameter = dataParameter;
	}
	
	
	/**
	 * Get the dataParameter, that is extraceted by the uri.
	 * <b> Note: </b> This method is currently not used, but will be used in
	 * the future when bookmarkable uris are supported 
	 * @param dataParameter
	 */
	public String getDataParameter(){
		return dataParameter;
	}
	
	
	
}
