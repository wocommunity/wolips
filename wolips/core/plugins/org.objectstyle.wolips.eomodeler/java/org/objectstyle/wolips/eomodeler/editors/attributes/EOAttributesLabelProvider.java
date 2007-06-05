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
package org.objectstyle.wolips.eomodeler.editors.attributes;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.objectstyle.wolips.eomodeler.Activator;
import org.objectstyle.wolips.eomodeler.core.model.AbstractEOArgument;
import org.objectstyle.wolips.eomodeler.core.model.EOAttribute;
import org.objectstyle.wolips.eomodeler.utils.TablePropertyLabelProvider;

public class EOAttributesLabelProvider extends TablePropertyLabelProvider implements ITableColorProvider, ITableFontProvider, ILabelProvider {
	private TableViewer _tableViewer;

	private Font _inheritedFont;

	private Font _flattenedFont;

	private Font _flattenedInheritedFont;
	
	private String _blankText;

	public EOAttributesLabelProvider(String[] properties) {
		super(properties);
	}

	public EOAttributesLabelProvider(String tableName) {
		this(null, tableName, null);
	}

	public EOAttributesLabelProvider(String tableName, String blankText) {
		this(null, tableName, blankText);
	}

	public EOAttributesLabelProvider(TableViewer tableViewer, String tableName) {
		this(tableViewer, tableName, null);
	}

	public EOAttributesLabelProvider(TableViewer tableViewer, String tableName, String blankText) {
		super(tableName);
		_tableViewer = tableViewer;
		_blankText = blankText;
	}
	
	public void setBlankText(String blankText) {
		_blankText = blankText;
	}

	public Image getColumnImage(Object _element, String _property) {
		EOAttribute attribute = (EOAttribute) _element;
		Image image = null;
		if (EOAttribute.PRIMARY_KEY.equals(_property)) {
			image = yesNoImage(attribute.isPrimaryKey(), Activator.getDefault().getImageRegistry().get(Activator.PRIMARY_KEY_ICON), null, null);
		} else if (EOAttribute.USED_FOR_LOCKING.equals(_property)) {
			image = yesNoImage(attribute.isUsedForLocking(), Activator.getDefault().getImageRegistry().get(Activator.LOCKING_ICON), null, null);
		} else if (EOAttribute.CLASS_PROPERTY.equals(_property)) {
			image = yesNoImage(attribute.isClassProperty(), Activator.getDefault().getImageRegistry().get(Activator.CLASS_PROPERTY_ICON), null, null);
		} else if (AbstractEOArgument.ALLOWS_NULL.equals(_property)) {
			image = yesNoImage(attribute.isAllowsNull(), Activator.getDefault().getImageRegistry().get(Activator.CHECK_ICON), null, null);
		}
		return image;
	}

	protected String yesNoText(EOAttribute _attribute, Boolean _bool) {
		return yesNoText(_bool, !_attribute.getEntity().isPrototype());
	}

	public String getColumnText(Object _element, String _property) {
		EOAttribute attribute = (EOAttribute) _element;
		String text = null;
		if (EOAttribute.PRIMARY_KEY.equals(_property)) {
			// DO NOTHING
		} else if (EOAttribute.USED_FOR_LOCKING.equals(_property)) {
			// DO NOTHING
		} else if (EOAttribute.CLASS_PROPERTY.equals(_property)) {
			// DO NOTHING
		} else if (AbstractEOArgument.ALLOWS_NULL.equals(_property)) {
			// DO NOTHING
		} else if (EOAttribute.PROTOTYPE.equals(_property)) {
			EOAttribute prototype = attribute.getPrototype();
			if (prototype != null) {
				text = prototype.getName();
			}
		} else {
			text = super.getColumnText(_element, _property);
			if (text == null) {
				text = _blankText;
			}
		}
		return text;
	}

	public Font getFont(Object _element, int _columnIndex) {
		Font font = null;
		if (_tableViewer != null) {
			EOAttribute attribute = (EOAttribute) _element;
			boolean inherited = attribute.isInherited();
			boolean flattened = attribute.isFlattened();
			if (flattened && inherited) {
				if (_flattenedInheritedFont == null) {
					Font originalFont = _tableViewer.getTable().getFont();
					FontData[] fontData = _tableViewer.getTable().getFont().getFontData();
					_flattenedInheritedFont = new Font(originalFont.getDevice(), fontData[0].getName(), fontData[0].getHeight(), SWT.BOLD | SWT.ITALIC);
				}
				font = _flattenedInheritedFont;
			}
			else if (flattened) {
				if (_flattenedFont == null) {
					Font originalFont = _tableViewer.getTable().getFont();
					FontData[] fontData = _tableViewer.getTable().getFont().getFontData();
					_flattenedFont = new Font(originalFont.getDevice(), fontData[0].getName(), fontData[0].getHeight(), SWT.BOLD);
				}
				font = _flattenedFont;
			} else if (inherited) {
				if (_inheritedFont == null) {
					Font originalFont = _tableViewer.getTable().getFont();
					FontData[] fontData = _tableViewer.getTable().getFont().getFontData();
					_inheritedFont = new Font(originalFont.getDevice(), fontData[0].getName(), fontData[0].getHeight(), SWT.ITALIC);
				}
				font = _inheritedFont;
			}
		}
		return font;
	}

	public Color getBackground(Object _element, int _columnIndex) {
		// EOAttribute attribute = (EOAttribute) _element;
		return null;
	}

	public Color getForeground(Object _element, int _columnIndex) {
		Color color = null;
		EOAttribute attribute = (EOAttribute) _element;
		if (attribute != null) {
			// if (attribute.isInherited()) {
			// color =
			// myTableViewer.getTable().getDisplay().getSystemColor(SWT.COLOR_GRAY);
			// }
			String property = getColumnProperty(_columnIndex);
			if (attribute.isPrototyped(property)) {
				color = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED);
			}
		}
		return color;
	}

	public void dispose() {
		if (_inheritedFont != null) {
			_inheritedFont.dispose();
		}
		if (_flattenedFont != null) {
			_flattenedFont.dispose();
		}
		if (_flattenedInheritedFont != null) {
			_flattenedInheritedFont.dispose();
		}
		super.dispose();
	}

	public Image getImage(Object element) {
		return getColumnImage(element, AbstractEOArgument.NAME);
	}

	public String getText(Object element) {
		return getColumnText(element, AbstractEOArgument.NAME);
	}
}
