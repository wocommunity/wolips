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
package org.objectstyle.wolips.eomodeler.utils;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.objectstyle.wolips.eomodeler.core.kvc.CachingKeyPath;
import org.objectstyle.wolips.eomodeler.core.kvc.KeyPath;

public class TablePropertyLabelProvider implements ITableLabelProvider {
	private String[] myColumnProperties;

	private Map myKeys;

	public TablePropertyLabelProvider(String[] _columnProperties) {
		myColumnProperties = _columnProperties;
		myKeys = new HashMap();
		for (int keyNum = 0; keyNum < _columnProperties.length; keyNum++) {
			KeyPath keyPath = new CachingKeyPath(_columnProperties[keyNum]);
			myKeys.put(_columnProperties[keyNum], keyPath);
		}
	}

	public String getColumnProperty(int _columnIndex) {
		return myColumnProperties[_columnIndex];
	}

	public Image getColumnImage(Object _element, String _property) {
		return null;
	}

	public Image getColumnImage(Object _element, int _columnIndex) {
		return getColumnImage(_element, myColumnProperties[_columnIndex]);
	}

	public String getColumnText(Object _element, String _property) {
		String text = null;
		Object value = ((KeyPath) myKeys.get(_property)).getValue(_element);
		if (value != null) {
			text = value.toString();
		}
		return text;
	}

	public String getColumnText(Object _element, int _columnIndex) {
		return getColumnText(_element, myColumnProperties[_columnIndex]);
	}

	public void addListener(ILabelProviderListener _listener) {
		// DO NOTHING
	}

	public void dispose() {
		// DO NOTHING
	}

	public boolean isLabelProperty(Object _element, String _property) {
		return true;
	}

	public void removeListener(ILabelProviderListener _listener) {
		// DO NOTHING
	}

	protected Image yesNoImage(Boolean _bool, Image _yesImage, Image _noImage, Image _nullImage) {
		Image image;
		if (_bool == null) {
			image = _nullImage;
		} else if (_bool.booleanValue()) {
			image = _yesImage;
		} else {
			image = _noImage;
		}
		return image;
	}

	protected String yesNoText(Boolean _bool, String _yesText, String _noText, boolean _nullIsNo) {
		String str;
		if (_bool == null) {
			if (_nullIsNo) {
				str = _noText;
			} else {
				str = "";
			}
		} else if (_bool.booleanValue()) {
			str = _yesText;
		} else {
			str = _noText;
		}
		return str;
	}

	protected String yesNoText(Boolean _bool, boolean _nullIsNo) {
		return yesNoText(_bool, "Y", "N", _nullIsNo);
	}
}
