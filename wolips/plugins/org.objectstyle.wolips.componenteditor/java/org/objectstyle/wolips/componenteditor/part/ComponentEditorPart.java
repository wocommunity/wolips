/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2005 - 2006 The ObjectStyle Group and individual authors of the
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
package org.objectstyle.wolips.componenteditor.part;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.part.MultiPageSelectionProvider;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.objectstyle.wolips.apieditor.editor.ApiEditor;
import org.objectstyle.wolips.componenteditor.ComponenteditorPlugin;
import org.objectstyle.wolips.componenteditor.outline.ComponentEditorOutline;
import org.objectstyle.wolips.components.editor.EditorInteraction;
import org.objectstyle.wolips.components.editor.IEmbeddedEditor;
import org.objectstyle.wolips.components.editor.IEmbeddedEditorSelected;
import org.objectstyle.wolips.components.input.ComponentEditorInput;
import org.objectstyle.wolips.htmleditor.sse.StructuredTextEditorWO;
import org.objectstyle.wolips.htmlpreview.editor.HtmlPreviewEditor;
import org.objectstyle.wolips.wodclipse.WodclipsePlugin;
import org.objectstyle.wolips.wodclipse.wod.WodEditor;

/**
 * @author uli
 */
public class ComponentEditorPart extends MultiPageEditorPart {

	ComponentEditorInput componentEditorInput;

	private EditorInteraction editorInteraction = new EditorInteraction();

	private IEditorPart[] editorParts;

	boolean htmlActive = true;

	private boolean useJavaEditor = false;
	
	protected CompilationUnitEditor compilationUnitEditor;

	protected StructuredTextEditorWO structuredTextEditorWO;

	protected WodEditor wodEditor;

	protected ApiEditor apiEditor;

	protected HtmlPreviewEditor htmlPreviewEditor;

	private ComponentEditorOutline componentEditorOutline;
	private IEditorPart activePart;
	
	public ComponentEditorPart() {
		super();
	}
	
	private int getComponentEditorPageIndex() {
		if(!useJavaEditor) {
			return 1;
		}
		return 0;
	}

	private int pageIndexToEditorPartOffset(int page) {
		if(!useJavaEditor) {
			return page - 1;
		}
		return page;
	}
	
	private int editorPartOffsetToPageIndex(int offset) {
		if(!useJavaEditor) {
			return offset + 1;
		}
		return offset;
	}
	
	private ComponentEditorOutline getComponentEditorOutline() {
		if (componentEditorOutline == null) {
			componentEditorOutline = new ComponentEditorOutline();
		}
		return componentEditorOutline;
	}

	public Object getAdapter(Class adapter) {
		if (adapter.equals(IContentOutlinePage.class)) {
			return this.getComponentEditorOutline();
		}
		return super.getAdapter(adapter);
	}

	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);
		componentEditorInput = (ComponentEditorInput) input;
		if (input != null) {
			String inputName = input.getName();
			String partName = inputName.substring(0, inputName.indexOf("."));
			setPartName(partName);
		}
		site.setSelectionProvider(new ComponentEditorPartSelectionProvider(this));
	}
	
	public Object getJavaFile() {
		return ((IFileEditorInput)componentEditorInput.getInput()[0]).getFile();
	}

	public IEditorInput getEditorInput() {
		if (componentEditorInput == null) {
			return super.getEditorInput();
		}
		return activePart.getEditorInput();
	}

	public Composite createInnerPartControl(Composite parent, final IEditorPart e) {
		Composite content = new Composite(parent, SWT.NONE);
		content.setLayout(new FillLayout());
		e.createPartControl(content);
		return content;
	}

	protected void createPages() {
		if (componentEditorInput == null) {
			return;
		}
		IEditorInput[] editorInput = componentEditorInput.getInput();
		editorParts = new IEditorPart[editorInput.length + 1];
		Composite componentEditorParent = new Composite(getContainer(), SWT.NONE);
		componentEditorParent.setLayout(new FillLayout());

		SashForm componentEditorSashParent = new SashForm(componentEditorParent, SWT.VERTICAL | SWT.SMOOTH);
		SashForm htmlSashform = new SashForm(componentEditorSashParent, SWT.VERTICAL);
		htmlSashform.addListener(SWT.Activate, new Listener() {
			public void handleEvent(Event event) {
				htmlActive = true;
				pageChange(pageIndexToEditorPartOffset(1));
				ComponentEditorPart.this.updateOutline();
			}
		});
		SashForm wodSashform = new SashForm(componentEditorSashParent, SWT.VERTICAL);
		wodSashform.addListener(SWT.Activate, new Listener() {
			public void handleEvent(Event event) {
				htmlActive = false;
				pageChange(pageIndexToEditorPartOffset(1));
				ComponentEditorPart.this.updateOutline();
			}
		});
		IFileEditorInput html = null;
		
		for (int i = 0; i < componentEditorInput.getEditors().length; i++) {
			IEditorPart editorPart = null;
			IFileEditorInput input = (IFileEditorInput) editorInput[i];
			String extension = input.getFile().getFileExtension();
			if("java".equals(extension)) {
				compilationUnitEditor = new CompilationUnitEditor();
				if(useJavaEditor) {
					editorPart = compilationUnitEditor;
					try {
						this.addPage(editorPart, input);
						this.setPageText(getPageCount() - 1, "Java");
					} catch (PartInitException e) {
						ComponenteditorPlugin.getDefault().log(e);
					}
				} else {
					// compilationUnitEditor.setInput(input);
				}
			} else if("html".equals(extension)) {
				structuredTextEditorWO = new StructuredTextEditorWO();
				editorPart = structuredTextEditorWO;
				IEditorSite htmlSite = createSite(editorPart);
				html = input;
				try {
					editorPart.init(htmlSite, input);
				} catch (PartInitException e) {
					ComponenteditorPlugin.getDefault().log(e);
				}
				createInnerPartControl(htmlSashform, editorPart);
				editorPart.addPropertyListener(new IPropertyListener() {
					public void propertyChanged(Object source, int propertyId) {
						ComponentEditorPart.this.handlePropertyChange(propertyId);
					}
				});
			} else if("wod".equals(extension)) {
				wodEditor = new WodEditor();
				editorPart = wodEditor;
				IEditorSite wodSite = createSite(editorPart);
				try {
					editorPart.init(wodSite, input);
				} catch (PartInitException e) {
					ComponenteditorPlugin.getDefault().log(e);
				}
				createInnerPartControl(wodSashform, editorPart);
				editorPart.addPropertyListener(new IPropertyListener() {
					public void propertyChanged(Object source, int propertyId) {
						ComponentEditorPart.this.handlePropertyChange(propertyId);
					}
				});
				this.addPage(componentEditorParent);
				this.setPageText(getPageCount() - 1, "Component");
				wodEditor.getSelectionProvider().addSelectionChangedListener(
						new ISelectionChangedListener() {

							public void selectionChanged(SelectionChangedEvent event) {
								WodclipsePlugin.getDefault().updateWebObjectsTagNames(null);
							}

						});
				WodclipsePlugin.getDefault().updateWebObjectsTagNames(wodEditor);
				// AK FIXME: doesn't work, spews out NPE later on
				htmlPreviewEditor = new HtmlPreviewEditor();
				try {
					this.addPage(htmlPreviewEditor, html);
					this.setPageText(getPageCount() - 1, "Preview");
				} catch (PartInitException e) {
					ComponenteditorPlugin.getDefault().log(e);
				}

			} else if("api".equals(extension)) {
				apiEditor = new ApiEditor();
				editorPart = apiEditor;
				try {
					this.addPage(editorPart, input);
				} catch (PartInitException e) {
					ComponenteditorPlugin.getDefault().log(e);
				}
				this.setPageText(getPageCount() - 1, "Api");
			}
			if(editorPart != null) {
				int offset = getPageCount();
				editorParts[offset] = editorPart;
				if (editorPart instanceof IEmbeddedEditor) {
					IEmbeddedEditor embeddedEditor = (IEmbeddedEditor) editorPart;
					embeddedEditor.initEditorInteraction(editorInteraction);
				}
			}
		}

		addWebObjectsTagNamesListener();

		CTabFolder tabFolder = (CTabFolder) this.getContainer();
		tabFolder.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				ComponentEditorPart.this.updateOutline();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				return;
			}

		});
		if (componentEditorInput.isDisplayJavaPartOnReveal()) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					ComponentEditorPart.this.updateOutline();
				}
			});
		}
		if (componentEditorInput.isDisplayHtmlPartOnReveal()) {
			this.switchToHtml();
		} else if (componentEditorInput.isDisplayWodPartOnReveal()) {
			this.switchToWod();
		} else if (componentEditorInput.isDisplayWooPartOnReveal()) {
			this.switchToWod();
		} else if (componentEditorInput.isDisplayApiPartOnReveal()) {
			this.switchToApi();
		}
		return;
	}

	private void addWebObjectsTagNamesListener() {
		structuredTextEditorWO.getSelectionProvider()
				.addSelectionChangedListener(new ISelectionChangedListener() {

					public void selectionChanged(SelectionChangedEvent event) {
						WodclipsePlugin.getDefault().updateWebObjectsTagNames(
								null);
					}

				});
		wodEditor.getSelectionProvider().addSelectionChangedListener(
				new ISelectionChangedListener() {

					public void selectionChanged(SelectionChangedEvent event) {
						WodclipsePlugin.getDefault().updateWebObjectsTagNames(
								wodEditor);
					}

				});
		WodclipsePlugin.getDefault().updateWebObjectsTagNames(wodEditor);
	}

	public void doSave(IProgressMonitor monitor) {
		for (int i = 0; i < editorParts.length; i++) {
			if (editorParts[i] != null && editorParts[i].isDirty()) {
				editorParts[i].doSave(monitor);
			}
		}
		return;
	}

	public void doSaveAs() {
		assert false;
		return;
	}

	public boolean isSaveAsAllowed() {
		return false;
	}

	public void setHtmlActive(boolean htmlActive) {
		this.htmlActive = htmlActive;
	}

	public void updateOutline() {
		IEditorPart editorPart = this.getActiveEditor();
		if (editorPart != null) {
			IContentOutlinePage contentOutlinePage = (IContentOutlinePage) editorPart
					.getAdapter(IContentOutlinePage.class);
			this.getComponentEditorOutline().setPageActive(contentOutlinePage);
		}
	}

	public boolean isDirty() {
		if (super.isDirty()) {
			return true;
		}
		if (structuredTextEditorWO.isDirty()) {
			return true;
		}
		if (wodEditor.isDirty()) {
			return true;
		}
		return false;
	}

	public void switchToJava() {
		switchToPage(pageIndexToEditorPartOffset(0));
	}

	public void switchToHtml() {
		setHtmlActive(true);
		switchToPage(pageIndexToEditorPartOffset(1));
	}

	public void switchToWod() {
		setHtmlActive(false);
		switchToPage(pageIndexToEditorPartOffset(1));
	}

	public void switchToPreview() {
		switchToPage(pageIndexToEditorPartOffset(2));
	}

	public void switchToApi() {
		if (apiEditor == null) {
			return;
		}
		switchToPage(pageIndexToEditorPartOffset(3));
	}

	public void switchToPage(int page) {
		this.setActivePage(page);
		setFocus();
	}

	protected void pageChange(int newPageIndex) {	
		super.pageChange(newPageIndex);
		activePart = getEditor(newPageIndex);
		if (activePart != null) {
			if (activePart instanceof IEmbeddedEditorSelected) {
				IEmbeddedEditorSelected embeddedEditorPageChanged = (IEmbeddedEditorSelected) activePart;
				embeddedEditorPageChanged.editorSelected();
			}
		}
	}
	
	public ComponentEditorInput getComponentEditorInput() {
		return componentEditorInput;
	}

	protected IEditorPart getEditor(int pageIndex) {
		IEditorPart part = editorParts[pageIndex];
		if (pageIndex == pageIndexToEditorPartOffset(0)) {
			return compilationUnitEditor;
		}
		if (pageIndex == pageIndexToEditorPartOffset(1)) {
			if (this.htmlActive) {
				return this.structuredTextEditorWO;
			}
			return this.wodEditor;
		}
		if (pageIndex == pageIndexToEditorPartOffset(2)) {
			return htmlPreviewEditor;
		}
		if (pageIndex == pageIndexToEditorPartOffset(3)) {
			return apiEditor;
		}
		System.out.println("Huh?");
		if (pageIndex > pageIndexToEditorPartOffset(1)) {
			return super.getEditor(pageIndex + 1);
		}
		return super.getEditor(pageIndex);
	}

	protected IEditorPart getActiveEditor() {
		if (this.getActivePage() == getComponentEditorPageIndex()) {
			if (this.htmlActive) {
				return this.structuredTextEditorWO;
			}
			return this.wodEditor;
		}
		return super.getActiveEditor();
	}
	
	private static class ComponentEditorPartSelectionProvider extends
			MultiPageSelectionProvider {
		private ISelection globalSelection;
		
		public ComponentEditorPartSelectionProvider(ComponentEditorPart componentEditorPart) {
			super(componentEditorPart);
		}

		public ISelection getSelection() {
			IEditorPart activeEditor = ((ComponentEditorPart) getMultiPageEditor()).getActiveEditor();
			if (activeEditor != null) {
				ISelectionProvider selectionProvider = activeEditor.getSite().getSelectionProvider();
				if (selectionProvider != null) {
					return selectionProvider.getSelection();
				}
			}
			return globalSelection;
		}
		
		public void setSelection(ISelection selection) {
			IEditorPart activeEditor = ((ComponentEditorPart) getMultiPageEditor()).getActiveEditor();
			if (activeEditor != null) {
				ISelectionProvider selectionProvider = activeEditor.getSite().getSelectionProvider();
				if (selectionProvider != null) {
					selectionProvider.setSelection(selection);
				}
			} else {
				this.globalSelection = selection;
				fireSelectionChanged(new SelectionChangedEvent(this, globalSelection));
			}
		}
	}
}
