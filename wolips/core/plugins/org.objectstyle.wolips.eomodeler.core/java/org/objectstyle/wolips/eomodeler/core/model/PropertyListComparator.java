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

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class PropertyListComparator implements Comparator<Object> {

	public static final PropertyListComparator AscendingInsensitivePropertyListComparator = new PropertyListComparator(true);
	public static final PropertyListComparator AscendingSensitivePropertyListComparator = new PropertyListComparator(false);

	private boolean _caseInsensitive;
	protected Map guideMap;

	public static PropertyListComparator propertyListComparatorWithGuideArray(Object[] guideArray) {
		if (guideArray != null && guideArray.length > 0) {
			Object[] sortedArray = guideArray.clone();
			Arrays.sort(sortedArray, AscendingInsensitivePropertyListComparator);

			if (!Arrays.equals(guideArray, sortedArray)) { // if it was already
															// sorted, don't use
															// it
				PropertyListComparator result = new PropertyListComparator(true);
				int size = guideArray.length;
				if (size > 0) {
					result.guideMap = new HashMap(size);

					for (int i = 0; i < size; ++i) {
						Object origEntry = guideArray[i];
						Object oneEntry = origEntry;
						if (origEntry instanceof Map) {
							Object name = ((Map) origEntry).get("name");
							if (name == null) {
								name = ((Map) origEntry).get("prototypeName");
							}
							if (name != null) {
								oneEntry = name;
							}
						}
						if (oneEntry != null) {
							result.guideMap.put(oneEntry, new Integer(i));
						}
					}
				}

				return result;
			}
		}

		return AscendingInsensitivePropertyListComparator;
	}

	public PropertyListComparator(boolean caseInsensitive) {
		_caseInsensitive = caseInsensitive;
	}
	
	public int compare(Object arg0, Object arg1) {
		if (arg0 == null) {
			return (arg1 == null) ? 0 : -1;
		} else if (arg1 == null) {
			return 1;
		} else if (guideMap != null && (guideMap.get(arg0) != null || guideMap.get(arg1) != null)) {
			Integer guide0 = (Integer) guideMap.get(arg0);
			Integer guide1 = (Integer) guideMap.get(arg1);

			if (guide0 == null) { // guide1 not null, by check above
				return 1;
			} else if (guide1 == null) {
				return -1;
			} else {
				return guide0.compareTo(guide1);
			}
		} else if (arg0 instanceof String && arg1 instanceof String) {
			if (_caseInsensitive) {
				return ((String) arg0).compareToIgnoreCase((String) arg1);
			}
			return ((String) arg0).compareTo((String) arg1);
		} else if (arg0 instanceof Number && arg1 instanceof Number) {
			double d0 = ((Number) arg0).doubleValue();
			double d1 = ((Number) arg1).doubleValue();
			if (d0 > d1) {
				return 1;
			} else if (d0 < d1) {
				return -1;
			}
			return 0;
		} else if (arg0 instanceof Timestamp && arg1 instanceof Timestamp) {
			return ((Timestamp) arg0).compareTo((Timestamp) arg1);
		} else if (arg0 instanceof ISortableEOModelObject && arg1 instanceof ISortableEOModelObject) {
			int comparison = compare(((ISortableEOModelObject) arg0).getName(), ((ISortableEOModelObject) arg1).getName());
			return comparison;
		} else if (arg0 instanceof Map && arg1 instanceof Map) {
			Map dic0 = (Map) arg0;
			Map dic1 = (Map) arg1;
			Object key0 = dic0.get("name");
			if (key0 == null) {
				key0 = dic0.get("prototypeName");
			}
			Object key1 = dic1.get("name");
			if (key1 == null) {
				key1 = dic1.get("prototypeName");
			}
			if (key0 != null && key1 != null) {
				return compare(key0, key1);
			} else if (key0 != key1) {
				throw new IllegalArgumentException("no 'name' key for either: " + arg0 + " or " + arg1);
			}
			// if no "name" keys are present, compare the keys and values
			Set allKeys0 = dic0.keySet();
			Set allKeys1 = dic1.keySet();
			Iterator allKeys0Iter = allKeys0.iterator();
			Iterator allKeys1Iter = allKeys1.iterator();
			while (allKeys0Iter.hasNext() && allKeys1Iter.hasNext()) {
				key0 = allKeys0Iter.next();
				key1 = allKeys1Iter.next();
				int compareResult = compare(key0, key1);
				if (compareResult != 0) {
					return compareResult;
				}
				compareResult = compare(dic0.get(key0), dic1.get(key1));
				if (compareResult != 0) {
					return compareResult;
				}
			}
			return compare(new Integer(allKeys0.size()), new Integer(allKeys1.size()));
		} else {
			int compareResult = compare(arg0.getClass().getName(), arg1.getClass().getName());
			if (compareResult != 0) {
				return compareResult;
			}
			return arg0.toString().compareTo(arg1.toString());
		}
	}
}
