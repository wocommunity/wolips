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
package org.objectstyle.wolips.eomodeler.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.objectstyle.wolips.eomodeler.Activator;
import org.objectstyle.wolips.eomodeler.Messages;
import org.objectstyle.wolips.eomodeler.core.model.EOArgument;
import org.objectstyle.wolips.eomodeler.core.model.EOAttribute;
import org.objectstyle.wolips.eomodeler.core.model.EOEntity;
import org.objectstyle.wolips.eomodeler.core.model.EORelationship;
import org.objectstyle.wolips.eomodeler.utils.TableUtils;

/**
 * @author mschrag
 */
public class EntityModelerPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	public EntityModelerPreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription(Messages.getString("Preferences.PageDescription"));
	}

	@Override
	public void createFieldEditors() {
		addField(new BooleanFieldEditor(PreferenceConstants.SHOW_ERRORS_IN_PROBLEMS_VIEW_KEY, Messages.getString("Preferences.ShowErrorsInProblemsViewLabel"), getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.SHOW_RELATIONSHIP_ATTRIBUTE_OPTIONALITY_MISMATCH, Messages.getString("Preferences.ShowRelationshipAttributeOptionalityMismatchLabel"), getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.OPEN_WINDOW_ON_VERIFICATION_ERRORS_KEY, Messages.getString("Preferences.OpenWindowOnVerificationErrorsLabel"), getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.OPEN_WINDOW_ON_VERIFICATION_WARNINGS_KEY, Messages.getString("Preferences.OpenWindowOnVerificationWarningsLabel"), getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.CHANGE_PERSPECTIVES_KEY, Messages.getString("Preferences.ChangePerspectivesLabel"), getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.OPEN_IN_WINDOW_KEY, Messages.getString("Preferences.OpenInWindowLabel"), getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.USED_FOR_LOCKING_DEFAULT_KEY, Messages.getString("Preferences.DefaultUsedForLockingLabel"), getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.ALLOWS_NULL_DEFAULT_KEY, Messages.getString("Preferences.DefaultAllowsNullLabel"), getFieldEditorParent()));

		addField(new StringFieldEditor(TableUtils.getPreferenceNameForTableNamed(EOEntity.class.getName()), "Entity Columns", getFieldEditorParent()));
		addField(new StringFieldEditor(TableUtils.getPreferenceNameForTableNamed(EOAttribute.class.getName()), "Attribute Columns", getFieldEditorParent()));
		addField(new StringFieldEditor(TableUtils.getPreferenceNameForTableNamed(EORelationship.class.getName()), "Relationship Columns", getFieldEditorParent()));
		addField(new StringFieldEditor(TableUtils.getPreferenceNameForTableNamed(EOArgument.class.getName()), "Argument Columns", getFieldEditorParent()));
	}

	public void init(IWorkbench workbench) {
		// DO NOTHING
	}

}