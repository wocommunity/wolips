/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2002 The ObjectStyle Group 
 * and individual authors of the software.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:  
 *       "This product includes software developed by the 
 *        ObjectStyle Group (http://objectstyle.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "ObjectStyle Group" and "Cayenne" 
 *    must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written 
 *    permission, please contact andrus@objectstyle.org.
 *
 * 5. Products derived from this software may not be called "ObjectStyle"
 *    nor may "ObjectStyle" appear in their names without prior written
 *    permission of the ObjectStyle Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE OBJECTSTYLE GROUP OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the ObjectStyle Group.  For more
 * information on the ObjectStyle Group, please see
 * <http://objectstyle.org/>.
 *
 */
package org.objectstyle.wolips.utils;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.objectstyle.wolips.IWOLipsPluginConstants;
import org.objectstyle.wolips.project.ProjectHelper;
/**
 * @author mnolte
 *
 */
public class WOLabelDecorator
	implements ILabelDecorator, IWOLipsPluginConstants {

	private static Image subprojectImage;
	private static Image componentImage;
	private static Image buildImage;
	private static Image eomodelImage;

	/**
	 * Constructor for WOLabelDecorator.
	 */
	public WOLabelDecorator() {
		super();
	}

	/**
	 * @see org.eclipse.jface.viewers.ILabelDecorator#decorateImage(Image, Object)
	 */
	public Image decorateImage(Image image, Object element) {

		if (element instanceof IFolder
			&& ProjectHelper.isWOProjectResource((IResource) element)) {
			String extension = ((IFolder) element).getFileExtension();
			if (EXT_SUBPROJECT.equals(extension)) {
				if (subprojectImage == null) {
					subprojectImage =
						new WOImageDescriptor(image, "subproj_overlay.gif")
							.createImage();
				}
				return subprojectImage;
			}
			if (EXT_COMPONENT.equals(extension)) {
				if (componentImage == null) {
					componentImage =
						new WOImageDescriptor(image, "comp_overlay.gif")
							.createImage();
				}
				return componentImage;
			}
			if (EXT_EOMODEL.equals(extension)) {
				if (eomodelImage == null) {
					eomodelImage =
						new WOImageDescriptor(image, "eomodel_overlay.gif")
							.createImage();
				}
				return eomodelImage;
			}
			if (EXT_WOA.equals(extension) || EXT_FRAMEWORK.equals(extension)) {
				if (buildImage == null) {
					buildImage =
						new WOImageDescriptor(image, "build_overlay.gif")
							.createImage();
				}
				return buildImage;
			}
		}
		return image;
	}

	/**
	 * @see org.eclipse.jface.viewers.ILabelDecorator#decorateText(String, Object)
	 */
	public String decorateText(String text, Object element) {
		return text;
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(ILabelProviderListener)
	 */
	public void addListener(ILabelProviderListener listener) {
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	public void dispose() {
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(Object, String)
	 */
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(ILabelProviderListener)
	 */
	public void removeListener(ILabelProviderListener listener) {
	}

	private class WOImageDescriptor extends CompositeImageDescriptor {

		private ImageData baseImageData;
		private ImageData overlayImageData;
		private Point size;

		public WOImageDescriptor(Image image, String overlayImageFilename) {
			baseImageData = image.getImageData();
			size = new Point(baseImageData.width, baseImageData.height);
			overlayImageData =
				ImageDescriptor
					.createFromFile(
						WOLabelDecorator.class,
						overlayImageFilename)
					.getImageData();
		}

		/**
		 * @see org.eclipse.jface.resource.CompositeImageDescriptor#drawCompositeImage(int, int)
		 */
		protected void drawCompositeImage(int width, int height) {
			// draw base image
			drawImage(baseImageData, 0, 0);
			int x = getSize().x;
			x -= overlayImageData.width;
			drawImage(overlayImageData, x, 0);
		}

		/**
		 * @see org.eclipse.jface.resource.CompositeImageDescriptor#getSize()
		 */
		protected Point getSize() {
			return size;
		}

	}

}
