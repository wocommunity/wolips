package org.objectstyle.wolips.ui.filters;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.ui.internal.misc.StringMatcher;
import org.objectstyle.wolips.WOLipsPlugin;
/**
 * @author uli
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class Filter extends ViewerFilter {
	private String[] patterns;
	private StringMatcher[] matchers;
	static final String COMMA_SEPARATOR = ",";//$NON-NLS-1$

	/**
	 * Creates a new viewer filter.
	 */
	protected Filter() {
	}
	/**
	 * Return the currently configured StringMatchers. If there aren't any look
	 * them up.
	 */
	private StringMatcher[] getMatchers() {

		if (this.matchers == null)
			initializeFromPreferences();

		return this.matchers;
	}
	/**
	 * Gets the patterns for the receiver. Returns the cached values if there
	 * are any - if not look it up.
	 */
	public String[] getPatterns() {

		if (this.patterns == null)
			initializeFromPreferences();

		return this.patterns;

	}
	
	public String storedPatternsTag() {
		return null;
	}
	
	/**
	 * Initializes the filters from the preference store.
	 */
	private void initializeFromPreferences() {
		WOLipsPlugin plugin = WOLipsPlugin.getDefault();
		// get the filters that were saved by ResourceNavigator.setFiltersPreference
		String storedPatterns = plugin.getPreferenceStore().getString(this.storedPatternsTag());

		//Get the strings separated by a comma and filter them from the currently
		//defined ones
		StringTokenizer entries = new StringTokenizer(storedPatterns, COMMA_SEPARATOR);
		List patterns = new ArrayList();

		while (entries.hasMoreElements()) {
			String nextToken = entries.nextToken();
			patterns.add(nextToken);
		}

		//Convert to an array of Strings
		String[] patternArray = new String[patterns.size()];
		patterns.toArray(patternArray);
		setPatterns(patternArray);

	}
	/* (non-Javadoc)
	 * Method declared on ViewerFilter.
	 */
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		IResource resource = null;
		if (element instanceof IResource) {
			resource = (IResource) element;
		} else
			if (element instanceof IAdaptable) {
				IAdaptable adaptable = (IAdaptable) element;
				resource = (IResource) adaptable.getAdapter(IResource.class);
			}
		if (resource != null) {
			String name = resource.getName();
			StringMatcher[] testMatchers = getMatchers();
			for (int i = 0; i < testMatchers.length; i++) {
				if (testMatchers[i].match(name))
					return false;
			}
			return true;
		}
		return true;
	}
	/**
	 * Sets the patterns to filter out for the receiver.
	 */
	public void setPatterns(String[] newPatterns) {

		this.patterns = newPatterns;
		this.matchers = new StringMatcher[newPatterns.length];
		for (int i = 0; i < newPatterns.length; i++) {
			//Reset the matchers to prevent constructor overhead
			matchers[i] = new StringMatcher(newPatterns[i], true, false);
		}
	}
}
