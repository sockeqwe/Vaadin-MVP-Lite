package com.mvpvaadin.event;

/**
 * This is the base event class for events that should show a View
 * @author Hannes Dorfmann
 *
 */
public abstract class ShowViewEvent extends Event<ShowViewEventHandler>{

	private static final long serialVersionUID = 2227091598650502970L;


	private String dataParameter;
	
	public void setDataParamerter(String dataParameter){
		this.dataParameter = dataParameter;
	}
	
	
	public String getDataParameter(){
		return dataParameter;
	}
	
	
	
}
