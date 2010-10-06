/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2002 - 2006 The ObjectStyle Group and individual authors of the
 * software. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * 3. The end-user documentation included with the redistribution, if any, must
 * include the following acknowlegement: "This product includes software
 * developed by the ObjectStyle Group (http://objectstyle.org/)." Alternately,
 * this acknowlegement may appear in the software itself, if and wherever such
 * third-party acknowlegements normally appear.
 * 
 * 4. The names "ObjectStyle Group" and "Cayenne" must not be used to endorse
 * or promote products derived from this software without prior written
 * permission. For written permission, please contact andrus@objectstyle.org.
 * 
 * 5. Products derived from this software may not be called "ObjectStyle" nor
 * may "ObjectStyle" appear in their names without prior written permission of
 * the ObjectStyle Group.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * OBJECTSTYLE GROUP OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many individuals
 * on behalf of the ObjectStyle Group. For more information on the ObjectStyle
 * Group, please see <http://objectstyle.org/>.
 *  
 */
package org.objectstyle.wolips.ui.preferences;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.objectstyle.woenvironment.env.WOVariables;
import org.objectstyle.wolips.preferences.Preferences;
import org.objectstyle.wolips.variables.VariablesPlugin;

public class WOLipsPropertiesPreferencesPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	private StringFieldEditor _wolipsPropertiesFieldEditor;
	
	public WOLipsPropertiesPreferencesPage() {
		super(FieldEditorPreferencePage.GRID);
		setDescription("The following folders define the locations of your frameworks, javadoc, and install locations that are required to successfully build WebObjects applications with WOLips.");
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getSource() instanceof FieldEditor && Preferences.PREF_WOLIPS_PROPERTIES_FILE.equals(((FieldEditor) event.getSource()).getPreferenceName())) {
			setPreferenceStore(doGetPreferenceStore());
			initialize();
			checkState();
		}
 		super.propertyChange(event);
	}
	
	@Override
	protected IPreferenceStore doGetPreferenceStore() {
		String wolipsPropertiesName = _wolipsPropertiesFieldEditor.getStringValue();
		if (wolipsPropertiesName == null || wolipsPropertiesName.length() == 0) {
			wolipsPropertiesName = Preferences.getPreferenceStore().getString(Preferences.PREF_WOLIPS_PROPERTIES_FILE);
		}
		IPreferenceStore preferenceStore = VariablesPlugin.getDefault().getGlobalVariables(wolipsPropertiesName);
		preferenceStore.setValue("wolips.properties", wolipsPropertiesName);
		return preferenceStore;
	}

	public void createFieldEditors() {
		int widthInChars = 50;
		
		// MS: WOLips Properties is set in a different preferences store, so we want to hijack this one field editor to use the primary preferences store
		_wolipsPropertiesFieldEditor = new StringFieldEditor(Preferences.PREF_WOLIPS_PROPERTIES_FILE, "WOLips Properties File", widthInChars, StringFieldEditor.VALIDATE_ON_FOCUS_LOST, getFieldEditorParent()) {
			@Override
			public void setPreferenceStore(IPreferenceStore store) {
				super.setPreferenceStore(store == null ? null : Preferences.getPreferenceStore());
			}
			
			public void loadDefault() {
				super.loadDefault();
				// MS: I have no idea why this isn't resetting ...
				getTextControl().setText(getPreferenceStore().getDefaultString(getPreferenceName()));
			}
		};
		addField(_wolipsPropertiesFieldEditor);

		addField(new WOLipsDirectoryFieldEditor(WOVariables.NETWORK_FRAMEWORKS, "Network Frameworks", widthInChars, getFieldEditorParent()));
		addField(new WOLipsDirectoryFieldEditor(WOVariables.SYSTEM_FRAMEWORKS, "System Frameworks", widthInChars, getFieldEditorParent()));
		addField(new WOLipsDirectoryFieldEditor(WOVariables.LOCAL_FRAMEWORKS, "Local Frameworks", widthInChars, getFieldEditorParent()));
		addField(new WOLipsDirectoryFieldEditor(WOVariables.USER_FRAMEWORKS, "User Frameworks", widthInChars, getFieldEditorParent()));

		addField(new WOLipsDirectoryFieldEditor(WOVariables.NETWORK_ROOT, "Network Root", widthInChars, getFieldEditorParent()));
		addField(new WOLipsDirectoryFieldEditor(WOVariables.SYSTEM_ROOT, "System Root", widthInChars, getFieldEditorParent()));
		addField(new WOLipsDirectoryFieldEditor(WOVariables.LOCAL_ROOT, "Local Root", widthInChars, getFieldEditorParent()));
		addField(new WOLipsDirectoryFieldEditor(WOVariables.USER_ROOT, "User Root", widthInChars, getFieldEditorParent()));

		addField(new WOLipsDirectoryFieldEditor(WOVariables.WEBOBJECTS_EXTENSIONS, "WebObjects Extensions", widthInChars, getFieldEditorParent()));

		addField(new WOLipsDirectoryFieldEditor(WOVariables.APPS_ROOT, "Installed Applications", widthInChars, getFieldEditorParent()));

		addField(new WOLipsDirectoryFieldEditor(WOVariables.API_ROOT_KEY, "WebObjects Javadoc", widthInChars, getFieldEditorParent()));
	}
	
	@Override
	public boolean isValid() {
		return true;
	}
    
	@Override
	public boolean okToLeave() {
		return true;
	}
	
	public void init(IWorkbench workbench) {
		// DO NOTHING
	}
}