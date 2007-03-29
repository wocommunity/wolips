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
package org.objectstyle.wolips.eomodeler.model;

import java.util.Map;

import org.objectstyle.wolips.eomodeler.utils.BooleanUtils;
import org.objectstyle.wolips.eomodeler.utils.NotificationMap;

public abstract class AbstractEOAttributePath implements IUserInfoable, IEOEntityRelative {
	private EORelationshipPath myParentRelationshipPath;

	private IEOAttribute myChildAttribute;

	public AbstractEOAttributePath(EORelationshipPath _parentRelationshipPath, IEOAttribute _childAttribute) {
		myParentRelationshipPath = _parentRelationshipPath;
		myChildAttribute = _childAttribute;
	}

	public EORelationshipPath getParentRelationshipPath() {
		return myParentRelationshipPath;
	}

	public IEOAttribute getChildIEOAttribute() {
		return myChildAttribute;
	}

	public NotificationMap<Object, Object> getUserInfo() {
		return myChildAttribute.getUserInfo();
	}

	public void setUserInfo(Map<Object, Object> _userInfo) {
		myChildAttribute.setUserInfo(_userInfo);
	}

	public void setUserInfo(Map<Object, Object> _userInfo, boolean _fireEvents) {
		myChildAttribute.setUserInfo(_userInfo, _fireEvents);
	}

	public EOEntity getEntity() {
		return myChildAttribute.getEntity();
	}

	public EOEntity getRootEntity() {
		EOEntity entity;
		if (myParentRelationshipPath != null) {
			entity = myParentRelationshipPath.getRootEntity();
		} else {
			entity = getEntity();
		}
		return entity;
	}

	public Boolean isToMany() {
		Boolean toMany = null;
		AbstractEOAttributePath attributePath = this;
		while (!BooleanUtils.isTrue(toMany) && attributePath != null) {
			IEOAttribute childAttribute = attributePath.getChildIEOAttribute();
			if (childAttribute == null) {
				toMany = Boolean.FALSE;
				attributePath = null;
			}
			else {
				toMany = childAttribute.isToMany();
				attributePath = attributePath.getParentRelationshipPath();
			}
		}
		return toMany;
	}

	public String toKeyPath() {
		StringBuffer sb = new StringBuffer();
		toKeyPath(sb);
		return sb.toString();
	}

	public boolean isValid() {
		System.out.println("AbstractEOAttributePath.isValid: " + myChildAttribute);
		return myChildAttribute != null;
	}
	
	protected void toKeyPath(StringBuffer _keyPathBuffer) {
		if (myParentRelationshipPath != null) {
			myParentRelationshipPath.toKeyPath(_keyPathBuffer);
			_keyPathBuffer.append(".");
		}
		if (myChildAttribute != null) {
			String name = myChildAttribute.getName();
			_keyPathBuffer.append(name);
		}
		else {
			_keyPathBuffer.append("<invalid>");
		}
	}
}