/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2002 - 2006 The ObjectStyle Group 
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

package org.objectstyle.wolips.ui.view;

import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.progress.IProgressConstants;

/**
 * @author uli
 */
public class PerspectiveFactory implements IPerspectiveFactory {
	private static final String ID_WIZARDS_ACTION_SET = "org.objectstyle.wolips.wizards.ElementCreationActionSet";

	public static final String ID_RELATED = "org.objectstyle.wolips.ui.view.RelatedView";

	public static final String ID_BINDINGS = "org.objectstyle.wolips.componenteditor.inspector.BindingsInspectorPageBookView";

	public static final String ID_WO_PACKAGES = "org.objectstyle.wolips.jdt.ui.WOPackageExplorer";

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();

		IFolderLayout leftFolder = layout.createFolder("left", IPageLayout.LEFT, (float) 0.25, editorArea); //$NON-NLS-1$
		leftFolder.addView(PerspectiveFactory.ID_WO_PACKAGES);
		leftFolder.addView(JavaUI.ID_TYPE_HIERARCHY);
		leftFolder.addPlaceholder(IPageLayout.ID_RES_NAV);
		
		IFolderLayout leftBottomFolder = layout.createFolder("left_bottom", IPageLayout.BOTTOM, (float) 0.65, "left");
		leftBottomFolder.addView(PerspectiveFactory.ID_RELATED);
		
		IFolderLayout bottomFolder = layout.createFolder("bottom", IPageLayout.BOTTOM, (float) 0.75, editorArea); //$NON-NLS-1$
		bottomFolder.addView(IPageLayout.ID_PROBLEM_VIEW);
		bottomFolder.addView(PerspectiveFactory.ID_BINDINGS);
		bottomFolder.addView(IConsoleConstants.ID_CONSOLE_VIEW);
		bottomFolder.addView(JavaUI.ID_JAVADOC_VIEW);
		bottomFolder.addView(JavaUI.ID_SOURCE_VIEW);
		bottomFolder.addPlaceholder(NewSearchUI.SEARCH_VIEW_ID);
		bottomFolder.addPlaceholder(IConsoleConstants.ID_CONSOLE_VIEW);
		bottomFolder.addPlaceholder(IPageLayout.ID_BOOKMARKS);
		bottomFolder.addPlaceholder(IProgressConstants.PROGRESS_VIEW_ID);

		layout.addView(IPageLayout.ID_OUTLINE, IPageLayout.RIGHT, (float) 0.75, editorArea);

		layout.addActionSet(IDebugUIConstants.LAUNCH_ACTION_SET);
		layout.addActionSet(JavaUI.ID_ACTION_SET);
		layout.addActionSet(JavaUI.ID_ELEMENT_CREATION_ACTION_SET);
		layout.addActionSet(PerspectiveFactory.ID_WIZARDS_ACTION_SET);
		layout.addActionSet(IPageLayout.ID_NAVIGATE_ACTION_SET);

		// views - java
		layout.addShowViewShortcut(JavaUI.ID_PACKAGES);
		layout.addShowViewShortcut(JavaUI.ID_TYPE_HIERARCHY);
		layout.addShowViewShortcut(JavaUI.ID_SOURCE_VIEW);
		layout.addShowViewShortcut(JavaUI.ID_JAVADOC_VIEW);

		// views - search
		layout.addShowViewShortcut(NewSearchUI.SEARCH_VIEW_ID);

		// views - debugging
		layout.addShowViewShortcut(IConsoleConstants.ID_CONSOLE_VIEW);

		// views - standard workbench
		layout.addShowViewShortcut(IPageLayout.ID_OUTLINE);
		layout.addShowViewShortcut(IPageLayout.ID_PROBLEM_VIEW);
		layout.addShowViewShortcut(IPageLayout.ID_RES_NAV);
		// views - wolips
		layout.addShowViewShortcut(PerspectiveFactory.ID_RELATED);

		// new actions - Java project creation wizard
		layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewPackageCreationWizard"); //$NON-NLS-1$
		layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewClassCreationWizard"); //$NON-NLS-1$
		layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewInterfaceCreationWizard"); //$NON-NLS-1$
		layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewEnumCreationWizard"); //$NON-NLS-1$
		layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewAnnotationCreationWizard"); //$NON-NLS-1$
		layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewSourceFolderCreationWizard"); //$NON-NLS-1$
		layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewSnippetFileCreationWizard"); //$NON-NLS-1$
		layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.folder");//$NON-NLS-1$
		layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.file");//$NON-NLS-1$
		layout.addNewWizardShortcut("org.eclipse.ui.editors.wizards.UntitledTextFileWizard");//$NON-NLS-1$
		layout.addNewWizardShortcut("org.objectstyle.wolips.wizards.new.wo.5.1.application"); //$NON-NLS-1$
		layout.addNewWizardShortcut("org.objectstyle.wolips.wizards.new.wo.5.1.d2wapplication"); //$NON-NLS-1$
		layout.addNewWizardShortcut("org.objectstyle.wolips.wizards.new.wo.5.1.framework"); //$NON-NLS-1$
		layout.addNewWizardShortcut("org.objectstyle.wolips.wizards.new.wo.5.1.jarproject"); //$NON-NLS-1$
		layout.addNewWizardShortcut("org.objectstyle.wolips.wizards.new.wo.5.1.subproject"); //$NON-NLS-1$
		layout.addNewWizardShortcut("org.objectstyle.wolips.wizards.new.wo.5.1.eomodel"); //$NON-NLS-1$
		layout.addNewWizardShortcut("org.objectstyle.wolips.wizards.new.wo.5.1.component"); //$NON-NLS-1$
	}

}
