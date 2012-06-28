package com.mvpvaadin.bookmarkable;

import com.mvpvaadin.event.ShowViewEvent;

public @interface Bookmarkable {
	Class<? extends ShowViewEvent> eventClass();
	String uriFragment();
}
