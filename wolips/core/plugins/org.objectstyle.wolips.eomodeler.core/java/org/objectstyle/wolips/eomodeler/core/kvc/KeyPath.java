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
package org.objectstyle.wolips.eomodeler.core.kvc;

public class KeyPath implements IKey {
	private String[] myKeyNames;

	private Key[] myKeys;

	public KeyPath(String _keyPath) {
		this(_keyPath.split("\\."));
	}

	public KeyPath(String[] _keyNames) {
		myKeyNames = _keyNames;
	}

	public KeyPath(Key[] _keys) {
		myKeys = _keys;
		myKeyNames = new String[_keys.length];
		for (int keyNum = 0; keyNum < _keys.length; keyNum++) {
			myKeyNames[keyNum] = _keys[keyNum].getName();
		}
	}

	public String toKeyPath() {
		StringBuffer sb = new StringBuffer();
		for (int keyNum = 0; keyNum < myKeyNames.length; keyNum++) {
			sb.append(myKeyNames[keyNum]);
			sb.append(".");
		}
		if (myKeyNames.length > 0) {
			sb.setLength(sb.length() - 1);
		}
		return sb.toString();
	}

	public ResolvedKeyPath toResolvedKey(Class _declaringClass) {
		return new ResolvedKeyPath(_declaringClass, myKeyNames);
	}

	protected Key[] getKeys() {
		if (myKeys == null) {
			Key key = null;
			myKeys = new Key[myKeyNames.length];
			for (int keyNum = 0; keyNum < myKeys.length; keyNum++) {
				key = createKey(key, myKeyNames[keyNum]);
				myKeys[keyNum] = key;
			}
		}
		return myKeys;
	}

	@SuppressWarnings("unused")
	protected Key createKey(Key _previousKey, String _keyName) {
		return new Key(_keyName);
	}

	public Class getType(Object _instance) {
		Class type = null;
		Key[] keys = getKeys();
		if (keys.length > 0) {
			type = keys[keys.length - 1].getType(_instance);
		}
		return type;
	}

	public void setValue(Object _instance, Object _value) {
		Key[] keys = getKeys();
		Object instance = _instance;
		for (int keyNum = 0; instance != null && keyNum < keys.length - 1; keyNum++) {
			Key key = keys[keyNum];
			instance = key.getValue(instance);
		}
		if (instance != null) {
			keys[keys.length - 1].setValue(instance, _value);
		}
	}

	public Object getValue(Object _instance) {
		Key[] keys = getKeys();
		Object instance = _instance;
		for (int keyNum = 0; instance != null && keyNum < keys.length; keyNum++) {
			Key key = keys[keyNum];
			instance = key.getValue(instance);
		}
		return instance;
	}

	public String toString() {
		return "[KeyPath: " + toKeyPath() + "]";
	}

	public static Object getValue(Object _instance, String _keyPath) {
		Object value;
		if (_instance == null) {
			value = null;
		} else {
			ResolvedKeyPath keyPath = new ResolvedKeyPath(_instance.getClass(), _keyPath);
			value = keyPath.getValue(_instance);
		}
		return value;
	}
}