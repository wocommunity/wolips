/*
 * Created on 16.07.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.objectstyle.wolips.ui.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.objectstyle.wolips.core.plugin.IWOLipsPluginConstants;
import org.objectstyle.wolips.core.preferences.Preferences;
import org.objectstyle.wolips.core.preferences.PreferencesMessages;

/**
 * @author ulrich
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ProjectBuilderPreferencesPage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	/**
	 * Constructor
	 */
	public ProjectBuilderPreferencesPage() {
		super(GRID);
		setPreferenceStore(Preferences.getPreferenceStore());
		setDescription(
			PreferencesMessages.getString(
				"Preferences.ProjectBuilder.PageDescription"));
		Preferences.setDefaults();
	}
	/**
		 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
		 */
	public void createFieldEditors() {
		addField(
			new BooleanFieldEditor(
				IWOLipsPluginConstants.PREF_PBWO_PROJECT_UPDATE,
				PreferencesMessages.getString(
					"Preferences.UpdatePBWOProject.Label"),
				getFieldEditorParent()));

	}
	/**
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}
	/**
	 * Method performOK.
	 * @return boolean
	 */
	public boolean performOk() {
		if (super.performOk()) {
			//do some stuff	
			return true;
		}
		return false;
	}
}

