/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2004 - 2006 The ObjectStyle Group 
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

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.objectstyle.wolips.core.resources.types.project.IProjectAdapter;
import org.objectstyle.wolips.core.resources.types.project.IProjectPatternsets;
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

	private Image createImageWithName(Image image, String aString) {
		return UIPlugin.getImageDescriptorRegistry().get(ResourcesLabelDecorator.cachedImageDescriptor(image, aString));
	}

	private Image resourcesImage(Image image) {
		return createImageWithName(image, "resources_overlay.gif");
	}

	private Image webServerResourcesImage(Image image) {
		return createImageWithName(image, "webserverresources_overlay.gif");
	}

	public Image decorateImage(Image image, Object element) {
		if (element instanceof IResource && !(element instanceof IProject)) {
			IResource resource = (IResource) element;
			IProject project = resource.getProject();
			IProjectAdapter projectAdapter = (IProjectAdapter) project.getAdapter(IProjectAdapter.class);
			// make sure it's a wo project
			if (projectAdapter != null) {
				IProjectPatternsets projectPatternsets = (IProjectPatternsets) project.getAdapter(IProjectPatternsets.class);
				if (projectPatternsets != null) {
					if (projectPatternsets.matchesResourcesPattern(resource)) {
						return resourcesImage(image);
					}
					if (projectPatternsets.matchesWOAppResourcesPattern(resource)) {
						return webServerResourcesImage(image);
					}
				}
			}
		}
		return image;
	}

	public String decorateText(String text, Object element) {
		return text;
	}

	public void addListener(ILabelProviderListener listener) {
		return;
	}

	public void dispose() {
		// DO NOTHING
	}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
		// DO NOTHING
	}

	protected static Map<String, Map<Image, WOImageDescriptor>> _imageDescriptors;

	public static WOImageDescriptor cachedImageDescriptor(Image image, String overlayImageFilename) {
		if (_imageDescriptors == null) {
			_imageDescriptors = new HashMap<String, Map<Image, WOImageDescriptor>>();
		}
		Map<Image, WOImageDescriptor> overlayImageDescriptors = _imageDescriptors.get(overlayImageFilename);
		if (overlayImageDescriptors == null) {
			overlayImageDescriptors = new WeakHashMap<Image, WOImageDescriptor>();
			_imageDescriptors.put(overlayImageFilename, overlayImageDescriptors);
		}
		WOImageDescriptor imageDescriptor = overlayImageDescriptors.get(image);
		if (imageDescriptor == null) {
			imageDescriptor = new WOImageDescriptor(image, overlayImageFilename);
			overlayImageDescriptors.put(image, imageDescriptor);
		}
		return imageDescriptor;
	}

	/**
	 * @author uli
	 * 
	 * To change this generated comment edit the template variable
	 * "typecomment": Window>Preferences>Java>Templates. To enable and disable
	 * the creation of type comments go to Window>Preferences>Java>Code
	 * Generation.
	 */
	private static class WOImageDescriptor extends CompositeImageDescriptor {
		private Image baseImage;

		private String overlayImageFilename;

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
				this.baseImage = image;
				this.baseImageData = image.getImageData();
				this.size = new Point(this.baseImageData.width, this.baseImageData.height);
			}
			this.overlayImageFilename = overlayImageFilename;
			this.overlayImageData = ImageDescriptor.createFromFile(ResourcesLabelDecorator.class, overlayImageFilename).getImageData();
			if (this.size == null) {
				this.size = new Point(this.overlayImageData.width, this.overlayImageData.height);
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

		public boolean equals(Object object) {
			if (object == null || !WOImageDescriptor.class.equals(object.getClass()))
				return false;

			WOImageDescriptor other = (WOImageDescriptor) object;
			boolean equals = true;
			if (baseImage == null) {
				equals = (other.baseImage == null);
			} else {
				equals = baseImage.equals(other.baseImage);
			}
			if (equals) {
				equals = this.overlayImageFilename.equals(other.overlayImageFilename);
			}
			return equals;
		}

		public int hashCode() {
			int hashcode = 0;
			if (this.baseImage != null) {
				hashcode |= this.baseImage.hashCode();
			}
			hashcode |= this.overlayImageFilename.hashCode();
			return hashcode;
		}
	}
}