package com.mvplite.bookmarkable;

import com.mvplite.event.ShowViewEvent;

public @interface Bookmarkable {
	Class<? extends ShowViewEvent> eventClass();
	String uriFragment();
}
