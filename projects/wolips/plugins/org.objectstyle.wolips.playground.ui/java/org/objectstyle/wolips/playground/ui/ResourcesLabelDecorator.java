/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2004 The ObjectStyle Group 
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
package org.objectstyle.wolips.playground.ui;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.objectstyle.wolips.datasets.adaptable.Project;
import org.objectstyle.wolips.ui.UIPlugin;

/**
 * @author mnolte
 * 
 */
public class ResourcesLabelDecorator implements ILabelDecorator {

	/**
	 * Constructor for WOLabelDecorator.
	 */
	public ResourcesLabelDecorator() {
		super();
	}

	/**
	 * Method withName.
	 * 
	 * @param aString
	 * @param image
	 * @return Image
	 */
	private Image createImagewithName(Image image, String aString) {
		return new WOImageDescriptor(image, aString).createImage();
	}

	/**
	 * Method subprojectImage.
	 * 
	 * @param image
	 * @return Image
	 */
	private Image resourcesImage(Image image) {
		return this.createImagewithName(image, "resources_overlay.gif");
	}

	/**
	 * Method componentImage.
	 * 
	 * @param image
	 * @return Image
	 */
	private Image webServerResourcesImage(Image image) {
		return this
				.createImagewithName(image, "webserverresources_overlay.gif");
	}

	/**
	 * @see org.eclipse.jface.viewers.ILabelDecorator#decorateImage(Image,
	 *      Object)
	 */
	public Image decorateImage(Image image, Object element) {
		if (element instanceof IResource && !(element instanceof IContainer)) {
			IResource resource = (IResource) element;
			IProject iProject = resource.getProject();
			Project project = (Project) iProject.getAdapter(Project.class);
			try {
				if (project.hasWOLipsNature()) {
					if (project.matchesResourcesPattern(resource))
						return resourcesImage(image);
					if (project.matchesWOAppResourcesPattern(resource))
						return webServerResourcesImage(image);
				}
			} catch (CoreException e) {
				UIPlugin.getDefault().getPluginLogger().log(e);
			}
		}
		return image;
	}

	/**
	 * @see org.eclipse.jface.viewers.ILabelDecorator#decorateText(String,
	 *      Object)
	 */
	public String decorateText(String text, Object element) {
		return text;
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(ILabelProviderListener)
	 */
	public void addListener(ILabelProviderListener listener) {
		return;
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	public void dispose() {
		return;
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(Object,
	 *      String)
	 */
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(ILabelProviderListener)
	 */
	public void removeListener(ILabelProviderListener listener) {
		return;
	}

	/**
	 * @author uli
	 * 
	 * To change this generated comment edit the template variable
	 * "typecomment": Window>Preferences>Java>Templates. To enable and disable
	 * the creation of type comments go to Window>Preferences>Java>Code
	 * Generation.
	 */
	private class WOImageDescriptor extends CompositeImageDescriptor {

		private ImageData baseImageData;

		private ImageData overlayImageData;

		private Point size;

		/**
		 * Method WOImageDescriptor.
		 * 
		 * @param image
		 * @param overlayImageFilename
		 */
		public WOImageDescriptor(Image image, String overlayImageFilename) {
			super();
			if (image != null) {
				this.baseImageData = image.getImageData();
				this.size = new Point(this.baseImageData.width,
						this.baseImageData.height);
			}
			this.overlayImageData = ImageDescriptor.createFromFile(
					ResourcesLabelDecorator.class, overlayImageFilename)
					.getImageData();
			if (this.size == null) {
				this.size = new Point(this.overlayImageData.width,
						this.overlayImageData.height);
			}
		}

		/**
		 * @see org.eclipse.jface.resource.CompositeImageDescriptor#drawCompositeImage(int,
		 *      int)
		 */
		protected void drawCompositeImage(int width, int height) {
			// draw base image
			if (this.baseImageData != null) {
				this.drawImage(this.baseImageData, 0, 0);
			}
			int x = getSize().x;
			x -= this.overlayImageData.width;
			int y = getSize().y;
			y -= this.overlayImageData.height;
			this.drawImage(this.overlayImageData, x, 0);
		}

		/**
		 * @see org.eclipse.jface.resource.CompositeImageDescriptor#getSize()
		 */
		protected Point getSize() {
			return this.size;
		}
	}
}