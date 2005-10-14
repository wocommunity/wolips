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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
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
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.wst.html.ui.internal.provisional.StructuredTextEditorHTML;
import org.eclipse.wst.sse.ui.internal.contentoutline.StructuredTextEditorContentOutlinePage;
import org.objectstyle.wolips.apieditor.editor.ApiEditor;
import org.objectstyle.wolips.componenteditor.ComponenteditorPlugin;
import org.objectstyle.wolips.components.input.ComponentEditorInput;
import org.objectstyle.wolips.htmleditor.editor.StructuredTextEditorHTMLWithWebObjectTags;
import org.objectstyle.wolips.wodclipse.WodclipsePlugin;
import org.objectstyle.wolips.wodclipse.wod.WodEditor;

public class ComponentEditorPart extends MultiPageEditorPart {

	private ComponentEditorInput componentEditorInput;

	private IEditorPart[] editorParts;

	boolean htmlActive = true;

	public CompilationUnitEditor compilationUnitEditor;

	public StructuredTextEditorHTMLWithWebObjectTags structuredTextEditorHTMLWithWebObjectTags;

	public WodEditor wodEditor;

	public ApiEditor apiEditor;

	private ComponentEditorOutline componentEditorOutline;

	public ComponentEditorPart() {
		super();
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
		String javaInputName = componentEditorInput.getInput()[0].getName();
		String partName = javaInputName
				.substring(0, javaInputName.length() - 5);
		setPartName(partName);
	}

	public Composite createInnerPartControl(Composite parent,
			final IEditorPart e) {
		Composite content = new Composite(parent, SWT.NONE);
		content.setLayout(new FillLayout());
		e.createPartControl(content);
		return content;
	}

	protected void createPages() {
		IEditorInput[] editorInput = componentEditorInput.getInput();
		editorParts = new IEditorPart[editorInput.length];
		Composite componentEditorParent = new Composite(getContainer(),
				SWT.NONE);
		componentEditorParent.setLayout(new FillLayout());

		SashForm componentEditorSashParent = new SashForm(
				componentEditorParent, SWT.VERTICAL | SWT.SMOOTH);
		SashForm htmlSashform = new SashForm(componentEditorSashParent,
				SWT.VERTICAL);
		htmlSashform.addListener(SWT.Activate, new Listener() {
			public void handleEvent(Event event) {
				htmlActive = true;
				pageChange(1);
				ComponentEditorPart.this.updateOutline();
			}
		});
		SashForm wodSashform = new SashForm(componentEditorSashParent,
				SWT.VERTICAL);
		wodSashform.addListener(SWT.Activate, new Listener() {
			public void handleEvent(Event event) {
				htmlActive = false;
				pageChange(1);
				ComponentEditorPart.this.updateOutline();
			}
		});
		for (int i = 0; i < componentEditorInput.getEditors().length; i++) {
			IEditorPart editorPart = null;
			switch (i) {
			case 0:
				compilationUnitEditor = new CompilationUnitEditor();;
				editorPart = compilationUnitEditor;
				try {
					this.addPage(editorPart, editorInput[i]);
				} catch (PartInitException e) {
					ComponenteditorPlugin.getDefault().log(e);
				}
				this.setPageText(i, "Java");
				break;
			case 1:
				structuredTextEditorHTMLWithWebObjectTags = new StructuredTextEditorHTMLWithWebObjectTags();
				editorPart = structuredTextEditorHTMLWithWebObjectTags;
				IEditorSite htmlSite = createSite(editorPart);
				try {
					editorPart.init(htmlSite, editorInput[i]);
				} catch (PartInitException e) {
					ComponenteditorPlugin.getDefault().log(e);
				}
				createInnerPartControl(htmlSashform, editorPart);
				editorPart.addPropertyListener(new IPropertyListener() {
					public void propertyChanged(Object source, int propertyId) {
						ComponentEditorPart.this
								.handlePropertyChange(propertyId);
					}
				});
				break;
			case 2:
				wodEditor = new WodEditor();
				editorPart = wodEditor;
				IEditorSite wodSite = createSite(editorPart);
				try {
					editorPart.init(wodSite, editorInput[i]);
				} catch (PartInitException e) {
					ComponenteditorPlugin.getDefault().log(e);
				}
				createInnerPartControl(wodSashform, editorPart);
				editorPart.addPropertyListener(new IPropertyListener() {
					public void propertyChanged(Object source, int propertyId) {
						ComponentEditorPart.this
								.handlePropertyChange(propertyId);
					}
				});
				this.addPage(componentEditorParent);
				this.setPageText(i - 1, "Component");
				wodEditor.getSelectionProvider().addSelectionChangedListener(
						new ISelectionChangedListener() {

							public void selectionChanged(
									SelectionChangedEvent event) {
								WodclipsePlugin.getDefault()
										.updateWebObjectsTagNames(null);
							}

						});
				WodclipsePlugin.getDefault()
						.updateWebObjectsTagNames(wodEditor);
				break;
			case 3:
				apiEditor = new ApiEditor();
				editorPart = apiEditor;
				try {
					this.addPage(editorPart, editorInput[i]);
				} catch (PartInitException e) {
					ComponenteditorPlugin.getDefault().log(e);
				}
				this.setPageText(i - 1, "Api");
				break;

			default:
				break;
			}

			editorParts[i] = editorPart;

		}
		StructuredTextEditorContentOutlinePage contentOutlinePage = null;
		contentOutlinePage = (StructuredTextEditorContentOutlinePage) structuredTextEditorHTMLWithWebObjectTags
				.getAdapter(IContentOutlinePage.class);
		addWebObjectsTagNamesListener(wodEditor,
				structuredTextEditorHTMLWithWebObjectTags);
		if (contentOutlinePage != null) {
			new HTMLOutlineSelectionHandler(contentOutlinePage,
					(WodEditor) wodEditor);
		}
		CTabFolder tabFolder = (CTabFolder) this.getContainer();
		tabFolder.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				ComponentEditorPart.this.updateOutline();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				return;
			}

		});
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				ComponentEditorPart.this.updateOutline();
			}
		});
		return;
	}

	private void addWebObjectsTagNamesListener(final WodEditor wodEditor,
			StructuredTextEditorHTML htmlEditor) {
		htmlEditor.getSelectionProvider().addSelectionChangedListener(
				new ISelectionChangedListener() {

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

	protected IEditorPart getEditor(int pageIndex) {
		if (editorParts == null) {
			return null;
		}
		int index = pageIndex;
		if (index > 1) {
			index = index + 1;
		} else if (index == 1) {
			if (!htmlActive) {
				index = 2;
			}
		}
		return editorParts[index];
	}

	public void doSave(IProgressMonitor monitor) {
		for (int i = 0; i < editorParts.length; i++) {
			if (editorParts[i].isDirty()) {
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
}
