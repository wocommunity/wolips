/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2006 The ObjectStyle Group,
 * and individual authors of the software.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        ObjectStyle Group (http://objectstyle.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "ObjectStyle Group" and "Cayenne"
 *    must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact andrus@objectstyle.org.
 *
 * 5. Products derived from this software may not be called "ObjectStyle"
 *    nor may "ObjectStyle" appear in their names without prior written
 *    permission of the ObjectStyle Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE OBJECTSTYLE GROUP OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the ObjectStyle Group.  For more
 * information on the ObjectStyle Group, please see
 * <http://objectstyle.org/>.
 *
 */
package org.objectstyle.wolips.debug.logicalstructure.provider;

import org.eclipse.debug.core.ILogicalStructureProvider;
import org.eclipse.debug.core.ILogicalStructureType;
import org.eclipse.debug.core.model.IValue;
import org.objectstyle.wolips.debug.logicalstructure.LogicalStructure;
import org.objectstyle.wolips.debug.logicalstructure.type.directtoweb.BooleanQualifierLogicalStructureType;
import org.objectstyle.wolips.debug.logicalstructure.type.directtoweb.NonNullQualifierLogicalStructureType;
import org.objectstyle.wolips.debug.logicalstructure.type.eoaccess.EOAttributeLogicalStructureType;
import org.objectstyle.wolips.debug.logicalstructure.type.eoaccess.EOEntityLogicalStructureType;
import org.objectstyle.wolips.debug.logicalstructure.type.eocontrol.EOAndQualifierLogicalStructureType;
import org.objectstyle.wolips.debug.logicalstructure.type.eocontrol.EOCustomObjectLogicalStructureType;
import org.objectstyle.wolips.debug.logicalstructure.type.eocontrol.EOEditingContextLogicalStructureType;
import org.objectstyle.wolips.debug.logicalstructure.type.eocontrol.EOFetchSpecificationLogicalStructureType;
import org.objectstyle.wolips.debug.logicalstructure.type.eocontrol.EOKeyComparisonQualifierLogicalStructureType;
import org.objectstyle.wolips.debug.logicalstructure.type.eocontrol.EOKeyValueQualifierLogicalStructureType;
import org.objectstyle.wolips.debug.logicalstructure.type.eocontrol.EONotQualifierLogicalStructureType;
import org.objectstyle.wolips.debug.logicalstructure.type.eocontrol.EOOrQualifierLogicalStructureType;
import org.objectstyle.wolips.debug.logicalstructure.type.eocontrol.EOQualifierLogicalStructureType;
import org.objectstyle.wolips.debug.logicalstructure.type.eocontrol.EOSQLQualifierLogicalStructureType;
import org.objectstyle.wolips.debug.logicalstructure.type.eocontrol.EOSortOrderingLogicalStructureType;
import org.objectstyle.wolips.debug.logicalstructure.type.foundation.NSArrayLogicalStructureType;
import org.objectstyle.wolips.debug.logicalstructure.type.foundation.NSSelectorLogicalStructureType;

public class LogicalStructureProvider extends LogicalStructure implements ILogicalStructureProvider {

	public ILogicalStructureType[] getLogicalStructureTypes(IValue value) {
		boolean supported = this.isSuperclassOfValue("com.webobjects.eocontrol.EOCustomObject", value);
		if (supported) {
			ILogicalStructureType[] logicalStructureTypes = new ILogicalStructureType[1];
			EOCustomObjectLogicalStructureType customObjectLogicalStructureType = new EOCustomObjectLogicalStructureType(value);
			logicalStructureTypes[0] = customObjectLogicalStructureType;
			return logicalStructureTypes;
		}
		supported = this.isSuperclassOfValue("com.webobjects.eocontrol.EOEditingContext", value);
		if (supported) {
			ILogicalStructureType[] logicalStructureTypes = new ILogicalStructureType[1];
			EOEditingContextLogicalStructureType editingContextLogicalStructureType = new EOEditingContextLogicalStructureType(value);
			logicalStructureTypes[0] = editingContextLogicalStructureType;
			return logicalStructureTypes;
		}
		supported = this.isSuperclassOfValue("com.webobjects.eocontrol.EOSortOrdering", value);
		if (supported) {
			ILogicalStructureType[] logicalStructureTypes = new ILogicalStructureType[1];
			EOSortOrderingLogicalStructureType sortOrderingLogicalStructureType = new EOSortOrderingLogicalStructureType(value);
			logicalStructureTypes[0] = sortOrderingLogicalStructureType;
			return logicalStructureTypes;
		}
		supported = this.isSuperclassOfValue("com.webobjects.eocontrol.EOFetchSpecification", value);
		if (supported) {
			ILogicalStructureType[] logicalStructureTypes = new ILogicalStructureType[1];
			EOFetchSpecificationLogicalStructureType fetchSpecificationLogicalStructureType = new EOFetchSpecificationLogicalStructureType(value);
			logicalStructureTypes[0] = fetchSpecificationLogicalStructureType;
			return logicalStructureTypes;
		}
		// subclass of EOQualifier
		supported = this.isSuperclassOfValue("com.webobjects.directtoweb.BooleanQualifier", value);
		if (supported) {
			ILogicalStructureType[] logicalStructureTypes = new ILogicalStructureType[1];
			BooleanQualifierLogicalStructureType booleanQualifierLogicalStructureType = new BooleanQualifierLogicalStructureType(value);
			logicalStructureTypes[0] = booleanQualifierLogicalStructureType;
			return logicalStructureTypes;
		}
		// subclass of EOQualifier
		supported = this.isSuperclassOfValue("com.webobjects.eocontrol.EOAndQualifier", value);
		if (supported) {
			ILogicalStructureType[] logicalStructureTypes = new ILogicalStructureType[1];
			EOQualifierLogicalStructureType andLogicalStructureType = new EOAndQualifierLogicalStructureType(value);
			logicalStructureTypes[0] = andLogicalStructureType;
			return logicalStructureTypes;
		}
		// subclass of EOQualifier
		supported = this.isSuperclassOfValue("com.webobjects.eocontrol.EOKeyComparisonQualifier", value);
		if (supported) {
			ILogicalStructureType[] logicalStructureTypes = new ILogicalStructureType[1];
			EOKeyComparisonQualifierLogicalStructureType keyComparisonQualifierLogicalStructureType = new EOKeyComparisonQualifierLogicalStructureType(value);
			logicalStructureTypes[0] = keyComparisonQualifierLogicalStructureType;
			return logicalStructureTypes;
		}
		// subclass of EOQualifier
		supported = this.isSuperclassOfValue("com.webobjects.eocontrol.EOKeyValueQualifier", value);
		if (supported) {
			ILogicalStructureType[] logicalStructureTypes = new ILogicalStructureType[1];
			EOKeyValueQualifierLogicalStructureType keyValueQualifierLogicalStructureType = new EOKeyValueQualifierLogicalStructureType(value);
			logicalStructureTypes[0] = keyValueQualifierLogicalStructureType;
			return logicalStructureTypes;
		}
		// subclass of EOQualifier
		supported = this.isSuperclassOfValue("com.webobjects.eocontrol.EONotQualifier", value);
		if (supported) {
			ILogicalStructureType[] logicalStructureTypes = new ILogicalStructureType[1];
			EONotQualifierLogicalStructureType notQualifierLogicalStructureType = new EONotQualifierLogicalStructureType(value);
			logicalStructureTypes[0] = notQualifierLogicalStructureType;
			return logicalStructureTypes;
		}
		// subclass of EOQualifier
		supported = this.isSuperclassOfValue("com.webobjects.eocontrol.EOOrQualifier", value);
		if (supported) {
			ILogicalStructureType[] logicalStructureTypes = new ILogicalStructureType[1];
			EOOrQualifierLogicalStructureType orQualifierLogicalStructureType = new EOOrQualifierLogicalStructureType(value);
			logicalStructureTypes[0] = orQualifierLogicalStructureType;
			return logicalStructureTypes;
		}
		// subclass of EOQualifier
		supported = this.isSuperclassOfValue("com.webobjects.eocontrol.EOSQLQualifier", value);
		if (supported) {
			ILogicalStructureType[] logicalStructureTypes = new ILogicalStructureType[1];
			EOSQLQualifierLogicalStructureType sqLogicalStructureType = new EOSQLQualifierLogicalStructureType(value);
			logicalStructureTypes[0] = sqLogicalStructureType;
			return logicalStructureTypes;
		}
		// subclass of EOQualifier
		supported = this.isSuperclassOfValue("com.webobjects.directtoweb.NonNullQualifier", value);
		if (supported) {
			ILogicalStructureType[] logicalStructureTypes = new ILogicalStructureType[1];
			NonNullQualifierLogicalStructureType nonNullQualifierLogicalStructureType = new NonNullQualifierLogicalStructureType(value);
			logicalStructureTypes[0] = nonNullQualifierLogicalStructureType;
			return logicalStructureTypes;
		}
		// make sure that eoqualifier is after all subclasses of eoqualifier
		supported = this.isSuperclassOfValue("com.webobjects.eocontrol.EOQualifier", value);
		if (supported) {
			ILogicalStructureType[] logicalStructureTypes = new ILogicalStructureType[1];
			EOQualifierLogicalStructureType eoQualifierLogicalStructureType = new EOQualifierLogicalStructureType(value);
			logicalStructureTypes[0] = eoQualifierLogicalStructureType;
			return logicalStructureTypes;
		}
		supported = this.isSuperclassOfValue("com.webobjects.eoaccess.EOEntity", value);
		if (supported) {
			ILogicalStructureType[] logicalStructureTypes = new ILogicalStructureType[1];
			EOEntityLogicalStructureType entityLogicalStructureType = new EOEntityLogicalStructureType(value);
			logicalStructureTypes[0] = entityLogicalStructureType;
			return logicalStructureTypes;
		}
		supported = this.isSuperclassOfValue("com.webobjects.eoaccess.EOAttribute", value);
		if (supported) {
			ILogicalStructureType[] logicalStructureTypes = new ILogicalStructureType[1];
			EOAttributeLogicalStructureType attributeLogicalStructureType = new EOAttributeLogicalStructureType(value);
			logicalStructureTypes[0] = attributeLogicalStructureType;
			return logicalStructureTypes;
		}
		supported = this.isSuperclassOfValue("com.webobjects.foundation.NSSelector", value);
		if (supported) {
			ILogicalStructureType[] logicalStructureTypes = new ILogicalStructureType[1];
			NSSelectorLogicalStructureType selectorLogicalStructureType = new NSSelectorLogicalStructureType(value);
			logicalStructureTypes[0] = selectorLogicalStructureType;
			return logicalStructureTypes;
		}
		// only useful for wo < 5.3 (since 5.3 NSArray is a collection)
		supported = this.isSuperclassOfValue("com.webobjects.foundation.NSArray", value);
		if (supported) {
			ILogicalStructureType[] logicalStructureTypes = new ILogicalStructureType[1];
			NSArrayLogicalStructureType arrayLogicalStructureType = new NSArrayLogicalStructureType(value);
			logicalStructureTypes[0] = arrayLogicalStructureType;
			return logicalStructureTypes;
		}
		return new ILogicalStructureType[0];
	}
}
