/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2005 The ObjectStyle Group and individual authors of the
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
package org.objectstyle.wolips.ruleeditor.model;

import java.util.Map;

/**
 * @author uli
 */
public class Qualifier extends AbstractQualifierElement {
	private static final String KEY_KEY = "key";

	private static final String VALUE_KEY = "value";

	private static final String SELECTOR_NAME_KEY = "selectorName";

	public Qualifier(D2WModel model, Map map) {
		super(model, map);
	}

	public String getKey() {
		return (String) this.getMap().get(KEY_KEY);
	}

	public void setKey(String key) {
		this.getMap().put(KEY_KEY, key);
		this.getModel().setHasUnsavedChanges(true);
	}

	public String getValue() {
		return (String) this.getMap().get(VALUE_KEY);
	}

	public void setValue(String keyPath) {
		this.getMap().put(VALUE_KEY, keyPath);
		this.getModel().setHasUnsavedChanges(true);
	}

	public String getSelectorName() {
		return (String) this.getMap().get(SELECTOR_NAME_KEY);
	}

	public void setSelectorName(String selectorName) {
		this.getMap().put(SELECTOR_NAME_KEY, selectorName);
		this.getModel().setHasUnsavedChanges(true);
	}

	public void appendToDisplayStringBuffer(StringBuffer stringBuffer, String concatWith) {
		if (this.getQualifiers() == null) {
			stringBuffer.append(this.getKey());
			stringBuffer.append(" ");
			stringBuffer.append(this.getSelectorDisplayString());
			stringBuffer.append(" ");
			stringBuffer.append(this.getValue());
		}
		super.appendToDisplayStringBuffer(stringBuffer, concatWith);
	}

	private String getSelectorDisplayString() {
		String selectorName = this.getSelectorName();
		if (selectorName == null) {
			return null;
		}
		if ("isEqualTo".equals(selectorName)) {
			return "=";
		}
		if ("isNotEqualTo".equals(selectorName)) {
			return "!=";
		}
		if ("isLessThen".equals(selectorName)) {
			return "<";
		}
		if ("isLessThenOrEqualTo".equals(selectorName)) {
			return "<=";
		}
		if ("isGreaterThen".equals(selectorName)) {
			return ">";
		}
		if ("isGreaterThenOrEqualTo".equals(selectorName)) {
			return ">=";
		}
		if ("isLike".equals(selectorName)) {
			return "=";
		}
		return selectorName;
	}
}
