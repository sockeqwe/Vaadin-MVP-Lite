package com.mvplite.view;

/**
 * The {@link NavigateableSubView} is a sub view of a {@link NavigateableView}.
 * That means, that this view has a parent {@link NavigateableView} and you 
 * can implement a tree of views, since a {@link NavigateableSubView} can also be
 * a parent of another {@link NavigateableSubView}
 * @author Hannes Dorfmann
 *
 */
public interface NavigateableSubView extends NavigateableView {

	
	/**
	 * Get the parent of this {@link NavigateableSubView}.
	 * Note that a {@link NavigateableSubView} can be a parent of a {@link NavigateableSubView}
	 * @return The parent {@link NavigateableView}
	 */
	public NavigateableView getParentView();
}
