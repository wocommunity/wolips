/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2007 The ObjectStyle Group 
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
package org.objectstyle.wolips.jdt.ui.tags.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.objectstyle.wolips.jdt.ui.tags.Tag;
import org.objectstyle.wolips.jdt.ui.tags.TagLib;
import org.objectstyle.wolips.workbenchutilities.actions.AbstractActionOnIResources;

/**
 * @author ulrich
 */
public class TagResourcesAction extends AbstractActionOnIResources {

	public void run(IAction action) {
		Map<IProject, List<IResource>> projectFilesMap = new HashMap<IProject, List<IResource>>();
		IResource[] resources = this.getActionResources();
		for (int i = 0; i < resources.length; i++) {
			IResource resource = resources[i];
			List<IResource> resourcesList = projectFilesMap.get(resource.getProject());
			if (resourcesList == null) {
				resourcesList = new ArrayList<IResource>();
				projectFilesMap.put(resource.getProject(), resourcesList);
			}
			resourcesList.add(resource);
		}
		for (Iterator iterator = projectFilesMap.keySet().iterator(); iterator.hasNext();) {
			IProject project = (IProject) iterator.next();
			TagLib tagLib = new TagLib(project);
			TagDialog tagDialog = new TagDialog(this.part.getSite().getShell(), tagLib);
			int status = tagDialog.open();
			if (status != Window.OK) {
				return;
			}
			String tagName = tagDialog.tag;
			if (tagName == null || tagName.length() == 0) {
				return;
			}
			List<IResource> resourcesList = projectFilesMap.get(project);
			String[] componentNames = this.find(new NullProgressMonitor(), resourcesList);
			tagLib.tagComponents(componentNames, tagName);
		}
	}

	protected String[] find(IProgressMonitor monitor, List<IResource> resourcesList) {
		ArrayList<String> components = new ArrayList<String>();
		try {
			for (Iterator iterator = resourcesList.iterator(); iterator.hasNext();) {
				IResource resource = (IResource) iterator.next();
				this.find(resource, components, monitor);
			}

		} catch (CoreException e) {
			e.printStackTrace();
		}
		return components.toArray(new String[components.size()]);
	}

	private void find(IResource resource, ArrayList<String> components, IProgressMonitor monitor) throws CoreException {
		if (resource.getType() == IResource.FILE) {
			return;
		}
		IResource[] members = null;
		if (resource.getType() == IResource.FOLDER) {
			if (resource.getFileExtension() != null && "wo".equals(resource.getFileExtension())) {
				components.add(resource.getName().substring(0, resource.getName().indexOf('.')));
				return;
			}
			members = ((IFolder) resource).members();
		}
		if (resource.getType() == IResource.PROJECT)
			members = ((IProject) resource).members();
		if (members != null) {
			for (int i = 0; i < members.length; i++) {
				this.find(members[i], components, monitor);
			}
		}
	}

	private class TagDialog extends Dialog {

		Text textTag;

		Combo comboTags;

		private TagLib tagLib;
		
		String tag;

		public TagDialog(Shell parentShell, TagLib tagLib) {
			super(parentShell);
			setShellStyle(getShellStyle() | SWT.RESIZE);
			TagDialog.this.tagLib = tagLib;
		}

		protected Point getInitialSize() {
			Point size = super.getInitialSize();
			size.x = 300;
			return size;
		}

		protected Control createDialogArea(Composite parent) {
			getShell().setText("Add Tag");

			Composite composite = new Composite(parent, SWT.NULL);
			composite.setLayoutData(new GridData(GridData.FILL_BOTH));
			composite.setLayout(new GridLayout(2, false));

			Label label = new Label(composite, SWT.NULL);
			label.setText("Existing Tags");

			comboTags = new Combo(composite, SWT.READ_ONLY);
			comboTags.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			Tag[] tags = tagLib.getTags();
			for (int i = 0; i < tags.length; i++) {
				comboTags.add(tags[i].name);
			}
			label = new Label(composite, SWT.NULL);
			label.setText("Tag");

			textTag = new Text(composite, SWT.BORDER);
			textTag.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			
			comboTags.addSelectionListener(new SelectionListener() {

				public void widgetDefaultSelected(SelectionEvent e) {
					//do nothing
				}

				public void widgetSelected(SelectionEvent e) {
					int selected = comboTags.getSelectionIndex();
					if(selected >= 0) {
						textTag.setText(comboTags.getItem(selected));
					}
				}
				
			});

			return composite;
		}

		@Override
		protected void okPressed() {
			tag = textTag.getText();
			super.okPressed();
		}
	}
}
