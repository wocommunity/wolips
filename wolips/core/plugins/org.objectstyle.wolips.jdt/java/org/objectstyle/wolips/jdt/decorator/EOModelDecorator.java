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
package org.objectstyle.wolips.jdt.decorator;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.ui.ProblemsLabelDecorator;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.objectstyle.wolips.jdt.PluginImages;

public class EOModelDecorator implements ILabelDecorator {
	private boolean _decorated;

	private ILabelDecorator _problemsLabelDecorator;

	private Image _eomodelBundleImage;

	public EOModelDecorator() {
		_problemsLabelDecorator = new ProblemsLabelDecorator(null);
	}

	public synchronized Image decorateImage(Image image, Object element) {
		Image decoratedImage;
		if (_decorated) {
			decoratedImage = image;
		} else {
			_decorated = true;
			try {
				if (_eomodelBundleImage == null) {
					_eomodelBundleImage = PluginImages.createImageDescriptor("icons/EOModelBundle.png").createImage(false);
				}
				decoratedImage = _eomodelBundleImage;
				decoratedImage = PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator().decorateImage(decoratedImage, element);
				decoratedImage = _problemsLabelDecorator.decorateImage(decoratedImage, element);
			} finally {
				_decorated = false;
			}
		}
		return decoratedImage;
	}

	public String decorateText(String text, Object element) {
		IResource modelResource = (IResource) element;
		boolean isModelFile = (modelResource instanceof IFile);
		IContainer modelFolder;
		if (isModelFile) {
			IFile modelFile = (IFile) element;
			modelFolder = modelFile.getParent();
		} else {
			modelFolder = (IContainer) modelResource;
		}
		String fileExtension = "." + modelFolder.getFileExtension();
		String decoratedText;
		if (isModelFile) {
			decoratedText = text.replace(fileExtension, "");
		} else {
			decoratedText = text.replace(fileExtension, " EOModel");
		}
		return decoratedText;
	}

	public void addListener(ILabelProviderListener _listener) {
		// DO NOTHING
	}

	public void dispose() {
		if (_eomodelBundleImage != null) {
			_eomodelBundleImage.dispose();
			_eomodelBundleImage = null;
		}
	}

	public boolean isLabelProperty(Object _element, String _property) {
		return false;
	}

	public void removeListener(ILabelProviderListener _listener) {
		// DO NOTHING
	}

}
