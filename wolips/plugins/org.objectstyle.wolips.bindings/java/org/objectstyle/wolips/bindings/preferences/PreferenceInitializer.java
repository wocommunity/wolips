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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.objectstyle.wolips.bindings.Activator;
import org.objectstyle.wolips.bindings.wod.BindingValidationRule;
import org.objectstyle.wolips.bindings.wod.TagShortcut;

public class PreferenceInitializer extends AbstractPreferenceInitializer {
  @Override
  public void initializeDefaultPreferences() {
    IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();

    List<TagShortcut> tagShortcuts = new ArrayList<TagShortcut>();
    tagShortcuts.add(new TagShortcut("localized", "ERXLocalizedString")); // not in 5.4
    tagShortcuts.add(new TagShortcut("not", "WOConditional"));
    tagShortcuts.add(new TagShortcut("else", "ERXElse"));
    tagShortcuts.add(new TagShortcut("if", "WOConditional"));
    tagShortcuts.add(new TagShortcut("conditional", "WOConditional"));
    tagShortcuts.add(new TagShortcut("condition", "WOConditional")); // not in 5.4
    tagShortcuts.add(new TagShortcut("foreach", "WORepetition"));
    tagShortcuts.add(new TagShortcut("repeat", "WORepetition"));
    tagShortcuts.add(new TagShortcut("repetition", "WORepetition"));
    tagShortcuts.add(new TagShortcut("loop", "WORepetition")); // not in 5.4
    tagShortcuts.add(new TagShortcut("content", "WOComponentContent"));
    tagShortcuts.add(new TagShortcut("componentContent", "WOComponentContent"));
    tagShortcuts.add(new TagShortcut("str", "WOString")); // not in 5.4
    tagShortcuts.add(new TagShortcut("string", "WOString"));
    tagShortcuts.add(new TagShortcut("switchComponent", "WOSwitchComponent"));
    tagShortcuts.add(new TagShortcut("switch", "WOSwitchComponent"));
    tagShortcuts.add(new TagShortcut("XMLNode", "WOXMLNode"));
    tagShortcuts.add(new TagShortcut("nestedList", "WONestedList"));
    tagShortcuts.add(new TagShortcut("param", "WOParam"));
    tagShortcuts.add(new TagShortcut("applet", "WOApplet"));
    tagShortcuts.add(new TagShortcut("quickTime", "WOQuickTime"));
    tagShortcuts.add(new TagShortcut("commentString", "WOHTMLCommentString"));
    tagShortcuts.add(new TagShortcut("comment", "WOHTMLCommentString"));
    tagShortcuts.add(new TagShortcut("noContentElement", "WONoContentElement"));
    tagShortcuts.add(new TagShortcut("noContent", "WONoContentElement"));
    tagShortcuts.add(new TagShortcut("body", "WOBody"));
    tagShortcuts.add(new TagShortcut("embeddedObject", "WOEmbeddedObject"));
    tagShortcuts.add(new TagShortcut("embedded", "WOEmbeddedObject"));
    tagShortcuts.add(new TagShortcut("frame", "WOFrame"));
    tagShortcuts.add(new TagShortcut("image", "WOImage"));
    tagShortcuts.add(new TagShortcut("img", "WOImage")); // not in 5.4
    tagShortcuts.add(new TagShortcut("form", "WOForm"));
    tagShortcuts.add(new TagShortcut("javaScript", "WOJavaScript"));
    tagShortcuts.add(new TagShortcut("VBScript", "WOVBScript"));
    tagShortcuts.add(new TagShortcut("resourceURL", "WOResourceURL"));
    tagShortcuts.add(new TagShortcut("genericElement", "WOGenericElement"));
    tagShortcuts.add(new TagShortcut("element", "WOGenericElement"));
    tagShortcuts.add(new TagShortcut("genericContainer", "WOGenericContainer"));
    tagShortcuts.add(new TagShortcut("container", "WOGenericContainer"));
    tagShortcuts.add(new TagShortcut("activeImage", "WOActiveImage"));
    tagShortcuts.add(new TagShortcut("checkBox", "WOCheckBox"));
    tagShortcuts.add(new TagShortcut("checkbox", "WOCheckBox")); // not in 5.4 (5.4 is case insensitive)
    tagShortcuts.add(new TagShortcut("fileUpload", "WOFileUpload"));
    tagShortcuts.add(new TagShortcut("upload", "WOFileUpload"));
    tagShortcuts.add(new TagShortcut("hiddenField", "WOHiddenField"));
    tagShortcuts.add(new TagShortcut("hidden", "WOHiddenField")); // not in 5.4
    tagShortcuts.add(new TagShortcut("imageButton", "WOImageButton"));
    tagShortcuts.add(new TagShortcut("inputList", "WOInputList"));
    tagShortcuts.add(new TagShortcut("browser", "WOBrowser"));
    tagShortcuts.add(new TagShortcut("checkBoxList", "WOCheckBoxList"));
    tagShortcuts.add(new TagShortcut("popUpButton", "WOPopUpButton"));
    tagShortcuts.add(new TagShortcut("select", "WOPopUpButton")); // not in 5.4
    tagShortcuts.add(new TagShortcut("radioButtonList", "WORadioButtonList"));
    tagShortcuts.add(new TagShortcut("passwordField", "WOPasswordField"));
    tagShortcuts.add(new TagShortcut("password", "WOPasswordField"));
    tagShortcuts.add(new TagShortcut("radioButton", "WORadioButton"));
    tagShortcuts.add(new TagShortcut("radio", "WORadioButton"));
    tagShortcuts.add(new TagShortcut("resetButton", "WOResetButton"));
    tagShortcuts.add(new TagShortcut("reset", "WOResetButton"));
    tagShortcuts.add(new TagShortcut("submitButton", "WOSubmitButton"));
    tagShortcuts.add(new TagShortcut("submit", "WOSubmitButton"));
    tagShortcuts.add(new TagShortcut("text", "WOText"));
    tagShortcuts.add(new TagShortcut("textField", "WOTextField"));
    tagShortcuts.add(new TagShortcut("textfield", "WOTextField")); // not in 5.4 (5.4 is case insensitive)
    tagShortcuts.add(new TagShortcut("search", "WOSearchField"));
    tagShortcuts.add(new TagShortcut("searchfield", "WOSearchField"));
    tagShortcuts.add(new TagShortcut("hyperlink", "WOHyperlink"));
    tagShortcuts.add(new TagShortcut("link", "WOHyperlink"));
    tagShortcuts.add(new TagShortcut("actionURL", "WOActionURL"));
    prefs.setDefault(PreferenceConstants.TAG_SHORTCUTS_KEY, TagShortcut.toPreferenceString(tagShortcuts));

    List<BindingValidationRule> validationRules = new ArrayList<BindingValidationRule>();
    validationRules.add(new BindingValidationRule(".*", "^session\\.localizer\\..*"));
    validationRules.add(new BindingValidationRule(".*", "^d2wContext\\..*"));
    validationRules.add(new BindingValidationRule(".*", "^localContext\\..*"));
    validationRules.add(new BindingValidationRule(".*", "^localizer\\..*"));
    validationRules.add(new BindingValidationRule(".*", "^nonCachingContext\\..*"));
    prefs.setDefault(PreferenceConstants.BINDING_VALIDATION_RULES_KEY, BindingValidationRule.toPreferenceString(validationRules));

    prefs.setDefault(PreferenceConstants.USE_INLINE_BINDINGS_KEY, false);

    prefs.setDefault(PreferenceConstants.VALIDATE_TEMPLATES_KEY, true);
    prefs.setDefault(PreferenceConstants.VALIDATE_TEMPLATES_ON_BUILD_KEY, true);
    prefs.setDefault(PreferenceConstants.VALIDATE_BINDING_VALUES, true);
    prefs.setDefault(PreferenceConstants.VALIDATE_WOO_ENCODINGS_KEY, true);

    prefs.setDefault(PreferenceConstants.INVALID_OGNL_SEVERITY_KEY, PreferenceConstants.WARNING);
    prefs.setDefault(PreferenceConstants.MISSING_COLLECTION_SEVERITY_KEY, PreferenceConstants.WARNING);
    prefs.setDefault(PreferenceConstants.MISSING_COMPONENT_SEVERITY_KEY, PreferenceConstants.ERROR);
    prefs.setDefault(PreferenceConstants.MISSING_NSKVC_SEVERITY_KEY, PreferenceConstants.ERROR);
    prefs.setDefault(PreferenceConstants.AMBIGUOUS_SEVERITY_KEY, PreferenceConstants.WARNING);
    prefs.setDefault(PreferenceConstants.HTML_ERRORS_SEVERITY_KEY, PreferenceConstants.ERROR);
    prefs.setDefault(PreferenceConstants.WOD_ERRORS_IN_HTML_SEVERITY_KEY, PreferenceConstants.ERROR);
    prefs.setDefault(PreferenceConstants.UNUSED_WOD_ELEMENT_SEVERITY_KEY, PreferenceConstants.WARNING);
    prefs.setDefault(PreferenceConstants.WOD_MISSING_COMPONENT_SEVERITY_KEY, PreferenceConstants.ERROR);
    prefs.setDefault(PreferenceConstants.WOD_API_PROBLEMS_SEVERITY_KEY, PreferenceConstants.ERROR);
    prefs.setDefault(PreferenceConstants.AT_OPERATOR_SEVERITY_KEY, PreferenceConstants.WARNING);
    prefs.setDefault(PreferenceConstants.HELPER_FUNCTION_SEVERITY_KEY, PreferenceConstants.WARNING);
    prefs.setDefault(PreferenceConstants.WELL_FORMED_TEMPLATE_KEY, PreferenceConstants.DEFAULT);

    prefs.setDefault(PreferenceConstants.THREADED_VALIDATION_KEY, true);
  }
}
