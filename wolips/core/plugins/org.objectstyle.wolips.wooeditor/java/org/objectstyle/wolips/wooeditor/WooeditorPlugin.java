/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2007 The ObjectStyle Group,
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

package org.objectstyle.wolips.wooeditor;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.objectstyle.wolips.baseforuiplugins.AbstractBaseUIActivator;
import org.objectstyle.wolips.wodclipse.core.woo.WooModel;
import org.osgi.framework.BundleContext;

/**
 * The main plug-in class to be used in the desktop.
 */
public class WooeditorPlugin extends AbstractBaseUIActivator implements IResourceChangeListener {
  // The shared instance.
  private static WooeditorPlugin plugin;

  /**
   * Returns the shared instance.
   */
  public static WooeditorPlugin getDefault() {
    return plugin;
  }

  /**
   * Returns an image descriptor for the image file at the given plug-in
   * relative path.
   *
   * @param path
   *            the path
   * @return the image descriptor
   */
  public static ImageDescriptor getImageDescriptor(final String path) {
    return AbstractUIPlugin.imageDescriptorFromPlugin("org.objectstyle.wolips.wooeditor", path);
  }

  private FormColors formColors;

  private IWorkspace workspace;

  /**
   * The constructor.
   */
  public WooeditorPlugin() {
    super();
    plugin = this;
    workspace = ResourcesPlugin.getWorkspace();
  }

  public FormColors getFormColors(final Display display) {
    if (formColors == null) {
      formColors = new FormColors(display);
      formColors.markShared();
    }
    return formColors;
  }

  public Image getImage(final String key) {
    return getImageRegistry().get(key);
  }

  @Override
  public void resourceChanged(final IResourceChangeEvent event) {
    if (event.getType() == IResourceChangeEvent.POST_CHANGE) {
      final IResourceDelta delta = event.getDelta();
      final ArrayList<IResource> changed = new ArrayList<IResource>();
      IResourceDeltaVisitor visitor = new IResourceDeltaVisitor() {
        @Override
		public boolean visit(final IResourceDelta visitingDelta) {
          // only interested in changed encoding
          if (visitingDelta.getKind() != IResourceDelta.CHANGED || (visitingDelta.getFlags() & IResourceDelta.ENCODING) == 0) {
            return true;
          }

          IResource resource = visitingDelta.getResource();

          if (resource.getProjectRelativePath().toString().equals("build")) {
            return false;
          }

          // only interested in folders with the "wo" extension
          if (resource.getType() == IResource.FOLDER && "wo".equalsIgnoreCase(resource.getFileExtension())) {
            changed.add(resource);
          }
          return true;
        }
      };
      try {
        delta.accept(visitor);
      }
      catch (CoreException e) {
        // XXX open error dialog with syncExec or print to log file
        e.printStackTrace();
      }

      Display.getDefault().asyncExec(new Runnable() {
        @Override
		public void run() {
          for (IResource resource : changed) {
            IFolder folder = (IFolder) resource;
            try {
              String charset = folder.getDefaultCharset();
              IPath wooPath = folder.getLocation().addTrailingSeparator().append(folder.getName() + "o");
              IFile wooFile = workspace.getRoot().getFileForLocation(wooPath);
              WooModel.updateEncoding(wooFile, charset);

              // Change the eclipse encoding type of the Component template

              for (IResource element : folder.members()) {
                if (element.getType() == IResource.FILE) {
                  IFile file = (IFile) element;
                  if (file.getFileExtension().matches("(xml|html|xhtml|wod)") && !file.getCharset().equals(charset)) {
                    System.out.println("WooeditorPlugin.run: setting encoding of " + file + " to " + charset);
                    file.setCharset(charset, null);
                  }
                }
              }

            }
            catch (CoreException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
          }
        }
      });
    }
  }

  /**
   * This method is called upon plug-in activation
   */
  @Override
  public void start(final BundleContext context) throws Exception {
    super.start(context);

    workspace.addResourceChangeListener(this);
  }

  /**
   * This method is called when the plug-in is stopped
   */
  @Override
  public void stop(final BundleContext context) throws Exception {
    try {
      if (formColors != null) {
        formColors.dispose();
        formColors = null;
      }
    }
    finally {
      super.stop(context);
    }
    workspace.removeResourceChangeListener(this);

    plugin = null;
  }

}
