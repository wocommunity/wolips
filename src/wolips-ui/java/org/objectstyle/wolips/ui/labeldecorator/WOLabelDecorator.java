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
package org.objectstyle.wolips.ui.labeldecorator;

import java.util.Hashtable;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.objectstyle.wolips.core.plugin.IWOLipsPluginConstants;
import org.objectstyle.wolips.core.project.WOLipsProject;
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
	private Hashtable decoratedResources = new Hashtable();

	/**
	 * Constructor for WOLabelDecorator.
	 */
	public WOLabelDecorator() {
		super();
	}
	/**
	 * Method withName.
	 * @param aString
	 * @param image
	 * @return Image
	 */
	private Image createImagewithName(Image image, String aString) {
		return new WOImageDescriptor(image, aString).createImage();
	}
	/**
	 * Method subprojectImage.
	 * @param image
	 * @return Image
	 */
	private Image subprojectImage(Image image) {
		if (subprojectImage == null) {
			subprojectImage =
				this.createImagewithName(image, "subproj_overlay.gif");
		}
		return subprojectImage;
	}
	/**
	 * Method componentImage.
	 * @param image
	 * @return Image
	 */
	private Image componentImage(Image image) {
		if (componentImage == null) {
			componentImage =
				this.createImagewithName(image, "comp_overlay.gif");
		}
		return componentImage;
	}
	/**
	 * Method eomodelImage.
	 * @param image
	 * @return Image
	 */
	private Image eomodelImage(Image image) {
		if (eomodelImage == null) {
			eomodelImage =
				this.createImagewithName(image, "eomodel_overlay.gif");
		}
		return eomodelImage;
	}
	/**
	 * Method buildImage.
	 * @param image
	 * @return Image
	 */
	private Image buildImage(Image image) {
		if (buildImage == null) {
			buildImage = this.createImagewithName(image, "build_overlay.gif");
		}
		return buildImage;
	}
	/**
	 * Method imageForExtension.
	 * @param image
	 * @param aString
	 * @return Image
	 */
	private Image imageForExtension(Image image, String aString) {
		if (EXT_SUBPROJECT.equals(aString))
			return subprojectImage(image);
		if (EXT_COMPONENT.equals(aString))
			return componentImage(image);
		if (EXT_EOMODEL.equals(aString))
			return eomodelImage(image);
		if (EXT_FRAMEWORK.equals(aString) || EXT_WOA.equals(aString))
			return buildImage(image);
		return image;
	}
	private boolean isDecorated(IFolder element) {
		String elementKey = element.getLocation().toString();
		return (decoratedResources.containsKey(elementKey) && decoratedResources.get(elementKey).equals(element.getModificationStamp() + ""));
	}
	private void addToDecorated(IFolder element) {
		String elementKey = element.getLocation().toString();
		decoratedResources.put(elementKey, element.getModificationStamp() + "");
	}

	/**
	 * @see org.eclipse.jface.viewers.ILabelDecorator#decorateImage(Image, Object)
	 */
	public Image decorateImage(Image image, Object element) {
		if (!(element instanceof IFolder))
			return image;
		if (!WOLipsProject.isWOProjectResource((IResource) element))
			return image;
		//avoid memory leak
		if (this.isDecorated((IFolder) element))
			return image;
		this.addToDecorated((IFolder) element);
		String extension = ((IFolder) element).getFileExtension();
		return this.imageForExtension(image, extension);
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

	/**
		 * @author uli
		 *
		 * To change this generated comment edit the template variable "typecomment":
		 * Window>Preferences>Java>Templates.
		 * To enable and disable the creation of type comments go to
		 * Window>Preferences>Java>Code Generation.
		 */
	private class WOImageDescriptor extends CompositeImageDescriptor {

		private ImageData baseImageData;
		private ImageData overlayImageData;
		private Point size;

		/**
		 * Method WOImageDescriptor.
		 * @param image
		 * @param overlayImageFilename
		 */
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
