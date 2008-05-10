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

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.objectstyle.wolips.baseforplugins.util.StringUtils;
import org.objectstyle.wolips.bindings.api.ApiModelException;
import org.objectstyle.wolips.bindings.api.Binding;
import org.objectstyle.wolips.bindings.api.Binding.BindingChangedListener;

public class BindingsPageBlock extends MasterDetailsBlock implements BindingChangedListener {
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
						binding.setBindingChangedListener(BindingsPageBlock.this);
					}
					return bindings.toArray();
				}
				return new Object[0];
			} catch (Throwable t) {
				throw new RuntimeException("Failed to open .api file.", t);
			}
		}

		public void dispose() {
			try {
				ApiEditor apiEditor = (ApiEditor) page.getEditor();
				List<Binding> bindings = apiEditor.getModel().getWODefinitions().getWo().getBindings();
				for (Binding binding : bindings) {
					binding.setBindingChangedListener(null);
				}
			} catch (Throwable t) {
				throw new RuntimeException("Failed to dispose api file.", t);
			}
		}

		public void inputChanged(Viewer inViewer, Object oldInput, Object newInput) {
			// nothing to do
		}
	}

	class MasterLabelProvider extends LabelProvider implements ITableLabelProvider, ITableFontProvider {
		@Override
		public String getText(Object element) {
			return getColumnText(element, 0);
		}

		@Override
		public void dispose() {
			super.dispose();
		}

		public Font getFont(Object element, int columnIndex) {
			Font font = null;
			if (element instanceof Binding) {
				Binding binding = (Binding) element;
				if (binding.isRequired()) {
					font = JFaceResources.getFontRegistry().getBold(JFaceResources.DIALOG_FONT);
				}
			}
			return font;
		}

		public String getColumnText(Object obj, int index) {
			return obj.toString();
		}

		public Image getColumnImage(Object obj, int index) {
			// if (obj instanceof Binding) {
			// return
			// PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
			// }
			return null;
		}
	}

	@Override
	public void createContent(final IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		FormToolkit toolkit = managedForm.getToolkit();

		Section apiSection = toolkit.createSection(form.getBody(), ExpandableComposite.TITLE_BAR);
		apiSection.setText("Component API");
		// apiSection.setDescription("Yep");
		apiSection.marginWidth = 10;
		apiSection.marginHeight = 10;
		// toolkit.createCompositeSeparator(apiSection);

		Composite apiClient = toolkit.createComposite(apiSection, SWT.WRAP);
		GridLayout apiClientLayout = new GridLayout();
		apiClientLayout.numColumns = 1;
		apiClientLayout.marginWidth = 2;
		apiClientLayout.marginHeight = 0;
		apiClientLayout.marginBottom = 10;
		apiClient.setLayout(apiClientLayout);

		Button componentContentButton = toolkit.createButton(apiClient, "Component Content", SWT.CHECK);
		ApiEditor apiEditor = (ApiEditor) page.getEditor();
		try {
			componentContentButton.setSelection(apiEditor.getModel().getWo().isComponentContent());
		} catch (ApiModelException e) {
			throw new RuntimeException("Failed to open .api file.", e);
		}
		componentContentButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent event) {
				widgetSelected(event);
			}

			public void widgetSelected(SelectionEvent event) {
				Button button = (Button) event.widget;
				try {
					@SuppressWarnings("hiding")
					ApiEditor apiEditor = (ApiEditor) page.getEditor();
					apiEditor.getModel().getWo().setComponentContent(button.getSelection());
					managedForm.dirtyStateChanged();
				} catch (ApiModelException e) {
					throw new RuntimeException("Failed to open .api file.", e);
				}
			}
		});

		toolkit.paintBordersFor(apiClient);

		apiSection.setClient(apiClient);

		super.createContent(managedForm);
	}

	protected void createMasterPart(final IManagedForm managedForm, Composite parent) {
		// final ScrolledForm form = managedForm.getForm();
		FormToolkit toolkit = managedForm.getToolkit();

		Section bindingsSection = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR);
		bindingsSection.setText("Bindings");
		bindingsSection.setDescription("The list contains bindings from the component whose details are editable on the right");
		bindingsSection.marginWidth = 10;
		bindingsSection.marginHeight = 5;
		// toolkit.createCompositeSeparator(bindingsSection);
		Composite bindingsClient = toolkit.createComposite(bindingsSection, SWT.WRAP);
		GridLayout bindingsClientLayout = new GridLayout();
		bindingsClientLayout.numColumns = 2;
		bindingsClientLayout.marginWidth = 2;
		bindingsClientLayout.marginHeight = 2;
		bindingsClient.setLayout(bindingsClientLayout);
		Table bindingsTable = toolkit.createTable(bindingsClient, SWT.FULL_SELECTION);
		GridData bindingTableData = new GridData(GridData.FILL_BOTH);
		bindingTableData.heightHint = 20;
		bindingTableData.widthHint = 100;
		bindingsTable.setLayoutData(bindingTableData);
		toolkit.paintBordersFor(bindingsClient);

		Composite buttonsGroup = new Composite(bindingsClient, SWT.NONE);
		GridData buttonsLayoutData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		buttonsGroup.setLayoutData(buttonsLayoutData);
		buttonsGroup.setBackground(bindingsClient.getBackground());
		RowLayout buttonsLayout = new RowLayout(SWT.VERTICAL);
		buttonsLayout.fill = true;
		buttonsLayout.justify = true;
		buttonsLayout.marginTop = 0;
		buttonsLayout.marginRight = 0;
		buttonsLayout.marginBottom = 0;
		buttonsLayout.marginLeft = 0;
		buttonsGroup.setLayout(buttonsLayout);

		Button addButton = toolkit.createButton(buttonsGroup, "Add", SWT.PUSH);
		addButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				try {
					ApiEditor apiEditor = (ApiEditor) page.getEditor();
					String newBindingName = StringUtils.findUnusedName("newBinding", apiEditor.getModel().getWo(), "getBinding");
					Binding newBinding = apiEditor.getModel().getWo().createBinding(newBindingName);
					viewer.refresh();
					viewer.editElement(newBinding, 0);
					managedForm.dirtyStateChanged();
				} catch (Throwable tx) {
					throw new RuntimeException("Failed to open .api file.", tx);
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				// nothing to do
			}

		});

		Button removeButton = toolkit.createButton(buttonsGroup, "Remove", SWT.PUSH);
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

		bindingsSection.setClient(bindingsClient);
		final SectionPart spart = new SectionPart(bindingsSection) {
			@Override
			public boolean isDirty() {
				try {
					return ((ApiEditor) page.getEditor()).getModel().isDirty();
				} catch (ApiModelException e) {
					return false;
				}
			}
		};
		managedForm.addPart(spart);
		viewer = new TableViewer(bindingsTable);
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				managedForm.fireSelectionChanged(spart, event.getSelection());
			}
		});
		viewer.setContentProvider(new MasterContentProvider());
		viewer.setSorter(new ViewerSorter());
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

	public void bindingChanged(Binding binding) {
		viewer.update(binding, null);
	}
}