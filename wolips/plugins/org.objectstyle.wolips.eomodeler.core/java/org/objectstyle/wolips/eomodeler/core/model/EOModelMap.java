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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.objectstyle.wolips.eomodeler.core.utils.BooleanUtils;

public class EOModelMap implements Map {
	public static final int YESNO = 1;

	public static final int YN = 2;

	public static final int YNOptional = 3;

	private Map myBackingMap;

	public EOModelMap() {
		this(new PropertyListMap());
	}

	public EOModelMap(Map _backingMap) {
		if (_backingMap == null) {
			myBackingMap = new PropertyListMap();
		} else {
			myBackingMap = _backingMap;
		}
	}

	public EOModelMap cloneModelMap() {
		Map sortedMap = new PropertyListMap();
		sortedMap.putAll(myBackingMap);
		return new EOModelMap(sortedMap);
	}

	// utility method--there is probably a better place for this
	public static Object[] asArray(Object o) {
		if (o instanceof Collection) {
			Collection c = (Collection) o;
			if (c.size() > 0) {
				return c.toArray();
			}
		}

		return null;
	}

	public Map getBackingMap() {
		return myBackingMap;
	}

	public void setBoolean(String _key, Boolean _value, int _booleanStyle) {
		if (_value == null) {
			myBackingMap.remove(_key);
		} else if (_booleanStyle == EOModelMap.YESNO) {
			myBackingMap.put(_key, _value.booleanValue() ? "YES" : "NO");
		} else if (_booleanStyle == EOModelMap.YNOptional) {
			if (_value.booleanValue()) {
				myBackingMap.put(_key, "Y");
			} else if (!"N".equals(myBackingMap.get(_key))) { // leave "N" if
																// it's already
																// there
				myBackingMap.remove(_key);
			}
		} else {
			myBackingMap.put(_key, _value.booleanValue() ? "Y" : "N");
		}
	}

	public Boolean getBoolean(String _key) {
		Object value = get(_key);
		Boolean boolValue;
		if (value == null) {
			boolValue = null;
		} else if (value instanceof Boolean) {
			boolValue = (Boolean) value;
		} else if (value instanceof String) {
			String strValue = getString(_key, true);
			if (strValue != null) {
				boolValue = Boolean.valueOf(BooleanUtils.isTrue(strValue)); //$NON-NLS-4$
			} else {
				boolValue = null;
			}
		} else {
			throw new IllegalArgumentException("Unknown boolean value '" + value + "' for '" + _key + "'");
		}
		return boolValue;
	}

	public void setMap(String _key, Map _map, boolean _skipIfEmpty) {
		Map map = _map;
		if (_skipIfEmpty && map != null && map.isEmpty()) {
			map = null;
		}
		if (map != null) {
			myBackingMap.put(_key, map);
		} else {
			myBackingMap.remove(_key);
		}
	}

	public Map getMap(String _key) {
		return (Map) myBackingMap.get(_key);
	}

	public Map getMap(String _key, boolean _clone) {
		Map map = (Map) myBackingMap.get(_key);
		if (_clone && map != null) {
			Map sortedMap = new PropertyListMap();
			sortedMap.putAll(map);
			map = sortedMap;
		}
		return map;
	}

	public void setList(String _key, List _list, boolean _skipIfEmpty) {
		setCollection(_key, _list, _skipIfEmpty);
	}

	public void setSet(String _key, Set _list, boolean _skipIfEmpty) {
		setCollection(_key, _list, _skipIfEmpty);
	}

	public void setCollection(String _key, Collection _list, boolean _skipIfEmpty) {
		Collection list = _list;
		if (_skipIfEmpty && list != null && list.isEmpty()) {
			list = null;
		}
		if (list == null) {
			myBackingMap.remove(_key);
		} else {
			myBackingMap.put(_key, list);
		}
	}

	public Set getSet(String _key) {
		return (Set) myBackingMap.get(_key);
	}

	public Set getSet(String _key, boolean _clone) {
		Set set = (Set) myBackingMap.get(_key);
		if (_clone && set != null) {
			Set sortedSet = new PropertyListSet();
			sortedSet.addAll(set);
			set = sortedSet;
		}
		return set;
	}

	public List getList(String _key) {
		return (List) myBackingMap.get(_key);
	}

	public List getList(String _key, boolean _clone) {
		List list = (List) myBackingMap.get(_key);
		if (_clone && list != null) {
			list = new LinkedList(list);
		}
		return list;
	}

	public void setString(String _key, String _value, boolean _emptyIsNull) {
		if (_value != null && (!_emptyIsNull || _value.trim().length() > 0)) {
			myBackingMap.put(_key, _value);
		} else {
			myBackingMap.remove(_key);
		}
	}

	public String getString(String _key, boolean _emptyIsNull) {
		Object objValue = myBackingMap.get(_key);
		String strValue;
		if (objValue instanceof String) {
			strValue = (String) objValue;
		} else if (objValue instanceof Number) {
			strValue = objValue.toString();
		} else if (objValue instanceof Boolean) {
			strValue = objValue.toString();
		} else {
			strValue = (String) objValue; // MS: Force a class cast in this
			// case
		}
		if (_emptyIsNull && strValue != null && strValue.trim().length() == 0) {
			strValue = null;
		}
		return strValue;
	}

	public void setInteger(String _key, Integer _value) {
		if (_value == null) {
			myBackingMap.remove(_key);
		} else {
			myBackingMap.put(_key, _value);
		}
	}

	public Integer getInteger(String _key) {
		Object value = get(_key);
		Integer integerValue;
		if (value == null) {
			integerValue = null;
		} else if (value instanceof Integer) {
			integerValue = (Integer) value;
		} else if (value instanceof String) {
			String strValue = getString(_key, true);
			integerValue = Integer.valueOf(strValue);
		} else {
			throw new IllegalArgumentException("Unknown integer value '" + value + "' for '" + _key + "'");
		}
		return integerValue;
	}

	public void clear() {
		myBackingMap.clear();
	}

	public boolean containsKey(Object _key) {
		return myBackingMap.containsKey(_key);
	}

	public boolean containsValue(Object _value) {
		return myBackingMap.containsValue(_value);
	}

	public Set entrySet() {
		return myBackingMap.entrySet();
	}

	public boolean equals(Object _o) {
		return myBackingMap.equals(_o);
	}

	public Object get(Object _key) {
		return myBackingMap.get(_key);
	}

	public int hashCode() {
		return myBackingMap.hashCode();
	}

	public boolean isEmpty() {
		return myBackingMap.isEmpty();
	}

	public Set keySet() {
		return myBackingMap.keySet();
	}

	public Object put(Object arg0, Object arg1) {
		return myBackingMap.put(arg0, arg1);
	}

	public void putAll(Map arg0) {
		myBackingMap.putAll(arg0);
	}

	public Object remove(Object _key) {
		return myBackingMap.remove(_key);
	}

	public int size() {
		return myBackingMap.size();
	}

	public Collection values() {
		return myBackingMap.values();
	}

	public String toString() {
		return "[EOModelMap: backingMap = " + myBackingMap + "]";
	}
}
