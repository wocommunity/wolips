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

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.objectstyle.wolips.eomodeler.Activator;
import org.objectstyle.wolips.eomodeler.core.model.AbstractEOArgument;
import org.objectstyle.wolips.eomodeler.core.model.EOArgument;
import org.objectstyle.wolips.eomodeler.core.model.EOAttribute;
import org.objectstyle.wolips.eomodeler.core.model.EOEntity;
import org.objectstyle.wolips.eomodeler.core.model.EOFetchSpecification;
import org.objectstyle.wolips.eomodeler.core.model.EOJoin;
import org.objectstyle.wolips.eomodeler.core.model.EORelationship;
import org.objectstyle.wolips.eomodeler.core.model.EOSortOrdering;
import org.objectstyle.wolips.eomodeler.utils.TableUtils;

import ch.rucotec.wolips.eomodeler.core.model.AbstractDiagram;

/**
 * @author mschrag
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {
  @Override
  public void initializeDefaultPreferences() {
    IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
    prefs.setDefault(PreferenceConstants.ALLOWS_NULL_DEFAULT_KEY, false);
    prefs.setDefault(PreferenceConstants.USED_FOR_LOCKING_DEFAULT_KEY, true);
    prefs.setDefault(PreferenceConstants.CHANGE_PERSPECTIVES_KEY, true);
    prefs.setDefault(PreferenceConstants.OPEN_IN_WINDOW_KEY, true);
    prefs.setDefault(PreferenceConstants.SHOW_RELATIONSHIP_ATTRIBUTE_OPTIONALITY_MISMATCH, false);
    prefs.setDefault(PreferenceConstants.SHOW_ERRORS_IN_PROBLEMS_VIEW_KEY, true);
    prefs.setDefault(PreferenceConstants.OPEN_WINDOW_ON_VERIFICATION_ERRORS_KEY, true);
    prefs.setDefault(PreferenceConstants.OPEN_WINDOW_ON_VERIFICATION_WARNINGS_KEY, true);
    TableUtils.setColumnsForTableNamed(EOArgument.class.getName(), new String[] { AbstractEOArgument.NAME, AbstractEOArgument.COLUMN_NAME, EOArgument.DIRECTION }, false);
    TableUtils.setColumnsForTableNamed(EOAttribute.class.getName(), new String[] { EOAttribute.PRIMARY_KEY, EOAttribute.CLASS_PROPERTY, EOAttribute.USED_FOR_LOCKING, AbstractEOArgument.ALLOWS_NULL, EOAttribute.PROTOTYPE, AbstractEOArgument.NAME, AbstractEOArgument.COLUMN_NAME, AbstractEOArgument.WIDTH, AbstractEOArgument.PRECISION, AbstractEOArgument.SCALE }, false);
    TableUtils.setColumnsForTableNamed(EOEntity.class.getName(), new String[] { EOEntity.NAME, EOEntity.EXTERNAL_NAME, EOEntity.CLASS_NAME, EOEntity.PARENT }, false);
    TableUtils.setColumnsForTableNamed(EOFetchSpecification.class.getName(), new String[] { EOFetchSpecification.SHARES_OBJECTS, EOFetchSpecification.NAME }, false);
    TableUtils.setColumnsForTableNamed(EOSortOrdering.class.getName(), new String[] { EOSortOrdering.KEY, EOSortOrdering.ASCENDING, EOSortOrdering.CASE_INSENSITIVE }, false);
    TableUtils.setColumnsForTableNamed(EOJoin.class.getName(), new String[] { EOJoin.SOURCE_ATTRIBUTE_NAME, EOJoin.DESTINATION_ATTRIBUTE_NAME }, false);
    TableUtils.setColumnsForTableNamed(EORelationship.class.getName(), new String[] { EORelationship.TO_MANY, EORelationship.CLASS_PROPERTY, EORelationship.OPTIONAL, EORelationship.NAME, EORelationship.DESTINATION, EOJoin.SOURCE_ATTRIBUTE, EOJoin.DESTINATION_ATTRIBUTE }, false);
    // SAVAS tableViewer ColumnNames
    TableUtils.setColumnsForTableNamed(AbstractDiagram.class.getName(), new String[] { AbstractDiagram.NAME, AbstractDiagram.ENTITYNAMES}, false);
  }
}
