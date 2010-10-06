/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2005 The ObjectStyle Group and individual authors of the
 * software. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 1.
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. The end-user documentation
 * included with the redistribution, if any, must include the following
 * acknowlegement: "This product includes software developed by the ObjectStyle
 * Group (http://objectstyle.org/)." Alternately, this acknowlegement may
 * appear in the software itself, if and wherever such third-party
 * acknowlegements normally appear. 4. The names "ObjectStyle Group" and
 * "Cayenne" must not be used to endorse or promote products derived from this
 * software without prior written permission. For written permission, please
 * contact andrus@objectstyle.org. 5. Products derived from this software may
 * not be called "ObjectStyle" nor may "ObjectStyle" appear in their names
 * without prior written permission of the ObjectStyle Group.
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
 * Group, please see <http://objectstyle.org/> .
 *  
 */
package org.objectstyle.wolips.bindings.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.objectstyle.wolips.bindings.Activator;

/**
 * @author mike
 */
public class BindingValidationPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	
	public BindingValidationPreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("The following settings control various aspects of the component validation system.");
	}

	@Override
	public void createFieldEditors() {
		addField(new BooleanFieldEditor(PreferenceConstants.VALIDATE_TEMPLATES_KEY, "Component Validation", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.VALIDATE_TEMPLATES_ON_BUILD_KEY, "Validate on Build", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.USE_INLINE_BINDINGS_KEY, "Inline Bindings Allowed", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.VALIDATE_BINDING_VALUES, "Binding Value Validation (Slow)", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.VALIDATE_WOO_ENCODINGS_KEY, "WOO Encoding Validation", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.THREADED_VALIDATION_KEY, "Threaded Validation", getFieldEditorParent()));

		addField(new ComboFieldEditor(PreferenceConstants.HTML_ERRORS_SEVERITY_KEY, "Invalid HTML", PreferenceConstants.IGNORE_WARNING_ERROR, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.WOD_MISSING_COMPONENT_SEVERITY_KEY, "Missing Component", PreferenceConstants.IGNORE_WARNING_ERROR, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.WOD_API_PROBLEMS_SEVERITY_KEY, "WOD API Problems", PreferenceConstants.IGNORE_WARNING_ERROR, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.UNUSED_WOD_ELEMENT_SEVERITY_KEY, "Unused WOD Elements", PreferenceConstants.IGNORE_WARNING_ERROR, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.WOD_ERRORS_IN_HTML_SEVERITY_KEY, "WOD Errors in Template", PreferenceConstants.IGNORE_WARNING_ERROR, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.MISSING_COLLECTION_SEVERITY_KEY, "Missing Key on NSDictionary/NSArray", PreferenceConstants.IGNORE_WARNING_ERROR, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.MISSING_COMPONENT_SEVERITY_KEY, "Missing Key on 'extends WOComponent'", PreferenceConstants.IGNORE_WARNING_ERROR, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.MISSING_NSKVC_SEVERITY_KEY, "Missing Key on 'implements NSKeyValueCoding'", PreferenceConstants.IGNORE_WARNING_ERROR, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.AMBIGUOUS_SEVERITY_KEY, "Ambiguous Key Paths", PreferenceConstants.IGNORE_WARNING_ERROR, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.AT_OPERATOR_SEVERITY_KEY, "@Operator KVC Paths", PreferenceConstants.IGNORE_WARNING_ERROR, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.HELPER_FUNCTION_SEVERITY_KEY, "Helper Functions", PreferenceConstants.IGNORE_WARNING_ERROR, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.INVALID_OGNL_SEVERITY_KEY, "Invalid OGNL", PreferenceConstants.IGNORE_WARNING_ERROR, getFieldEditorParent()));

		addField(new ComboFieldEditor(PreferenceConstants.WELL_FORMED_TEMPLATE_KEY, "Require well-formed HTML Template", PreferenceConstants.DEFAULT_YES_NO, getFieldEditorParent()));
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		super.propertyChange(event);
	}

	public void init(IWorkbench workbench) {
		// DO NOTHING
	}
}