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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.objectstyle.wolips.wodclipse.core.Activator;

/**
 * @author mike
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {
  @Override
  public void initializeDefaultPreferences() {
    IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
    prefs.setDefault(PreferenceConstants.ELEMENT_NAME, "65,0,197");
    prefs.setDefault(PreferenceConstants.ELEMENT_TYPE, "63,127,95");
    prefs.setDefault(PreferenceConstants.BINDING_NAME, "138,23,100");
    prefs.setDefault(PreferenceConstants.BINDING_VALUE, "0,65,216");
    prefs.setDefault(PreferenceConstants.CONSTANT_BINDING_VALUE, "42,0,255");
    prefs.setDefault(PreferenceConstants.OPERATOR, "0,0,0");
    prefs.setDefault(PreferenceConstants.COMMENT, "63,127,95");
    prefs.setDefault(PreferenceConstants.UNKNOWN, "0,0,0");
    prefs.setDefault(PreferenceConstants.ALLOWED_BINDING_CHARACTERS, ".^-@,|()");
    prefs.setDefault(PreferenceConstants.VALIDATE_TEMPLATES_KEY, true);
    prefs.setDefault(PreferenceConstants.VALIDATE_BINDING_VALUES, true);
    prefs.setDefault(PreferenceConstants.VALIDATE_OGNL_KEY, true);
    prefs.setDefault(PreferenceConstants.AUTO_INSERT_ON_COMPLETION, true);
    prefs.setDefault(PreferenceConstants.WARN_ON_MISSING_COLLECTION_KEY, true);
    prefs.setDefault(PreferenceConstants.ERROR_ON_MISSING_NSKVC_KEY, true);
    prefs.setDefault(PreferenceConstants.WARN_ON_MISSING_NSKVC_KEY, false);
    prefs.setDefault(PreferenceConstants.WARN_ON_AMBIGUOUS_KEY, true);
    prefs.setDefault(PreferenceConstants.ERROR_ON_HTML_ERRORS_KEY, true);
    prefs.setDefault(PreferenceConstants.WARN_ON_OPERATOR_KEY, true);
    prefs.setDefault(PreferenceConstants.WARN_ON_HELPER_FUNCTION_KEY, true);

    List<TagShortcut> tagShortcuts = new ArrayList<TagShortcut>();
    tagShortcuts.add(new TagShortcut("string", "WOString", new HashMap<String, String>()));
    tagShortcuts.add(new TagShortcut("str", "WOString", new HashMap<String, String>()));
    tagShortcuts.add(new TagShortcut("else", "ERXElse", new HashMap<String, String>()));
    tagShortcuts.add(new TagShortcut("if", "WOConditional", new HashMap<String, String>()));
    tagShortcuts.add(new TagShortcut("not", "WOConditional", new HashMap<String, String>()));
    tagShortcuts.add(new TagShortcut("condition", "WOConditional", new HashMap<String, String>()));
    tagShortcuts.add(new TagShortcut("conditional", "WOConditional", new HashMap<String, String>()));
    tagShortcuts.add(new TagShortcut("link", "WOHyperlink", new HashMap<String, String>()));
    tagShortcuts.add(new TagShortcut("loop", "WORepetition", new HashMap<String, String>()));
    tagShortcuts.add(new TagShortcut("textfield", "WOTextField", new HashMap<String, String>()));
    tagShortcuts.add(new TagShortcut("checkbox", "WOCheckBox", new HashMap<String, String>()));
    tagShortcuts.add(new TagShortcut("hidden", "WOHiddenField", new HashMap<String, String>()));
    tagShortcuts.add(new TagShortcut("select", "WOPopUpButton", new HashMap<String, String>()));
    tagShortcuts.add(new TagShortcut("radio", "WORadioButton", new HashMap<String, String>()));
    tagShortcuts.add(new TagShortcut("password", "WOPasswordField", new HashMap<String, String>()));
    tagShortcuts.add(new TagShortcut("upload", "WOFileUpload", new HashMap<String, String>()));
    tagShortcuts.add(new TagShortcut("text", "WOText", new HashMap<String, String>()));
    tagShortcuts.add(new TagShortcut("form", "WOForm", new HashMap<String, String>()));
    tagShortcuts.add(new TagShortcut("submit", "WOSubmitButton", new HashMap<String, String>()));
    tagShortcuts.add(new TagShortcut("localized", "ERXLocalizedString", new HashMap<String, String>()));
    prefs.setDefault(PreferenceConstants.TAG_SHORTCUTS_KEY, TagShortcut.toPreferenceString(tagShortcuts));

    List<BindingValidationRule> validationRules = new ArrayList<BindingValidationRule>();
    validationRules.add(new BindingValidationRule(".*", "^session\\.localized.*"));
    validationRules.add(new BindingValidationRule(".*", "^d2wContext\\..*"));
    validationRules.add(new BindingValidationRule(".*", "^localContext\\..*"));
    prefs.setDefault(PreferenceConstants.BINDING_VALIDATION_RULES_KEY, BindingValidationRule.toPreferenceString(validationRules));
  }
}
