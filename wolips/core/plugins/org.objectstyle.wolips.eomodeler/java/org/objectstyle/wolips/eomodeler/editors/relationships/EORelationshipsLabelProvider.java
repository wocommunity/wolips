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
package org.objectstyle.wolips.eomodeler.editors.relationships;

import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.objectstyle.wolips.eomodeler.Activator;
import org.objectstyle.wolips.eomodeler.model.EOAttribute;
import org.objectstyle.wolips.eomodeler.model.EOEntity;
import org.objectstyle.wolips.eomodeler.model.EOJoin;
import org.objectstyle.wolips.eomodeler.model.EORelationship;
import org.objectstyle.wolips.eomodeler.utils.TablePropertyLabelProvider;

public class EORelationshipsLabelProvider extends TablePropertyLabelProvider implements ITableColorProvider, ITableFontProvider {
	private TableViewer _tableViewer;

	private Font _flattenedFont;

	private Font _inheritedFont;

	private Font _flattenedInheritedFont;

	public EORelationshipsLabelProvider(TableViewer tableViewer, String[] columnProperties) {
		super(columnProperties);
		_tableViewer = tableViewer;
	}

	public Image getColumnImage(Object element, String property) {
		EORelationship relationship = (EORelationship) element;
		Image image = null;
		if (property == EORelationship.TO_MANY) {
			image = yesNoImage(relationship.isToMany(), Activator.getDefault().getImageRegistry().get(Activator.TO_MANY_ICON), Activator.getDefault().getImageRegistry().get(Activator.TO_ONE_ICON), Activator.getDefault().getImageRegistry().get(Activator.TO_ONE_ICON));
		} else if (property == EORelationship.CLASS_PROPERTY) {
			image = yesNoImage(relationship.isClassProperty(), Activator.getDefault().getImageRegistry().get(Activator.CLASS_PROPERTY_ICON), null, null);
		}
		return image;
	}

	public String getColumnText(Object element, String property) {
		EORelationship relationship = (EORelationship) element;
		String text = null;
		if (property == EORelationship.TO_MANY) {
			// DO NOTHING
		} else if (property == EORelationship.CLASS_PROPERTY) {
			// DO NOTHING
		} else if (property == EORelationship.DESTINATION) {
			EOEntity destination = relationship.getDestination();
			if (destination != null) {
				text = destination.getName();
			}
		} else if (property == EOJoin.SOURCE_ATTRIBUTE) {
			EOJoin firstJoin = relationship.getFirstJoin();
			if (firstJoin != null) {
				EOAttribute sourceAttribute = firstJoin.getSourceAttribute();
				if (sourceAttribute != null) {
					text = sourceAttribute.getName();
				}
			}
		} else if (property == EOJoin.DESTINATION_ATTRIBUTE) {
			EOJoin firstJoin = relationship.getFirstJoin();
			if (firstJoin != null) {
				EOAttribute destinationAttribute = firstJoin.getDestinationAttribute();
				if (destinationAttribute != null) {
					text = destinationAttribute.getName();
				}
			}
		} else {
			text = super.getColumnText(element, property);
		}
		return text;
	}

	public Font getFont(Object element, int columnIndex) {
		EORelationship relationship = (EORelationship) element;
		Font font = null;
		boolean inherited = relationship.isInherited();
		boolean flattened = relationship.isFlattened();
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
		return font;
	}

	public Color getBackground(Object element, int columnIndex) {
		// EORelationship relationship = (EORelationship) element;
		return null;
	}

	public Color getForeground(Object element, int columnIndex) {
		Color color = null;
		// EORelationship relationships = (EORelationship) element;
		// if (relationships.isInherited()) {
		// color =
		// myTableViewer.getTable().getDisplay().getSystemColor(SWT.COLOR_GRAY);
		// }
		// if (attribute.isPrototyped()) {
		// color = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED);
		// }
		return color;
	}

	public void dispose() {
		if (_flattenedFont != null) {
			_flattenedFont.dispose();
		}
		if (_inheritedFont != null) {
			_inheritedFont.dispose();
		}
		if (_flattenedInheritedFont != null) {
			_flattenedInheritedFont.dispose();
		}
		super.dispose();
	}
}
