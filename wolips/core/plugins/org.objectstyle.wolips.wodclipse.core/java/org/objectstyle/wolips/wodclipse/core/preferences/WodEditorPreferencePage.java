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
package org.objectstyle.wolips.wodclipse.core.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.objectstyle.wolips.wodclipse.core.Activator;

/**
 * @author mike
 */
public class WodEditorPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
  public WodEditorPreferencePage() {
    super(GRID);
    setPreferenceStore(Activator.getDefault().getPreferenceStore());
    setDescription("WOD Editor Preferences");
  }

  @Override
  public void createFieldEditors() {
    addField(new ColorFieldEditor(PreferenceConstants.ELEMENT_NAME, "Element Name Color", getFieldEditorParent()));
    addField(new ColorFieldEditor(PreferenceConstants.ELEMENT_TYPE, "Element Type Color", getFieldEditorParent()));
    addField(new ColorFieldEditor(PreferenceConstants.BINDING_NAME, "Binding Name Color", getFieldEditorParent()));
    addField(new ColorFieldEditor(PreferenceConstants.BINDING_VALUE, "Binding Value Color", getFieldEditorParent()));
    addField(new ColorFieldEditor(PreferenceConstants.CONSTANT_BINDING_VALUE, "Constant Binding Value Color", getFieldEditorParent()));
    addField(new ColorFieldEditor(PreferenceConstants.OGNL_BINDING_VALUE, "OGNL Binding Value Color", getFieldEditorParent()));
    addField(new ColorFieldEditor(PreferenceConstants.OPERATOR, "Operator Color", getFieldEditorParent()));
    addField(new ColorFieldEditor(PreferenceConstants.COMMENT, "Comment Color", getFieldEditorParent()));
    addField(new ColorFieldEditor(PreferenceConstants.UNKNOWN, "Unknown Color", getFieldEditorParent()));
    addField(new BooleanFieldEditor(PreferenceConstants.AUTO_INSERT_ON_COMPLETION, "Auto-Insert {'s, :'s, and ='s", getFieldEditorParent()));
    // AK: this actually belongs to the build prefs section, but this
    // resides in another framework and isn't used anywhere else...
    addField(new StringFieldEditor(PreferenceConstants.ALLOWED_BINDING_CHARACTERS, "Allowed Binding Characters", 15, getFieldEditorParent()));
    addField(new BooleanFieldEditor(PreferenceConstants.ERROR_ON_HTML_ERRORS_KEY, "Show HTML Template Errors", getFieldEditorParent()));
    addField(new BooleanFieldEditor(PreferenceConstants.VALIDATE_TEMPLATES_KEY, "Validate WOD / Inline Bindings", getFieldEditorParent()));
    addField(new BooleanFieldEditor(PreferenceConstants.VALIDATE_BINDING_VALUES, "Validate Binding Values", getFieldEditorParent()));
    addField(new BooleanFieldEditor(PreferenceConstants.WARN_ON_MISSING_COLLECTION_KEY, "... Warn if Missing Key on NSDictionary/NSArray", getFieldEditorParent()));
    addField(new BooleanFieldEditor(PreferenceConstants.ERROR_ON_MISSING_NSKVC_KEY, "... Error if Missing Key on 'implements NSKeyValueCoding'", getFieldEditorParent()));
    addField(new BooleanFieldEditor(PreferenceConstants.WARN_ON_MISSING_NSKVC_KEY, "... Warn if Missing Key on 'implements NSKeyValueCoding'", getFieldEditorParent()));
    addField(new BooleanFieldEditor(PreferenceConstants.WARN_ON_AMBIGUOUS_KEY, "... Warn on Ambiguous Key Paths", getFieldEditorParent()));
    addField(new BooleanFieldEditor(PreferenceConstants.WARN_ON_OPERATOR_KEY, "... Warn on @Operator", getFieldEditorParent()));
    addField(new BooleanFieldEditor(PreferenceConstants.WARN_ON_HELPER_FUNCTION_KEY, "... Warn on Helper Functions", getFieldEditorParent()));
    addField(new BooleanFieldEditor(PreferenceConstants.VALIDATE_OGNL_KEY, "... Validate OGNL", getFieldEditorParent()));
    addField(new BooleanFieldEditor(PreferenceConstants.WO54_KEY, "WO 5.4", getFieldEditorParent()));
  }

  public void init(IWorkbench workbench) {
  }

}