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
package org.objectstyle.wolips.debug.logicalstructure;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.jdt.debug.core.IJavaClassType;
import org.eclipse.jdt.debug.core.IJavaObject;
import org.eclipse.jdt.debug.core.IJavaType;
import org.eclipse.jdt.internal.debug.core.logicalstructures.JavaLogicalStructure;
import org.eclipse.jdt.internal.debug.core.model.JDIClassType;
import org.eclipse.jdt.internal.debug.core.model.JDIObjectValue;
import org.objectstyle.wolips.debug.Activator;

public abstract class LogicalStructure {

	public boolean isSuperclassOfValue(String superclass, IValue value) {
		if (value == null || !(value instanceof JDIObjectValue)) {
			return false;
		}
		JDIObjectValue jdiObjectValue = (JDIObjectValue) value;
		try {
			IJavaType javaType = jdiObjectValue.getJavaType();
			if (!(javaType instanceof JDIClassType)) {
				return false;
			}
			JDIClassType jdiClassType = (JDIClassType) javaType;
			if (jdiClassType.getName().equals(superclass)) {
				return true;
			}
			IJavaClassType javaClassType = jdiClassType.getSuperclass();
			while (javaClassType != null) {
				if (javaClassType.getName().equals(superclass)) {
					return true;
				}
				javaClassType = javaClassType.getSuperclass();
			}
		} catch (DebugException e) {
			Activator.getDefault().log(e);
		}
		return false;
	}

	public IJavaClassType getIJavaClassType(IValue value) throws DebugException {
		if (!(value instanceof IJavaObject)) {
			return null;
		}
		IJavaObject javaValue = (IJavaObject) value;
		IJavaType type = javaValue.getJavaType();
		if (!(type instanceof IJavaClassType)) {
			return null;
		}
		IJavaClassType classType = (IJavaClassType) type;
		return classType;
	}

	public String resolve(String method, IValue value) {
		String resolvedString = null;
		IJavaClassType classType;
		try {
			classType = this.getIJavaClassType(value);
			JavaLogicalStructure countJavaLogicalStructure = new JavaLogicalStructure(classType.getName(), true, method, "bla", new String[0][0]);
			IValue resolvedValue = countJavaLogicalStructure.getLogicalStructure(value);
			resolvedString = resolvedValue.getValueString();
		} catch (DebugException e) {
			Activator.getDefault().log(e);
		} catch (CoreException e) {
			Activator.getDefault().log(e);
		}
		return resolvedString;
	}

	public String[] concat(String[] strings, String[] moreStrings) {
		String[] returnValue = new String[strings.length + moreStrings.length];
		System.arraycopy(strings, 0, returnValue, 0, strings.length);
		System.arraycopy(moreStrings, 0, returnValue, strings.length, moreStrings.length);
		return returnValue;
	}
}
