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
package org.objectstyle.wolips.debug.logicalstructure.type.eoaccess;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.jdt.debug.core.IJavaClassType;
import org.eclipse.jdt.internal.debug.core.logicalstructures.JavaLogicalStructure;
import org.objectstyle.wolips.debug.Activator;
import org.objectstyle.wolips.debug.logicalstructure.type.SuffixProvider;
import org.objectstyle.wolips.debug.logicalstructure.type.foundation.NSKeyValueCodingLogicalStructureType;

public class EOAttributeSuffixProvider extends SuffixProvider {

	public EOAttributeSuffixProvider(String keyPathToEOEntity) {
		super(keyPathToEOEntity);
	}

	public String getSuffix(String key, NSKeyValueCodingLogicalStructureType keyValueCodingLogicalStructureType) {
		IValue resolvedValue = null;
		IJavaClassType classType;
		try {
			classType = keyValueCodingLogicalStructureType.getIJavaClassType(keyValueCodingLogicalStructureType.getParentValue());
			JavaLogicalStructure entityJavaLogicalStructure = new JavaLogicalStructure(classType.getName(), true, "return valueForKeyPath(\"" + this.getSuffix() + "\");", "bla", new String[0][0]);
			resolvedValue = entityJavaLogicalStructure.getLogicalStructure(keyValueCodingLogicalStructureType.getParentValue());
		} catch (DebugException e) {
			Activator.getDefault().log(e);
		} catch (CoreException e) {
			Activator.getDefault().log(e);
		}
		EOEntityLogicalStructureType entityLogicalStructureType = new EOEntityLogicalStructureType(resolvedValue);
		try {
			classType = keyValueCodingLogicalStructureType.getIJavaClassType(resolvedValue);
			JavaLogicalStructure attributeJavaLogicalStructure = new JavaLogicalStructure(classType.getName(), true, "return attributeNamed(\"" + key + "\");", "bla", new String[0][0]);
			resolvedValue = attributeJavaLogicalStructure.getLogicalStructure(entityLogicalStructureType.getParentValue());
		} catch (DebugException e) {
			Activator.getDefault().log(e);
		} catch (CoreException e) {
			Activator.getDefault().log(e);
		}
		EOAttributeLogicalStructureType attributeLogicalStructureType = new EOAttributeLogicalStructureType(resolvedValue);
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append(" ( ");
		if (this.evaluatesToTrue(attributeLogicalStructureType.resolve("return allowsNull();"))) {
			stringBuffer.append("allowsNull ");
		}
		if (this.evaluatesToTrue(attributeLogicalStructureType.resolve("return isDerived();"))) {
			stringBuffer.append("isDerived ");
		}
		if (this.evaluatesToTrue(attributeLogicalStructureType.resolve("return isFlattened();"))) {
			stringBuffer.append("isFlattened ");
		}
		if (this.evaluatesToTrue(attributeLogicalStructureType.resolve("return isReadOnly();"))) {
			stringBuffer.append("isReadOnly ");
		}
		String className = attributeLogicalStructureType.resolve("return className();");
		if (className != null && className.length() > 0 && !className.equals("null")) {
			stringBuffer.append(this.beautifyClassName(className) + " ");
		}
		String externalType = attributeLogicalStructureType.resolve("return externalType();");
		if (externalType != null && externalType.length() > 0 && !externalType.equals("null")) {
			stringBuffer.append(externalType + " ");
		}
		String width = attributeLogicalStructureType.resolve("return width();");
		if (width != null && width.length() > 0 && !width.equals("null")) {
			stringBuffer.append(width + " ");
		}
		String precision = attributeLogicalStructureType.resolve("return precision();");
		if (precision != null && precision.length() > 0 && !precision.equals("null") && !precision.equals("0")) {
			stringBuffer.append(precision + " ");
		}
		stringBuffer.append(")");
		return stringBuffer.toString();
	}
}