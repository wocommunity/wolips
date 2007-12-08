/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2005 The ObjectStyle Group,
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

package org.objectstyle.wolips.wooeditor.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.objectstyle.wolips.wooeditor.model.DisplayGroup;
import org.objectstyle.wolips.wooeditor.model.WooModel;

public class DisplayGroupPageBlock extends MasterDetailsBlock {
	private class AddButtonListener implements SelectionListener {
		public void widgetDefaultSelected(final SelectionEvent e) {
			// Do nothing
		}

		public void widgetSelected(final SelectionEvent e) {
			try {
				WooEditor wooEditor = (WooEditor) page.getEditor();
				int nextId = 1;
				for (DisplayGroup dg : wooEditor.getModel().getDisplayGroups()) {
					if (dg.getName().matches("^displayGroup[0-9]*$")) {
						String index = dg.getName().replaceAll("^displayGroup([0-9]*)$", "$1");
						int j = new Integer(index);
						if (nextId <= j) {
							nextId = j + 1;
						}
					}
				}

				wooEditor.getModel().createDisplayGroup("displayGroup" + nextId);
				viewer.refresh();
				int count = viewer.getTable().getItemCount();
				Object element = viewer.getElementAt(count - 1);
				viewer.editElement(element, count - 1);
				myManagedForm.dirtyStateChanged();
			} catch (Throwable tx) {
				throw new RuntimeException("Failed to open .woo file.", tx);
			}
		}
	}

	protected class MasterContentProvider implements IStructuredContentProvider {
		public void dispose() {
			// nothing to do
		}

		public Object[] getElements(final Object inputElement) {
			try {
				if (inputElement instanceof IEditorInput) {
					WooEditor wooEditor = (WooEditor) page.getEditor();
					DisplayGroup[] displayGroups = wooEditor.getModel()
							.getDisplayGroups();
					return displayGroups;
				}
			} catch (Throwable t) { }
			return new Object[0];
			
		}

		public void inputChanged(final Viewer inViewer, final Object oldInput,
				final Object newInput) {
			// nothing to do
		}
	}

	protected static class MasterLabelProvider extends LabelProvider implements
			ITableLabelProvider {
		public Image getColumnImage(final Object obj, final int index) {
			if (obj instanceof DisplayGroup) {
				return PlatformUI.getWorkbench().getSharedImages().getImage(
						ISharedImages.IMG_OBJ_ELEMENT);
			}
			return null;
		}

		public String getColumnText(final Object obj, final int index) {
			if (obj instanceof DisplayGroup) {
				DisplayGroup displayGroup = (DisplayGroup) obj;
				return displayGroup.getName();
			}
			return obj.toString();
		}
	}

	private class RemoveButtonListener implements SelectionListener {
		public void widgetDefaultSelected(final SelectionEvent e) {
			// Do nothing
		}

		public void widgetSelected(final SelectionEvent e) {
			try {
				WooEditor wooEditor = (WooEditor) page.getEditor();
				StructuredSelection selection = (StructuredSelection) viewer
						.getSelection();
				int selectionIndex = viewer.getTable().getSelectionIndex();
				for (Object displayGroup : selection.toList()) {
					wooEditor.getModel().removeDisplayGroup(
							(DisplayGroup) displayGroup);
				}
				viewer.refresh();
				int count = viewer.getTable().getItemCount();
				if (count <= selectionIndex) {
					selectionIndex = count - 1;
				}
				Object element = viewer.getElementAt(selectionIndex);
				if (element != null)
					viewer.editElement(element, selectionIndex);
				myManagedForm.dirtyStateChanged();
			} catch (Throwable tx) {
				tx.printStackTrace();
				throw new RuntimeException("Failed to open .woo file.", tx);
			}
		}
	}

	private FormPage page;

	private TableViewer viewer;

	private IManagedForm myManagedForm;

	public DisplayGroupPageBlock(final FormPage formPage) {
		this.page = formPage;
	}

	@Override
	public void createContent(IManagedForm managedForm) {
		super.createContent(managedForm);

		final PropertyChangeListener displayGroupListener =
			new PropertyChangeListener() {
			public void propertyChange(
					final PropertyChangeEvent event) {
				if (WooModel.DISPLAY_GROUP_NAME.equals(event
						.getPropertyName())) {
					viewer.update(event.getSource(), null);
				}
			}
		};
		final WooEditor wooEditor = (WooEditor) page.getEditor();
		final WooModel wooModel = wooEditor.getModel();
		wooModel.addPropertyChangeListener(displayGroupListener);
	}

	@Override
	protected void createMasterPart(final IManagedForm managedForm,
			final Composite parent) {
		myManagedForm = managedForm;
		FormToolkit toolkit = managedForm.getToolkit();
		Section section = toolkit.createSection(parent, Section.DESCRIPTION);

		section.setText("Display Groups");
		section.setDescription("The list contains display groups from the "
				+ "component whose details are editable on the right");
		section.marginWidth = 10;
		section.marginHeight = 5;
		toolkit.createCompositeSeparator(section);

		Composite client = toolkit.createComposite(section, SWT.WRAP);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		client.setLayout(layout);

		Table t = toolkit.createTable(client, SWT.NULL);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = 20;
		gd.widthHint = 80;
		gd.horizontalSpan = 2;
		t.setLayoutData(gd);
		toolkit.paintBordersFor(client);

		gd = new GridData(SWT.NONE, SWT.END, false, false);
		Button addButton = toolkit.createButton(client, "Add", SWT.PUSH);
		addButton.setLayoutData(gd);
		addButton.addSelectionListener(new AddButtonListener());
		Button removeButton = toolkit.createButton(client, "Remove", SWT.PUSH);
		removeButton.setLayoutData(gd);
		removeButton.addSelectionListener(new RemoveButtonListener());

		section.setClient(client);
		final SectionPart spart = new SectionPart(section);
		managedForm.addPart(spart);

		viewer = new TableViewer(t);
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(final SelectionChangedEvent event) {
				managedForm.fireSelectionChanged(spart, event.getSelection());
			}
		});
		viewer.setContentProvider(new MasterContentProvider());
		viewer.setLabelProvider(new MasterLabelProvider());
		viewer.setInput(page.getEditor().getEditorInput());
	}

	@Override
	protected void createToolBarActions(final IManagedForm managedForm) {
		// nothing to do
	}

	public SashForm getParentSashForm() {
		return sashForm;
	}

	@Override
	protected void registerPages(final DetailsPart details) {
		details.registerPage(DisplayGroup.class, new DisplayGroupDetailsPage());
	}

	public void refresh() {
		viewer.refresh();
	}

	public void update() {
		viewer.update(viewer.getSelection(), null);
	}

}
