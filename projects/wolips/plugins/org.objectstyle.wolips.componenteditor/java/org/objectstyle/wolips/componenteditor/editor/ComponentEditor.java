/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2005 The ObjectStyle Group and individual authors of the
 * software. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 1.
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. The end-user documentation
 * included with the redistribution, if any, must include the following
 * acknowlegement: "This product includes software developed by the ObjectStyle
 * Group (http://objectstyle.org/)." Alternately, this acknowlegement may
 * appear in the software itself, if and wherever such third-party
 * acknowlegements normally appear. 4. The names "ObjectStyle Group" and
 * "Cayenne" must not be used to endorse or promote products derived from this
 * software without prior written permission. For written permission, please
 * contact andrus@objectstyle.org. 5. Products derived from this software may
 * not be called "ObjectStyle" nor may "ObjectStyle" appear in their names
 * without prior written permission of the ObjectStyle Group.
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
 * This software consists of voluntary contributions made by many individuals
 * on behalf of the ObjectStyle Group. For more information on the ObjectStyle
 * Group, please see <http://objectstyle.org/> .
 *  
 */
package org.objectstyle.wolips.componenteditor.editor;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.MultiEditor;
import org.eclipse.ui.part.MultiEditorInput;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.wst.html.ui.internal.provisional.StructuredTextEditorHTML;
import org.eclipse.wst.sse.ui.internal.contentoutline.StructuredTextEditorContentOutlinePage;
import org.objectstyle.wolips.componenteditor.ComponenteditorPlugin;
import org.objectstyle.wolips.wodclipse.WodclipsePlugin;
import org.objectstyle.wolips.wodclipse.wod.WodEditor;

/**
 * @author uli
 */
public class ComponentEditor extends MultiEditor {

  int activeEditorIndex = 0;

  CTabFolder folder;

  ComponentEditorInput componentEditorInput;

  boolean created = false;

  /*
   * @see IWorkbenchPart#createPartControl(Composite)
   */
  public void createPartControl(Composite parent) {
    final Composite activator = parent;
    Display.getDefault().asyncExec(new Runnable() {
      public void run() {
        activator.addListener(SWT.Activate, new Listener() {
          public void handleEvent(Event event) {
            if (event.type == SWT.Activate) {
            	ComponenteditorPlugin.getDefault().setActiveComponentEditor(ComponentEditor.this);
            }
            if (event.type == SWT.Deactivate) {
            	ComponenteditorPlugin.getDefault().setActiveComponentEditor(null);
            }
          }
        });
      }
    });
    folder = new CTabFolder(parent, SWT.BOTTOM);
    Composite javaEditorParent = new Composite(folder, SWT.NONE);
    javaEditorParent.setLayout(new FillLayout());
    final CTabItem javaTab = new CTabItem(folder, SWT.NONE);
    javaTab.setControl(javaEditorParent);
    javaTab.setText("Java");
    javaEditorParent.addListener(SWT.Activate, new Listener() {

      public void handleEvent(Event event) {
        setActiveEditorIndex(0);
      }

    });
    Composite componentEditorParent = new Composite(folder, SWT.NONE);
    componentEditorParent.setLayout(new FillLayout());
    final CTabItem componentTab = new CTabItem(folder, SWT.NONE);
    componentTab.setControl(componentEditorParent);
    componentTab.setText("Component");
    Composite apiEditorParent = new Composite(folder, SWT.NONE);
    apiEditorParent.setLayout(new FillLayout());
    final CTabItem apiTab = new CTabItem(folder, SWT.NONE);
    apiTab.setControl(apiEditorParent);
    apiTab.setText("Api");
    apiEditorParent.addListener(SWT.Activate, new Listener() {

      public void handleEvent(Event event) {
        setActiveEditorIndex(3);
      }

    });
    SashForm componentEditorSashParent = new SashForm(componentEditorParent, SWT.VERTICAL | SWT.SMOOTH);
    StructuredTextEditorHTML htmlEditor = null;
    StructuredTextEditorContentOutlinePage contentOutlinePage = null;
    IEditorPart[] innerEditors = getInnerEditors();
    for (int i = 0; i < innerEditors.length; i++) {
      final IEditorPart innerEditor = innerEditors[i];
      switch (i) {
      case 0:
        createInnerPartControl(javaEditorParent, innerEditor);
        addPropertyListener(innerEditor);
        break;

      case 1:
        SashForm htmlSashform = new SashForm(componentEditorSashParent, SWT.VERTICAL);
        createInnerPartControl(htmlSashform, innerEditor);
        addPropertyListener(innerEditor);
        htmlEditor = (StructuredTextEditorHTML) innerEditor;
        htmlSashform.addListener(SWT.Activate, new Listener() {

          public void handleEvent(Event event) {
            setActiveEditorIndex(1);
          }

        });
        contentOutlinePage = (StructuredTextEditorContentOutlinePage) htmlEditor.getAdapter(IContentOutlinePage.class);
        break;

      case 2:
        SashForm wodSashform = new SashForm(componentEditorSashParent, SWT.VERTICAL);
        createInnerPartControl(wodSashform, innerEditor);
        addPropertyListener(innerEditor);
        wodSashform.addListener(SWT.Activate, new Listener() {
          public void handleEvent(Event event) {
            setActiveEditorIndex(2);
          }

        });
        addWebObjectsTagNamesListener((WodEditor) innerEditor, htmlEditor);
        if (contentOutlinePage != null) {
          new HTMLOutlineSelectionHandler(contentOutlinePage, (WodEditor) innerEditor);
        }
        break;
      case 3:
        createInnerPartControl(apiEditorParent, innerEditor);
        addPropertyListener(innerEditor);
        break;

      default:
        break;
      }
    }

    Display.getDefault().asyncExec(new Runnable() {
      public void run() {
        if (componentEditorInput.isCreatedFromDotApi()) {
          folder.setSelection(apiTab);
        }
        else if (componentEditorInput.isCreatedFromDotHtml()) {
          folder.setSelection(componentTab);
        }
        else if (componentEditorInput.isCreatedFromDotJava()) {
          folder.setSelection(javaTab);
        }
        else if (componentEditorInput.isCreatedFromDotWod()) {
          folder.setSelection(componentTab);
        }
        else if (componentEditorInput.isCreatedFromDotWoo()) {
          folder.setSelection(componentTab);
        }
      }
    });
  }

  private void addWebObjectsTagNamesListener(final WodEditor wodEditor, StructuredTextEditorHTML htmlEditor) {
    htmlEditor.getSelectionProvider().addSelectionChangedListener(new ISelectionChangedListener() {

      public void selectionChanged(SelectionChangedEvent event) {
        WodclipsePlugin.getDefault().updateWebObjectsTagNames(wodEditor);
      }

    });
    wodEditor.getSelectionProvider().addSelectionChangedListener(new ISelectionChangedListener() {

      public void selectionChanged(SelectionChangedEvent event) {
        WodclipsePlugin.getDefault().updateWebObjectsTagNames(null);
      }

    });
    WodclipsePlugin.getDefault().updateWebObjectsTagNames(wodEditor);
  }

  public void setActiveEditorIndex(int _activeEditorIndex) {
    if (activeEditorIndex != _activeEditorIndex) {
      if (activeEditorIndex != -1) {
        IEditorPart lastEditorPart = getInnerEditors()[activeEditorIndex];
        //lastEditorPart.getEditorSite().getPage().addSelectionListener(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().get);
        //PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().removeSelectionListener(lastEditorPart);
      }

      activeEditorIndex = _activeEditorIndex;
      IEditorPart newEditorPart = getInnerEditors()[activeEditorIndex];

      if (newEditorPart != null) {
        //PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().addSelectionListener(newEditorPart);
      }
      //activateEditor(editorPart);
    }
  }

  private void addPropertyListener(IEditorPart editorPart) {
    editorPart.addPropertyListener(new IPropertyListener() {
      public void propertyChanged(Object source, int property) {
        if (property == IEditorPart.PROP_DIRTY || property == IWorkbenchPart.PROP_TITLE)
          if (source instanceof IEditorPart)
            fireDirtyProperty();
      }
    });

  }

  void fireDirtyProperty() {
    this.firePropertyChange(IEditorPart.PROP_DIRTY);
  }

  /**
   * Draw the gradient for the specified editor.
   */
  protected void drawGradient(IEditorPart innerEditor, Gradient g) {
    return;
  }

  protected int getIndex(IEditorPart editor) {
    IEditorPart innerEditors[] = getInnerEditors();
    for (int i = 0; i < innerEditors.length; i++) {
      if (innerEditors[i] == editor)
        return i;
    }
    return -1;
  }

  /*
   * @see IEditorPart#init(IEditorSite, IEditorInput)
   */
  public void init(IEditorSite site, MultiEditorInput input) throws PartInitException {
    super.init(site, input);
    String javaInputName = input.getInput()[0].getName();
    String partName = javaInputName.substring(0, javaInputName.length() - 5) + " WOComponent";
    setPartName(partName);
    componentEditorInput = (ComponentEditorInput) input;
    final int initActiveEditorIndex;
    if (componentEditorInput.isCreatedFromDotApi()) {
      initActiveEditorIndex = 3;
    }
    else if (componentEditorInput.isCreatedFromDotHtml()) {
      initActiveEditorIndex = 1;
    }
    else if (componentEditorInput.isCreatedFromDotJava()) {
      initActiveEditorIndex = 0;
    }
    else if (componentEditorInput.isCreatedFromDotWod()) {
      initActiveEditorIndex = 2;
    }
    else if (componentEditorInput.isCreatedFromDotWoo()) {
      initActiveEditorIndex = 1;
    }
    else {
      initActiveEditorIndex = 1;
    }
    Display.getDefault().asyncExec(new Runnable() {
      public void run() {
        setActiveEditorIndex(initActiveEditorIndex);
      }
    });
  }

  /*
   * @see IWorkbenchPart#setFocus()
   */
  public void setFocus() {
    getInnerEditors()[activeEditorIndex].setFocus();
  }

  public void switchToJava() {
    Display.getDefault().asyncExec(new Runnable() {
      public void run() {
        setActiveEditorIndex(0);
        folder.setSelection(0);
        ComponentEditor.this.setFocus();
      }
    });
  }

  public void switchToHtml() {
    Display.getDefault().asyncExec(new Runnable() {
      public void run() {
        setActiveEditorIndex(1);
        folder.setSelection(1);
        ComponentEditor.this.setFocus();
      }
    });
  }

  public void switchToWod() {
    Display.getDefault().asyncExec(new Runnable() {
      public void run() {
        setActiveEditorIndex(2);
        folder.setSelection(1);
        setActiveEditorIndex(2);
        ComponentEditor.this.setFocus();
      }
    });
  }

  public void switchToApi() {
    Display.getDefault().asyncExec(new Runnable() {
      public void run() {
        setActiveEditorIndex(3);
        folder.setSelection(2);
        ComponentEditor.this.setFocus();
      }
    });
  }

}