package com.mvplite.view.ui;

import com.mvplite.view.NavigateableView;
import com.mvplite.view.NavigationController;
import com.mvplite.view.ui.Breadcrumbs.BreadcrumbElementFactory;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.BaseTheme;

public class ArrowBreadcrumbElementFactory implements BreadcrumbElementFactory{
	
	private static final long serialVersionUID = -2513158352408693591L;
	
	private static String elementStyleName = "niceArrowElement";
	private static String firstElementStyleName = "niceArrowElement-first";
	private static String elementZIndexHelper = "niceArrowElementZIndex";
	
	public Component createElement(final NavigationController controller,
			final NavigateableView view, int index, int count) {

		Button b = new Button(view.getBreadcrumbTitle());
		b.setStyleName(BaseTheme.BUTTON_LINK);
		b.addStyleName(elementStyleName);
		b.addStyleName(elementZIndexHelper+(20-index));
		b.setSizeUndefined();
		
		b.addListener(new ClickListener() {
			private static final long serialVersionUID = 5571355154543327126L;
			public void buttonClick(ClickEvent event) {
				controller.getEventBus().fireEvent(view.getEventToShowThisView());
			}
		});
		
		if (index == 0)
			b.addStyleName(firstElementStyleName);
		
		return b;
	}

	public void updateButtonTexts(Button button, NavigateableView view) {
		// TODO Auto-generated method stub
		
	}

	public void setBreadcrumbElementStyleName(String styleName) {
		// TODO Auto-generated method stub
		
	}

	
	
	
}
