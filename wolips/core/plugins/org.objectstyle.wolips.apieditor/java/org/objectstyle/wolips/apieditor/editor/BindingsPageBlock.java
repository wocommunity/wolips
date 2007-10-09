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
package org.objectstyle.wolips.apieditor.editor;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
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
import org.objectstyle.wolips.bindings.api.Binding;
import org.objectstyle.wolips.bindings.api.Binding.BindingNameChangedListener;

public class BindingsPageBlock extends MasterDetailsBlock implements BindingNameChangedListener {
	FormPage page;

	TableViewer viewer;

	public BindingsPageBlock(FormPage page) {
		this.page = page;
	}

	/**
	 * @param id
	 * @param title
	 */
	class MasterContentProvider implements IStructuredContentProvider {
		public Object[] getElements(Object inputElement) {
			try {
				if (inputElement instanceof IEditorInput) {
					ApiEditor apiEditor = (ApiEditor) page.getEditor();
					List<Binding> bindings = apiEditor.getModel().getWODefinitions().getWo().getBindings();
					for (Binding binding : bindings) {
						binding.setBindingNameChangedListener(BindingsPageBlock.this);
					}
					return bindings.toArray();
				}
				return new Object[0];
			} catch (Throwable t) {
				throw new RuntimeException("Failed to open .api file.", t);
			}
		}

		public void dispose() {
			// nothing to do
		}

		public void inputChanged(Viewer inViewer, Object oldInput, Object newInput) {
			// nothing to do
		}
	}

	class MasterLabelProvider extends LabelProvider implements ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			return obj.toString();
		}

		public Image getColumnImage(Object obj, int index) {
			if (obj instanceof Binding) {
				return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
			}
			return null;
		}
	}

	protected void createMasterPart(final IManagedForm managedForm, Composite parent) {
		// final ScrolledForm form = managedForm.getForm();
		FormToolkit toolkit = managedForm.getToolkit();
		Section section = toolkit.createSection(parent, Section.DESCRIPTION);
		section.setText("Bindings");
		section.setDescription("The list contains bindings from the component whose details are editable on the right");
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
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 20;
		gd.widthHint = 100;
		t.setLayoutData(gd);
		toolkit.paintBordersFor(client);

		Button addButton = toolkit.createButton(client, "Add", SWT.PUSH);
		gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		addButton.setLayoutData(gd);
		addButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				try {
					ApiEditor apiEditor = (ApiEditor) page.getEditor();
					apiEditor.getModel().getWo().createBinding("Foo");
					viewer.refresh();
					int count = viewer.getTable().getItemCount();
					Object element = viewer.getElementAt(count - 1);
					viewer.editElement(element, count - 1);
					managedForm.dirtyStateChanged();
				} catch (Throwable tx) {
					throw new RuntimeException("Failed to open .api file.", tx);
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				// nothing to do
			}

		});

		Button removeButton = toolkit.createButton(client, "Remove", SWT.PUSH);
		gd = new GridData(GridData.VERTICAL_ALIGN_END);
		removeButton.setLayoutData(gd);
		removeButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				try {
					IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
					if (!selection.isEmpty()) {
						Iterator iterator = selection.iterator();
						ApiEditor apiEditor = (ApiEditor) page.getEditor();
						while (iterator.hasNext()) {
							Binding binding = (Binding) iterator.next();
							apiEditor.getModel().getWo().removeBinding(binding);
							viewer.remove(binding);
						}
						managedForm.dirtyStateChanged();
					}
				} catch (Throwable tx) {
					throw new RuntimeException("Failed to open .api file.", tx);
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				// nothing to do
			}

		});

		section.setClient(client);
		final SectionPart spart = new SectionPart(section);
		managedForm.addPart(spart);
		viewer = new TableViewer(t);
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				managedForm.fireSelectionChanged(spart, event.getSelection());
			}
		});
		viewer.setContentProvider(new MasterContentProvider());
		viewer.setLabelProvider(new MasterLabelProvider() {

			public String getColumnText(Object obj, int index) {
				if (obj instanceof Binding) {
					Binding binding = (Binding) obj;
					return binding.getName();
				}
				return super.getColumnText(obj, index);
			}

		});
		viewer.setInput(page.getEditor().getEditorInput());
	}

	public SashForm getParentSashForm() {
		return sashForm;
	}

	protected void createToolBarActions(IManagedForm managedForm) {
		// nothing to do
	}

	protected void registerPages(DetailsPart details) {
		details.registerPage(Binding.class, new BindingDetailsPage());
	}

	public void namedChanged(Binding binding) {
		viewer.update(binding, null);

	}
}