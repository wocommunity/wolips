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

import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.objectstyle.wolips.eomodeler.Activator;
import org.objectstyle.wolips.eomodeler.model.EOArgument;
import org.objectstyle.wolips.eomodeler.model.EOAttribute;
import org.objectstyle.wolips.eomodeler.model.EOAttributePath;
import org.objectstyle.wolips.eomodeler.model.EODatabaseConfig;
import org.objectstyle.wolips.eomodeler.model.EOEntity;
import org.objectstyle.wolips.eomodeler.model.EOEntityIndex;
import org.objectstyle.wolips.eomodeler.model.EOFetchSpecification;
import org.objectstyle.wolips.eomodeler.model.EOModel;
import org.objectstyle.wolips.eomodeler.model.EORelationship;
import org.objectstyle.wolips.eomodeler.model.EORelationshipPath;
import org.objectstyle.wolips.eomodeler.model.EOStoredProcedure;
import org.objectstyle.wolips.eomodeler.utils.BooleanUtils;

public class EOModelOutlineLabelProvider implements ILabelProvider, IFontProvider {
	private TreeViewer myTreeViewer;

	private Font myInheritedFont;

	private Font myActiveFont;

	public EOModelOutlineLabelProvider(TreeViewer _treeViewer) {
		myTreeViewer = _treeViewer;
	}

	public void addListener(ILabelProviderListener _listener) {
		// DO NOTHING
	}

	public void dispose() {
		if (myInheritedFont != null) {
			myInheritedFont.dispose();
		}
		if (myActiveFont != null) {
			myActiveFont.dispose();
		}
	}

	public Image getImage(Object _element) {
		Image image;
		if (_element instanceof String) {
			image = Activator.getDefault().getImageRegistry().get(Activator.EOMODEL_ICON);
		} else if (_element instanceof EOModel) {
			image = Activator.getDefault().getImageRegistry().get(Activator.EOMODEL_ICON);
		} else if (_element instanceof EOEntity) {
			image = Activator.getDefault().getImageRegistry().get(Activator.EOENTITY_ICON);
		} else if (_element instanceof EOAttribute) {
			image = Activator.getDefault().getImageRegistry().get(Activator.EOATTRIBUTE_ICON);
		} else if (_element instanceof EORelationship) {
			image = Activator.getDefault().getImageRegistry().get(Activator.EORELATIONSHIP_ICON);
		} else if (_element instanceof EORelationshipPath) {
			image = Activator.getDefault().getImageRegistry().get(Activator.EORELATIONSHIP_ICON);
		} else if (_element instanceof EOAttributePath) {
			image = Activator.getDefault().getImageRegistry().get(Activator.EOATTRIBUTE_ICON);
		} else if (_element instanceof EOFetchSpecification) {
			image = Activator.getDefault().getImageRegistry().get(Activator.EOFETCHSPEC_ICON);
		} else if (_element instanceof EOStoredProcedure) {
			image = Activator.getDefault().getImageRegistry().get(Activator.EOSTOREDPROCEDURE_ICON);
		} else if (_element instanceof EOArgument) {
			image = Activator.getDefault().getImageRegistry().get(Activator.EOATTRIBUTE_ICON);
		} else if (_element instanceof EODatabaseConfig) {
			image = Activator.getDefault().getImageRegistry().get(Activator.EODATABASECONFIG_ICON);
		} else if (_element instanceof EOEntityIndex) {
			image = Activator.getDefault().getImageRegistry().get(Activator.EOENTITYINDEX_ICON);
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
		} else {
			text = null;
		}
		if (text == null) {
			text = "?";
		}
		return text;
	}

	public Font getFont(Object _element) {
		Font font = null;
		if (_element instanceof EOEntity) {
			EOEntity entity = (EOEntity) _element;
			if (BooleanUtils.isTrue(entity.isAbstractEntity())) {
				if (myInheritedFont == null) {
					Font originalFont = myTreeViewer.getTree().getFont();
					FontData[] fontData = myTreeViewer.getTree().getFont().getFontData();
					myInheritedFont = new Font(originalFont.getDevice(), fontData[0].getName(), fontData[0].getHeight(), SWT.ITALIC);
				}
				font = myInheritedFont;
			}
		}
		else if (_element instanceof EODatabaseConfig) {
			EODatabaseConfig databaseConfig = (EODatabaseConfig) _element;
			if (databaseConfig.isActive()) {
				if (myActiveFont == null) {
					Font originalFont = myTreeViewer.getTree().getFont();
					FontData[] fontData = myTreeViewer.getTree().getFont().getFontData();
					myActiveFont = new Font(originalFont.getDevice(), fontData[0].getName(), fontData[0].getHeight(), SWT.BOLD);
				}
				font = myActiveFont;
			}
		}
		return font;
	}

	public boolean isLabelProperty(Object _element, String _property) {
		return false;
	}

	public void removeListener(ILabelProviderListener _listener) {
		// DO NOTHING
	}
}
