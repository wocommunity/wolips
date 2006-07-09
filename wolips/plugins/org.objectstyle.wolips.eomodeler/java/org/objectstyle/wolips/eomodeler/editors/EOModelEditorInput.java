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
package org.objectstyle.wolips.eomodeler.editors;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.part.FileEditorInput;
import org.objectstyle.wolips.eomodeler.model.EOEntity;
import org.objectstyle.wolips.eomodeler.model.EOModel;
import org.objectstyle.wolips.eomodeler.model.EOModelException;
import org.objectstyle.wolips.eomodeler.model.EclipseEOModelGroupFactory;

public class EOModelEditorInput implements IFileEditorInput {
  private IFileEditorInput myFileEditorInput;
  private IContainer myModelFolder;
  private EOModel myModel;
  private String myFileEntityName;

  public EOModelEditorInput(IFileEditorInput _fileEditorInput, List _failures) throws CoreException, IOException, EOModelException {
    IFile file = _fileEditorInput.getFile();
    if ("plist".equalsIgnoreCase(file.getFileExtension())) { //$NON-NLS-1$
      String name = file.getName();
      myFileEntityName = name.substring(0, name.indexOf('.'));
      myFileEditorInput = new FileEditorInput(file.getParent().getFile(new Path("index.eomodeld"))); //$NON-NLS-1$
    }
    else {
      myFileEditorInput = _fileEditorInput;
    }
    myModelFolder = myFileEditorInput.getFile().getParent();
    myModel = EclipseEOModelGroupFactory.createModel(myFileEditorInput.getFile(), _failures);
  }
  
  public EOEntity getFileEntity() {
    EOEntity entity = null;
    if (myFileEntityName != null) {
      entity = myModel.getEntityNamed(myFileEntityName);
    }
    return entity;
  }

  public IContainer getModelFolder() {
    return myModelFolder;
  }

  public EOModel getModel() {
    return myModel;
  }

  public boolean exists() {
    return myFileEditorInput.exists();
  }

  public Object getAdapter(Class _adapter) {
    return myFileEditorInput.getAdapter(_adapter);
  }

  public IFile getFile() {
    return myFileEditorInput.getFile();
  }

  public ImageDescriptor getImageDescriptor() {
    return myFileEditorInput.getImageDescriptor();
  }

  public String getName() {
    return myFileEditorInput.getName();
  }

  public IPersistableElement getPersistable() {
    return myFileEditorInput.getPersistable();
  }

  public IStorage getStorage() throws CoreException {
    return myFileEditorInput.getStorage();
  }

  public String getToolTipText() {
    return myFileEditorInput.getToolTipText();
  }

}
