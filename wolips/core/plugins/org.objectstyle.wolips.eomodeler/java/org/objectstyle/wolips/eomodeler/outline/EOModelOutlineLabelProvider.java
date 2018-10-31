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
package org.objectstyle.wolips.eomodeler.outline;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.objectstyle.wolips.eomodeler.Activator;
import org.objectstyle.wolips.eomodeler.core.model.AbstractEOAttributePath;
import org.objectstyle.wolips.eomodeler.core.model.EOArgument;
import org.objectstyle.wolips.eomodeler.core.model.EOAttribute;
import org.objectstyle.wolips.eomodeler.core.model.EOAttributePath;
import org.objectstyle.wolips.eomodeler.core.model.EODatabaseConfig;
import org.objectstyle.wolips.eomodeler.core.model.EOEntity;
import org.objectstyle.wolips.eomodeler.core.model.EOEntityIndex;
import org.objectstyle.wolips.eomodeler.core.model.EOFetchSpecification;
import org.objectstyle.wolips.eomodeler.core.model.EOModel;
import org.objectstyle.wolips.eomodeler.core.model.EORelationship;
import org.objectstyle.wolips.eomodeler.core.model.EORelationshipPath;
import org.objectstyle.wolips.eomodeler.core.model.EOStoredProcedure;
import org.objectstyle.wolips.eomodeler.core.model.IEOAttribute;
import org.objectstyle.wolips.eomodeler.core.utils.BooleanUtils;

import ch.rucotec.wolips.eomodeler.core.model.EOERDiagram;
import ch.rucotec.wolips.eomodeler.core.model.EOERDiagramGroup;

public class EOModelOutlineLabelProvider implements ILabelProvider, IFontProvider, IColorProvider {
	private TreeViewer _treeViewer;

	private Font _inheritedFont;

	private Font _flattenedFont;

	private Font _flattenedInheritedFont;

	private Font _activeFont;

	public EOModelOutlineLabelProvider(TreeViewer treeViewer) {
		_treeViewer = treeViewer;
	}

	public void addListener(ILabelProviderListener listener) {
		// DO NOTHING
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
		if (_activeFont != null) {
			_activeFont.dispose();
		}
	}

	public Image getImage(Object element) {
		Image image;
		if (element instanceof String) {
			image = Activator.getDefault().getImageRegistry().get(Activator.EOMODEL_ICON);
		} else if (element instanceof EOModel) {
			image = Activator.getDefault().getImageRegistry().get(Activator.EOMODEL_ICON);
		} else if (element instanceof EOEntity) {
			image = Activator.getDefault().getImageRegistry().get(Activator.EOENTITY_ICON);
		} else if (element instanceof EOAttribute) {
			image = Activator.getDefault().getImageRegistry().get(Activator.EOATTRIBUTE_ICON);
		} else if (element instanceof EORelationship) {
			image = Activator.getDefault().getImageRegistry().get(Activator.EORELATIONSHIP_ICON);
		} else if (element instanceof EORelationshipPath) {
			image = Activator.getDefault().getImageRegistry().get(Activator.EORELATIONSHIP_ICON);
		} else if (element instanceof EOAttributePath) {
			image = Activator.getDefault().getImageRegistry().get(Activator.EOATTRIBUTE_ICON);
		} else if (element instanceof EOFetchSpecification) {
			image = Activator.getDefault().getImageRegistry().get(Activator.EOFETCHSPEC_ICON);
		} else if (element instanceof EOStoredProcedure) {
			image = Activator.getDefault().getImageRegistry().get(Activator.EOSTOREDPROCEDURE_ICON);
		} else if (element instanceof EOArgument) {
			image = Activator.getDefault().getImageRegistry().get(Activator.EOATTRIBUTE_ICON);
		} else if (element instanceof EODatabaseConfig) {
			image = Activator.getDefault().getImageRegistry().get(Activator.EODATABASECONFIG_ICON);
		} else if (element instanceof EOEntityIndex) {
			image = Activator.getDefault().getImageRegistry().get(Activator.EOENTITYINDEX_ICON);
		} else if (element instanceof EOERDiagramGroup) { // SAVAS das Image für EOERD
			image = Activator.getDefault().getImageRegistry().get(Activator.EOSTOREDPROCEDURE_ICON);
		} else if (element instanceof EOERDiagram) {
			image = Activator.getDefault().getImageRegistry().get(Activator.EORELATIONSHIP_ICON);
		} else {
			image = null;
		}
		return image;
	}

	public String getText(Object _element) {
		String text;
		if (_element instanceof String) {
			text = (String) _element;
		} else if (_element instanceof EOModel) {
			EOModel model = (EOModel) _element;
			text = model.getName();
			if (model.isDirty()) {
				text = text + "*";
			}
		} else if (_element instanceof EOEntity) {
			EOEntity entity = (EOEntity) _element;
			text = entity.getName();
		} else if (_element instanceof EOAttribute) {
			EOAttribute attribute = (EOAttribute) _element;
			text = attribute.getName();
		} else if (_element instanceof EORelationship) {
			EORelationship relationship = (EORelationship) _element;
			text = relationship.getName();
		} else if (_element instanceof EORelationshipPath) {
			EORelationshipPath relationshipPath = (EORelationshipPath) _element;
			text = relationshipPath.getChildRelationship().getName();
		} else if (_element instanceof EOAttributePath) {
			EOAttributePath attributePath = (EOAttributePath) _element;
			text = attributePath.getChildAttribute().getName();
		} else if (_element instanceof EOFetchSpecification) {
			EOFetchSpecification fetchSpec = (EOFetchSpecification) _element;
			text = fetchSpec.getName();
		} else if (_element instanceof EOArgument) {
			EOArgument argument = (EOArgument) _element;
			text = argument.getName();
		} else if (_element instanceof EOStoredProcedure) {
			EOStoredProcedure storedProcedure = (EOStoredProcedure) _element;
			text = storedProcedure.getName();
		} else if (_element instanceof EODatabaseConfig) {
			EODatabaseConfig databaseConfig = (EODatabaseConfig) _element;
			text = databaseConfig.getName();
		} else if (_element instanceof EOEntityIndex) {
			EOEntityIndex entityIndex = (EOEntityIndex) _element;
			text = entityIndex.getName();
		} else if (_element instanceof EOERDiagramGroup){ // SAVAS Name im TreeView, die sich im Outline befindet
			EOERDiagramGroup erdGroup = (EOERDiagramGroup) _element;
			text = erdGroup.getName();
		} else if (_element instanceof EOERDiagram){
			EOERDiagram erd = (EOERDiagram) _element;
			text = erd.getName();
		} else {
			text = null;
		}
		if (text == null) {
			text = "?";
		}
		return text;
	}

	protected Font getInheritedFont() {
		if (_inheritedFont == null) {
			Font originalFont = _treeViewer.getTree().getFont();
			FontData[] fontData = _treeViewer.getTree().getFont().getFontData();
			_inheritedFont = new Font(originalFont.getDevice(), fontData[0].getName(), fontData[0].getHeight(), SWT.ITALIC);
		}
		return _inheritedFont;
	}

	protected Font getFlattenedFont() {
		if (_flattenedFont == null) {
			Font originalFont = _treeViewer.getTree().getFont();
			FontData[] fontData = _treeViewer.getTree().getFont().getFontData();
			_flattenedFont = new Font(originalFont.getDevice(), fontData[0].getName(), fontData[0].getHeight(), SWT.BOLD);
		}
		return _flattenedFont;
	}

	protected Font getFlattenedInheritedFont() {
		if (_flattenedInheritedFont == null) {
			Font originalFont = _treeViewer.getTree().getFont();
			FontData[] fontData = _treeViewer.getTree().getFont().getFontData();
			_flattenedInheritedFont = new Font(originalFont.getDevice(), fontData[0].getName(), fontData[0].getHeight(), SWT.BOLD | SWT.ITALIC);
		}
		return _flattenedInheritedFont;
	}

	protected Font getActiveFont() {
		if (_activeFont == null) {
			Font originalFont = _treeViewer.getTree().getFont();
			FontData[] fontData = _treeViewer.getTree().getFont().getFontData();
			_activeFont = new Font(originalFont.getDevice(), fontData[0].getName(), fontData[0].getHeight(), SWT.BOLD);
		}
		return _activeFont;
	}

	public Font getFont(Object element) {
		Font font = null;
		if (element instanceof EOEntity) {
			EOEntity entity = (EOEntity) element;
			if (BooleanUtils.isTrue(entity.isAbstractEntity())) {
				font = getInheritedFont();
			}
		} else if (element instanceof EODatabaseConfig) {
			EODatabaseConfig databaseConfig = (EODatabaseConfig) element;
			if (databaseConfig.isActive()) {
				font = getActiveFont();
			}
		} else {
			IEOAttribute attribute = null;
			if (element instanceof IEOAttribute) {
				attribute = (IEOAttribute) element;
			} else if (element instanceof AbstractEOAttributePath) {
				AbstractEOAttributePath attributePath = (AbstractEOAttributePath) element;
				attribute = attributePath.getChildIEOAttribute();
			}
			if (attribute != null) {
				boolean flattened = attribute.isFlattened();
				boolean inherited = attribute.isInherited();
				if (flattened && inherited) {
					font = getFlattenedInheritedFont();
				} else if (flattened) {
					font = getFlattenedFont();
				} else if (inherited) {
					font = getInheritedFont();
				}
			}
		}
		return font;
	}

	public Color getForeground(Object element) {
//		EOModel relatedModel = EOModelUtils.getRelatedModel(element);
//		if (relatedModel == null || !relatedModel.isEditing()) {
//			return _treeViewer.getTree().getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY);
//		}
		return null;
	}

	public Color getBackground(Object element) {
		return null;
	}

	public boolean isLabelProperty(Object _element, String _property) {
		return false;
	}

	public void removeListener(ILabelProviderListener _listener) {
		// DO NOTHING
	}
}
