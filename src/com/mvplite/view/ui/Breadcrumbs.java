package com.mvplite.view.ui;

import java.util.LinkedList;
import java.util.List;

import com.mvplite.view.NavigateableSubView;
import com.mvplite.view.NavigateableView;
import com.mvplite.view.NavigationController;
import com.mvplite.view.NavigationControllerListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.BaseTheme;

/**
 * This is a UI Component, that displays breadcrumbs for the 
 * {@link NavigateableView} / {@link NavigateableSubView}, which is currently
 * displayed on screen.
 * To customize the look of this component, you should write your own 
 * {@link SeparatorFactory} and {@link BreadcrumbElementFactory} and set it with
 * {@link #setBreadcrumbElementFactory(BreadcrumbElementFactory)}
 * {@link #setSeparatorFactory(SeparatorFactory)}
 * 
 * @author Hannes Dorfmann
 */
public class Breadcrumbs extends HorizontalLayout implements NavigationControllerListener{
	

	private static final long serialVersionUID = -7308648462712616003L;

	public static String BREADCRUMB_ELEMENT = "breadcrumb-element";

	
	
	/**
	 * Factory interface for creating breadcrumb separators.
	 * 
	 * @see Breadcrumbs#setSeparatorFactory(SeparatorFactory)
	 * @author Petter Holmström
	 * @since 1.0
	 */
	public static interface SeparatorFactory extends java.io.Serializable {
		/**
		 * Creates and returns a component to be used as a separator between
		 * breadcrumbs.
		 */
		Component createSeparator();
	}

	/**
	 * Default implementation of {@link SeparatorFactory}. The separators are
	 * labels containing the "»" character and having the
	 * {@link Breadcrumbs#BREADCRUMB_ELEMENT} style.
	 * 
	 * @author Petter Holmström
	 * @since 1.0
	 */
	public static class DefaultSeparatorFactory implements SeparatorFactory {

		private static final long serialVersionUID = 7957216244739746986L;

		@Override
		public Component createSeparator() {
			final Label separator = new Label("»");
			separator.setSizeUndefined();
			separator.addStyleName(BREADCRUMB_ELEMENT);
			return separator;
		}
	}

	/**
	 * Factory interface for creating breadcrumb buttons.
	 * 
	 * @see Breadcrumbs#setBreadcrumbElementFactory(BreadcrumbElementFactory)
	 * @author Petter Holmström
	 * @since 1.0
	 */
	public static interface BreadcrumbElementFactory extends java.io.Serializable {

		/**
		 * Creates and returns a button for the specified view. The click
		 * listener will be registered by the breadcrumbs component.
		 */
		Button createButton(NavigateableView view);

		/**
		 * Updates the button texts. This method is called when the display name
		 * and/or the description of the specified view are changed.
		 */
		void updateButtonTexts(Button button, NavigateableView view);
		

		public void setBreadcrumbElementStyleName(String styleName);
	}

	/**
	 * Default implementation of {@link BreadcrumbElementFactory}. The created buttons have
	 * the {@link BaseTheme#BUTTON_LINK} and
	 * {@link Breadcrumbs#BREADCRUMB_ELEMENT} styles.
	 * 
	 * @author Petter Holmström
	 * @since 1.0
	 */
	public static class DefaultBreadcrumbElementFactory implements BreadcrumbElementFactory {

		private static final long serialVersionUID = 8031407455065485896L;
		
		@Override
		public Button createButton(NavigateableView view) {
			final Button btn = new Button();
			btn.setStyleName(BaseTheme.BUTTON_LINK);
			btn.setSizeUndefined();
			btn.addStyleName(BREADCRUMB_ELEMENT);
			updateButtonTexts(btn, view);
			return btn;
		}

		@Override
		public void updateButtonTexts(Button button, NavigateableView view) {
			button.setCaption(view.getBreadcrumbTitle());
		}

		@Override
		public void setBreadcrumbElementStyleName(String styleName) {
			BREADCRUMB_ELEMENT = styleName;
		}
	}
	

	private SeparatorFactory separatorFactory;
	private BreadcrumbElementFactory buttonFactory;
	private HorizontalLayout breadcrumbElementContainer;
	private NavigationController navigationController;
	
	public Breadcrumbs(NavigationController controller){
		buttonFactory = new DefaultBreadcrumbElementFactory();
		separatorFactory = new DefaultSeparatorFactory();
		breadcrumbElementContainer = new HorizontalLayout();
		breadcrumbElementContainer.setSizeUndefined();
		this.addComponent(breadcrumbElementContainer);
		this.setBreadcrumListAlignment(Alignment.MIDDLE_LEFT);
		controller.addListener(this);
		this.navigationController = controller;
	}
	
	/**
	 * The style must be set before adding the first BreadcrumbElement
	 * @param styleName
	 */
	public void setBreadcrumbElementStyleName(String styleName){
		BREADCRUMB_ELEMENT = styleName;
	}
	
	/**
	 * Set the position / alignment of the breadcrumbs-list in the whole {@link Breadcrumbs} 
	 * @param alignment {@link Alignment}
	 */
	public void setBreadcrumListAlignment(Alignment alignment){
		this.setComponentAlignment(breadcrumbElementContainer, alignment);
	}

	/**
	 * Returns the separator factory to use for creating separators between
	 * breadcrumb buttons.
	 */
	public SeparatorFactory getSeparatorFactory() {
		return separatorFactory;
	}

	/**
	 * Sets the separator factory to use for creating separators between
	 * breadcrumb buttons. Set this value to <code>null</code> to use the
	 * default separator factory.
	 */
	public void setSeparatorFactory(SeparatorFactory separatorFactory) {
		if (separatorFactory == null) {
			separatorFactory = new DefaultSeparatorFactory();
		}
		this.separatorFactory = separatorFactory;
	}

	/**
	 * Returns the button factory to use for creating breadcrumb buttons.
	 */
	public BreadcrumbElementFactory getBreadcrumbElementFactory() {
		return buttonFactory;
	}

	/**
	 * Sets the button factory to use for creating breadcrumb buttons. Set this
	 * value to <code>null</code> to use the default button factory.
	 */
	public void setBreadcrumbElementFactory(BreadcrumbElementFactory buttonFactory) {
		if (buttonFactory == null) {
			buttonFactory = new DefaultBreadcrumbElementFactory();
		}
		this.buttonFactory = buttonFactory;
	}

	
	protected void addBreadcrumbForView(final NavigateableView view) {
		
		final Button btn = getBreadcrumbElementFactory().createButton(view);
		
		btn.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = -9116612359809246223L;

			@Override
			public void buttonClick(ClickEvent event) {
				navigationController.getEventBus().fireEvent(view.getEventToShowThisView());
			}
		});
		
		breadcrumbElementContainer.addComponent(btn);
		breadcrumbElementContainer.setComponentAlignment(btn, Alignment.MIDDLE_LEFT);
	}
	

	
	
	
	protected void addSeparatorForView() {
		Component separator = getSeparatorFactory().createSeparator();
		breadcrumbElementContainer.addComponent(separator);
		breadcrumbElementContainer.setComponentAlignment(separator, Alignment.MIDDLE_LEFT);
	}

	protected void removeBreadcrumbs() {
		breadcrumbElementContainer.removeAllComponents();
		
	}

	@Override
	public void onNavigatedTo(NavigateableView view) {
		removeBreadcrumbs();
		generateBreadcrumb(view);
		
	}
	
	
	private void generateBreadcrumb(NavigateableView view){
		
		NavigateableView v = view;
		List<NavigateableView> viewPath = new LinkedList<NavigateableView>();
		
		while (true){
			
			viewPath.add(v);
			
			if (v instanceof NavigateableSubView)
			{
				v = ((NavigateableSubView) v).getParentView();
			}
			else
				break;
		}
		
		
		for (int i = viewPath.size()-1; i>=0; i--)
		{
			if (i!=viewPath.size()-1)
				addSeparatorForView();
			
			addBreadcrumbForView(viewPath.get(i));
		}
		
		
		
	}
	
}
