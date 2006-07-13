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

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;
import org.eclipse.ui.texteditor.ITextEditor;

public class EOModelEditorContributor extends MultiPageEditorActionBarContributor {
  private IEditorPart myActiveEditorPart;

  //private NewEntityAction myNewEntityAction;

  public EOModelEditorContributor() {
    createActions();
  }

  /**
   * Returns the action registed with the given text editor.
   * @return IAction or null if editor is null.
   */
  protected IAction getAction(ITextEditor _editor, String _actionID) {
    return (_editor == null ? null : _editor.getAction(_actionID));
  }

  public void setActiveEditor(IEditorPart _part) {
    //System.out.println("EOModelEditorContributor.setActiveEditor: " + _part);
    super.setActiveEditor(_part);
  }

  public void setActivePage(IEditorPart _editor) {
    //System.out.println("EOModelEditorContributor.setActivePage: " + _editor);
    if (myActiveEditorPart == _editor) {
      return;
    }

    myActiveEditorPart = _editor;
    //myNewEntityAction.setActiveEditor(_editor);
    //    IActionBars actionBars = getActionBars();
    //    if (actionBars != null) {
    //      ITextEditor editor = (_part instanceof ITextEditor) ? (ITextEditor) _part : null;
    //      actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(), getAction(editor, ITextEditorActionConstants.DELETE));
    //      actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(), getAction(editor, ITextEditorActionConstants.UNDO));
    //      actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(), getAction(editor, ITextEditorActionConstants.REDO));
    //      actionBars.setGlobalActionHandler(ActionFactory.CUT.getId(), getAction(editor, ITextEditorActionConstants.CUT));
    //      actionBars.setGlobalActionHandler(ActionFactory.COPY.getId(), getAction(editor, ITextEditorActionConstants.COPY));
    //      actionBars.setGlobalActionHandler(ActionFactory.PASTE.getId(), getAction(editor, ITextEditorActionConstants.PASTE));
    //      actionBars.setGlobalActionHandler(ActionFactory.SELECT_ALL.getId(), getAction(editor, ITextEditorActionConstants.SELECT_ALL));
    //      actionBars.setGlobalActionHandler(ActionFactory.FIND.getId(), getAction(editor, ITextEditorActionConstants.FIND));
    //      actionBars.setGlobalActionHandler(IDEActionFactory.BOOKMARK.getId(), getAction(editor, IDEActionFactory.BOOKMARK.getId()));
    //      actionBars.updateActionBars();
    //    }
  }

  private void createActions() {
    //System.out.println("EOModelEditorContributor.createActions: ");
    //    myNewEntityAction = new NewEntityAction();
    //    myNewEntityAction.setToolTipText("Add a New Entity");
    //    myNewEntityAction.setText("New Entity");
    //    myNewEntityAction.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Activator.EOENTITY_ICON));
  }

  public void contributeToMenu(IMenuManager _manager) {
    //    IMenuManager menu = new MenuManager("EO&Modeler");
    //    _manager.prependToGroup(IWorkbenchActionConstants.MB_ADDITIONS, menu);
    //    menu.add(myNewEntityAction);
  }

  public void contributeToToolBar(IToolBarManager _manager) {
    //    _manager.add(new Separator());
    //    _manager.add(myNewEntityAction);
  }
}
