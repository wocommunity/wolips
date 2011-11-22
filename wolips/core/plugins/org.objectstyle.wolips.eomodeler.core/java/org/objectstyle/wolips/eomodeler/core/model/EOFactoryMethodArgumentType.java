/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2006 The ObjectStyle Group and individual authors of the
 * software. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * 3. The end-user documentation included with the redistribution, if any, must
 * include the following acknowlegement: "This product includes software
 * developed by the ObjectStyle Group (http://objectstyle.org/)." Alternately,
 * this acknowlegement may appear in the software itself, if and wherever such
 * third-party acknowlegements normally appear.
 * 
 * 4. The names "ObjectStyle Group" and "Cayenne" must not be used to endorse or
 * promote products derived from this software without prior written permission.
 * For written permission, please contact andrus@objectstyle.org.
 * 
 * 5. Products derived from this software may not be called "ObjectStyle" nor
 * may "ObjectStyle" appear in their names without prior written permission of
 * the ObjectStyle Group.
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
 * This software consists of voluntary contributions made by many individuals on
 * behalf of the ObjectStyle Group. For more information on the ObjectStyle
 * Group, please see <http://objectstyle.org/>.
 *  
 */
package org.objectstyle.wolips.eomodeler.core.model;

import org.objectstyle.wolips.baseforplugins.util.ComparisonUtils;
import org.objectstyle.wolips.eomodeler.core.Messages;

public class EOFactoryMethodArgumentType {
	public static final EOFactoryMethodArgumentType STRING = new EOFactoryMethodArgumentType("EOFactoryMethodArgumentIsNSString", Messages.getString("EOFactoryMethodArgumentType.string"));

	public static final EOFactoryMethodArgumentType BYTES = new EOFactoryMethodArgumentType("EOFactoryMethodArgumentIsBytes", Messages.getString("EOFactoryMethodArgumentType.bytes"));

	public static final EOFactoryMethodArgumentType DATA = new EOFactoryMethodArgumentType("EOFactoryMethodArgumentIsData", Messages.getString("EOFactoryMethodArgumentType.data"));

	public static final EOFactoryMethodArgumentType DATE = new EOFactoryMethodArgumentType("EOFactoryMethodArgumentIsDate", Messages.getString("EOFactoryMethodArgumentType.date"));

	public static final EOFactoryMethodArgumentType[] ARGUMENT_TYPES = new EOFactoryMethodArgumentType[] { EOFactoryMethodArgumentType.STRING, EOFactoryMethodArgumentType.DATA, EOFactoryMethodArgumentType.BYTES, EOFactoryMethodArgumentType.DATE };

	private String myID;

	private String myName;

	public EOFactoryMethodArgumentType(String _id, String _name) {
		myID = _id;
		myName = _name;
		myName = _name;
	}

	public String getID() {
		return myID;
	}

	public String getName() {
		return myName;
	}

	public String toString() {
		return "[EOFactoryMethodArgumentType: name = " + myName + "]";
	}

	public static EOFactoryMethodArgumentType getFactoryMethodArgumentTypeByID(String _id) {
		EOFactoryMethodArgumentType matchingArgumentType = null;
		if (_id != null) {
			for (int argumentTypeNum = 0; matchingArgumentType == null && argumentTypeNum < EOFactoryMethodArgumentType.ARGUMENT_TYPES.length; argumentTypeNum++) {
				EOFactoryMethodArgumentType argumentType = EOFactoryMethodArgumentType.ARGUMENT_TYPES[argumentTypeNum];
				if (ComparisonUtils.equals(argumentType.myID, _id)) {
					matchingArgumentType = argumentType;
				}
			}
			if (matchingArgumentType == null) {
				matchingArgumentType = EOFactoryMethodArgumentType.DATA;
			}
		}
		return matchingArgumentType;
	}
}
