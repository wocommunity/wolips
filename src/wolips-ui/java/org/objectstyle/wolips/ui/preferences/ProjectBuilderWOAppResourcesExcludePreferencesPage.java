/*
 * Created on 23.07.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.objectstyle.wolips.ui.preferences;

import org.eclipse.ui.IWorkbench;
import org.objectstyle.wolips.core.plugin.IWOLipsPluginConstants;
import org.objectstyle.wolips.core.preferences.PreferencesMessages;

/**
 * @author ulrich
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ProjectBuilderWOAppResourcesExcludePreferencesPage extends PatternPreferencesPage {
	public void init(IWorkbench workbench) {
		super.init(workbench, PreferencesMessages.getString("ProjectBuilderWOAppResourcesExcludePreferencesPage.description"), IWOLipsPluginConstants.PREF_PBWO_PROJECT_EXCLUDED_WOAPP_RESOURCES);
	}
	
}
