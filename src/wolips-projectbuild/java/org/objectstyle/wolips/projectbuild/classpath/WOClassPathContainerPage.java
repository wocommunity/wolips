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
 
package org.objectstyle.wolips.projectbuild.classpath;

// http://www.eclipse.org/articles/Understanding%20Layouts/Understanding%20Layouts.htm

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.jdt.ui.wizards.IClasspathContainerPage;

import java.io.File;
import java.io.FilenameFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * Insert the type's description here.
 * @see WizardPage
 */
public class WOClassPathContainerPage 
  extends WizardPage 
  implements IClasspathContainerPage 
{
  /**
   * The constructor.
   */
  public WOClassPathContainerPage() {
    super ("non-localized WOClassPathContainerPage");
  }

  /**
   * Insert the method's description here.
   * @see WizardPage#createControl
   */
  public void createControl(Composite parent)  {
    Composite thisPage = new Composite(parent, SWT.NONE);
    
    thisPage.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    thisPage.setLayout(new GridLayout());
    //thisPage.setLayout(new RowLayout(SWT.VERTICAL));
    
    _uiList = CheckboxTableViewer.newCheckList(thisPage, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
    GridData gd = new GridData (GridData.HORIZONTAL_ALIGN_FILL|GridData.FILL_HORIZONTAL); //|GridData.VERTICAL_ALIGN_FILL
    Rectangle trim = _uiList.getTable().computeTrim(0,0,0,12*_uiList.getTable().getItemHeight());
    gd.heightHint = trim.height;
    _uiList.getTable().setLayoutData(gd);
    _uiList.setContentProvider(new ArrayContentProvider());
    _uiList.setLabelProvider(new LabelProvider());

    Label lbl = new Label (thisPage, SWT.SINGLE);
    lbl.setText("Hint: use Ctrl-click or Shift-click");
    
    thisPage.layout ();
    
    _initFrameworkList ();
    
    setControl(thisPage);
  }

  /**
   * Insert the method's description here.
   * @see WizardPage#finish
   */
  public boolean finish()  {
    
    boolean exported = false;
    if (null != _selection) {
      exported = _selection.isExported();
    }

    IPath path = new Path (WOClasspathContainer.WOCP_IDENTITY);

    //path = path.append ("exported=" + exported);

    Object [] selected = _uiList.getCheckedElements();
    for (int i = 0; i < selected.length; ++i) {
      path = path.append (selected[i].toString());
    }
    
    
    _selection = JavaCore.newContainerEntry(path, exported);
    
    return true;
  }

  /**
   * Insert the method's description here.
   * @see WizardPage#getSelection
   */
  public IClasspathEntry getSelection()  {
    return _selection;
  }

  /**
   * Insert the method's description here.
   * @see WizardPage#setSelection
   */
  public void setSelection(IClasspathEntry containerEntry)  {
    _selection = containerEntry;
    
    if (null == _selection) {
      IPath path = new Path (WOClasspathContainer.WOCP_IDENTITY);

      if (true /*WOSupportPlugin.shouldIncludeDefaultFrameworks()*/) {      
        path = path.append("JavaEOAccess");
        path = path.append("JavaEOControl");
        path = path.append("JavaEOProject");
        path = path.append("JavaFoundation");
        path = path.append("JavaJDBCAdaptor");
        path = path.append("JavaWebObjects");
        path = path.append("JavaWOExtensions");
        path = path.append("JavaXML");
      }

      if (true /*WOSupportPlugin.shouldIncludeJSPFrameworks()*/) {      
        path = path.append("JavaWOJSPServlet");
      }

      if (true/*WOSupportPlugin.shouldIncludeDTWFrameworks()*/) {      
        path = path.append("JavaDirectToWeb");
        path = path.append("JavaDTWGeneration");
      }

      _selection = JavaCore.newContainerEntry(path, false);
    }
    
    if ((null != _selection) && (null != getControl())) {
      _setSelection ();
    }
  }

  public static class WildcardFilenameFilter implements FilenameFilter {
    WildcardFilenameFilter (String prefix, String suffix) {
       _prefix = prefix;
       _suffix = suffix;
    }
    
    public boolean accept (File file, String name) {
      
      String lowerName = name.toLowerCase();
      
      return (
        ((null == _prefix) || lowerName.startsWith(_prefix))
        && ((null == _suffix) || lowerName.endsWith(_suffix))
      );
    }
    
    
    String _prefix;
    String _suffix;
  }
  
  
  public static class WOFWFilenameFilter implements FilenameFilter {
    public boolean accept (File file, String name) {
      /*name.startsWith("Java") &&*/ 
      boolean candidate = name.endsWith (".framework");

      boolean result = false;

      if (candidate) {
        File resDir = new File (file, name+"/Resources/Java");
        if (resDir.exists()) {
          
          String jarFiles[] = resDir.list(new WildcardFilenameFilter(null, ".jar"));
          String zipFiles[] = resDir.list(new WildcardFilenameFilter(null, ".zip"));
          
          result = (0 != jarFiles.length) || (0 != zipFiles.length);
          
        }
      }
      
      return (result);
    }
  };

  private void _initFrameworkList () {
    File fwBase = WOClasspathContainer._getFrameworkBase();
    File fwBaseL = WOClasspathContainer._getLocalFrameworkBase();

	if (fwBase.exists() && fwBase.isDirectory()) {
	  File frameworks[] = fwBase.listFiles(new WOFWFilenameFilter());
      
      List list = new ArrayList ();
      
      for (int i = 0; i < frameworks.length; ++i) {
        String thisOne = frameworks[i].getName();
        list.add(thisOne.substring(0, thisOne.length()-10)); // cut off the .framework
      }
      if (fwBaseL.exists() && fwBaseL.isDirectory()) {
        frameworks = fwBaseL.listFiles(new WOFWFilenameFilter());
        for (int i = 0; i < frameworks.length; ++i) {
          String thisOne = frameworks[i].getName();
          list.add(thisOne.substring(0, thisOne.length()-10)); // cut off the .framework
        }
      }
      
      _uiList.setInput(list);
    }
    
    if (null != _selection) {
      _setSelection ();
    }
  }
  
  private void _setSelection () {
    IPath path = _selection.getPath();
    
    _uiList.setAllChecked(false);
    
    for (int i=1; i < path.segmentCount(); ++i) {
      String thisOne = path.segment(i);
      
      _uiList.setChecked(thisOne, true);
      /*
      int thisIndex = _uiList.indexOf(thisOne);
      if (-1 != thisIndex) {
        _uiList.select(thisIndex);
      }
      */
    }
  }


  private IClasspathEntry _selection;
  private CheckboxTableViewer _uiList;
}
