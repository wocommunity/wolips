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

public class PreferenceConstants {
	public static final String VALIDATE_BINDING_VALUES = "CheckBindingValues";

	public static final String MISSING_COLLECTION_SEVERITY_KEY = "MissingCollectionSeverityKey";

	public static final String MISSING_COMPONENT_SEVERITY_KEY = "MissingComponentSeverityKey";

	public static final String MISSING_NSKVC_SEVERITY_KEY = "MissingNSKVCSeverityKey";

	public static final String INVALID_OGNL_SEVERITY_KEY = "InvalidOGNLSeverityKey";

	public static final String AMBIGUOUS_SEVERITY_KEY = "AmbiguousSeverityKey";

	public static final String HTML_ERRORS_SEVERITY_KEY = "HtmlErrorsSeverityKey";

	public static final String WOD_API_PROBLEMS_SEVERITY_KEY = "WodApiProblemsSeverityKey";
	
	public static final String DEPRECATED_BINDING_SEVERITY_KEY = "DeprecatedBindingSeverityKey";

	public static final String WOD_MISSING_COMPONENT_SEVERITY_KEY = "WodProblemsSeverityKey";

	public static final String WOD_ERRORS_IN_HTML_SEVERITY_KEY = "WodErrorsInHtmlSeverityKey";

	public static final String UNUSED_WOD_ELEMENT_SEVERITY_KEY = "UnusedWodElementSeverityKey";

	public static final String AT_OPERATOR_SEVERITY_KEY = "AtOperatorSeverityKey";

	public static final String HELPER_FUNCTION_SEVERITY_KEY = "HelperFunctionSeverityKey";

	public static final String VALIDATE_TEMPLATES_KEY = "ValidateTemplatesKey";

	public static final String VALIDATE_TEMPLATES_ON_BUILD_KEY = "ValidateTemplatesOnBuildKey";

	public static final String VALIDATE_WOO_ENCODINGS_KEY = "ValidateWooEncodingsKey";

	public static final String TAG_SHORTCUTS_KEY = "TagShortcuts";

	public static final String BINDING_VALIDATION_RULES_KEY = "BindingValidationRules";

	// public static final String WO54_KEY = "WO 5.4";

	public static final String USE_INLINE_BINDINGS_KEY = "UseInlineBindings";

	public static final String THREADED_VALIDATION_KEY = "ThreadedValidation";

	public static final String WELL_FORMED_TEMPLATE_KEY = "WellFormedTemplate";

	public static final String ERROR = "error";

	public static final String WARNING = "warning";

	public static final String IGNORE = "ignore";

	public static final String DEFAULT = "default";

	public static final String NO = "no";

	public static final String YES = "yes";

	public static String[][] IGNORE_WARNING_ERROR = new String[][] { new String[] { "Ignore", PreferenceConstants.IGNORE }, new String[] { "Warning", PreferenceConstants.WARNING }, new String[] { "Error", PreferenceConstants.ERROR } };
	
	public static String[][] DEFAULT_YES_NO = new String[][] { new String[] { "Default", PreferenceConstants.DEFAULT }, new String[] { "Yes", PreferenceConstants.YES }, new String[] { "No", PreferenceConstants.NO } };
}
