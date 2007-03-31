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

import java.util.HashSet;
import java.util.Set;

import org.objectstyle.wolips.eomodeler.utils.StringUtils;

public class EOSortOrdering extends EOModelObject<EOFetchSpecification> {
	public static final String SELECTOR_ASCENDING = "compareAscending";

	public static final String SELECTOR_DESCENDING = "compareDescending";

	public static final String SELECTOR_CASE_INSENSITIVE_ASCENDING = "compareCaseInsensitiveAscending";

	public static final String SELECTOR_CASE_INSENSITIVE_DESCENDING = "compareCaseInsensitiveDescending";

	public static final String KEY = "key";

	public static final String SELECTOR_NAME = "selectorName";

	public static final String CASE_INSENSITIVE = "caseInsensitive";

	public static final String ASCENDING = "ascending";

	private String myKey;

	private String mySelectorName;

	public EOSortOrdering() {
		mySelectorName = EOSortOrdering.SELECTOR_ASCENDING;
	}

	public EOSortOrdering(String _key, String _selectorName) {
		myKey = _key;
		mySelectorName = _selectorName;
	}

	public Set<EOModelVerificationFailure> getReferenceFailures() {
		return new HashSet<EOModelVerificationFailure>();
	}

	protected void _propertyChanged(String _propertyName, Object _oldValue, Object _newValue) {
		// DO NOTHING
	}

	public void setKey(String _key) {
		String oldKey = myKey;
		myKey = _key;
		firePropertyChange(EOSortOrdering.KEY, oldKey, myKey);
	}

	public String getKey() {
		return myKey;
	}

	public void setSelectorName(String _selectorName) {
		String oldSelectorName = mySelectorName;
		mySelectorName = _selectorName;
		firePropertyChange(EOSortOrdering.SELECTOR_NAME, oldSelectorName, mySelectorName);
	}

	public String getSelectorName() {
		return mySelectorName;
	}

	public void setAscending(boolean _ascending) {
		String oldSelectorName = mySelectorName;
		Boolean oldAscending = Boolean.valueOf(isAscending());
		if (StringUtils.isSelectorNameEqual(EOSortOrdering.SELECTOR_ASCENDING, mySelectorName) || StringUtils.isSelectorNameEqual(EOSortOrdering.SELECTOR_DESCENDING, mySelectorName)) {
			mySelectorName = (_ascending) ? EOSortOrdering.SELECTOR_ASCENDING : EOSortOrdering.SELECTOR_DESCENDING;
		} else if (StringUtils.isSelectorNameEqual(EOSortOrdering.SELECTOR_CASE_INSENSITIVE_ASCENDING, mySelectorName) || StringUtils.isSelectorNameEqual(EOSortOrdering.SELECTOR_CASE_INSENSITIVE_DESCENDING, mySelectorName)) {
			mySelectorName = (_ascending) ? EOSortOrdering.SELECTOR_CASE_INSENSITIVE_ASCENDING : EOSortOrdering.SELECTOR_CASE_INSENSITIVE_DESCENDING;
		} else {
			mySelectorName = EOSortOrdering.SELECTOR_ASCENDING;
		}
		firePropertyChange(EOSortOrdering.ASCENDING, oldAscending, Boolean.valueOf(isAscending()));
		firePropertyChange(EOSortOrdering.SELECTOR_NAME, oldSelectorName, mySelectorName);
	}

	public boolean isAscending() {
		return (StringUtils.isSelectorNameEqual(EOSortOrdering.SELECTOR_ASCENDING, mySelectorName) || StringUtils.isSelectorNameEqual(EOSortOrdering.SELECTOR_CASE_INSENSITIVE_ASCENDING, mySelectorName));
	}

	public void setCaseInsensitive(boolean _caseInsensitive) {
		String oldSelectorName = mySelectorName;
		Boolean oldCaseInsensitive = Boolean.valueOf(isCaseInsensitive());
		if (StringUtils.isSelectorNameEqual(EOSortOrdering.SELECTOR_ASCENDING, mySelectorName) || StringUtils.isSelectorNameEqual(EOSortOrdering.SELECTOR_CASE_INSENSITIVE_ASCENDING, mySelectorName)) {
			mySelectorName = (_caseInsensitive) ? EOSortOrdering.SELECTOR_CASE_INSENSITIVE_ASCENDING : EOSortOrdering.SELECTOR_ASCENDING;
		} else if (StringUtils.isSelectorNameEqual(EOSortOrdering.SELECTOR_DESCENDING, mySelectorName) || StringUtils.isSelectorNameEqual(EOSortOrdering.SELECTOR_CASE_INSENSITIVE_DESCENDING, mySelectorName)) {
			mySelectorName = (_caseInsensitive) ? EOSortOrdering.SELECTOR_CASE_INSENSITIVE_DESCENDING : EOSortOrdering.SELECTOR_DESCENDING;
		} else {
			mySelectorName = EOSortOrdering.SELECTOR_ASCENDING;
		}
		firePropertyChange(EOSortOrdering.CASE_INSENSITIVE, oldCaseInsensitive, Boolean.valueOf(isCaseInsensitive()));
		firePropertyChange(EOSortOrdering.SELECTOR_NAME, oldSelectorName, mySelectorName);
	}

	public boolean isCaseInsensitive() {
		return (StringUtils.isSelectorNameEqual(EOSortOrdering.SELECTOR_CASE_INSENSITIVE_ASCENDING, mySelectorName) || StringUtils.isSelectorNameEqual(EOSortOrdering.SELECTOR_CASE_INSENSITIVE_DESCENDING, mySelectorName));
	}

	public void loadFromMap(EOModelMap _map) {
		myKey = _map.getString("key", true);
		mySelectorName = _map.getString("selectorName", true);
		// MS: Fix for accidental setting of selector name
		if (EOSortOrdering.ASCENDING.equals(mySelectorName)) {
			mySelectorName = EOSortOrdering.SELECTOR_ASCENDING;
		}
	}

	public EOModelMap toMap() {
		EOModelMap sortOrderingMap = new EOModelMap();
		sortOrderingMap.setString("class", "EOSortOrdering", true);
		sortOrderingMap.setString("key", myKey, true);
		sortOrderingMap.setString("selectorName", mySelectorName, true);
		return sortOrderingMap;
	}

	public void verify(Set<EOModelVerificationFailure> _failures) {
		// TODO
	}

	@Override
	public String getName() {
		return getKey();
	}
	
	@Override
	public EOSortOrdering _cloneModelObject() {
		EOSortOrdering cloneSortOrdering = new EOSortOrdering(myKey, mySelectorName);
		return cloneSortOrdering;
	}
	
	@Override
	public Class<EOFetchSpecification> _getModelParentType() {
		return EOFetchSpecification.class;
	}
	
	@Override
	public EOFetchSpecification _getModelParent() {
		return null;
	}

	@Override
	public void _addToModelParent(EOFetchSpecification modelParent, boolean findUniqueName, Set<EOModelVerificationFailure> failures) {
		// DO NOTHING
	}

	@Override
	public void _removeFromModelParent(Set<EOModelVerificationFailure> failures) {
		// DO NOTHING
	}

	public String getFullyQualifiedName() {
		return "EOSortOrdering:" + myKey;
	}
}
