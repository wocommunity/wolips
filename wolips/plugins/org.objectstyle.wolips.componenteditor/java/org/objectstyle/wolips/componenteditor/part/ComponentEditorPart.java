/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2007 The ObjectStyle Group and individual authors of the
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

import java.util.ArrayList;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.part.MultiPageSelectionProvider;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.objectstyle.wolips.baseforuiplugins.IEditorTarget;
import org.objectstyle.wolips.componenteditor.outline.ComponentEditorOutline;
import org.objectstyle.wolips.components.editor.ComponentEditorInteraction;
import org.objectstyle.wolips.components.editor.IComponentEditor;
import org.objectstyle.wolips.components.input.ComponentEditorInput;
import org.objectstyle.wolips.templateeditor.TemplateEditor;
import org.objectstyle.wolips.wodclipse.editor.WodEditor;

/**
 * @author uli
 */
public class ComponentEditorPart extends MultiPageEditorPart implements IEditorTarget, IResourceChangeListener, IComponentEditor {
	private int htmlPageId;
	private int wodPageId;
	private int wooPageId;
	private int previewPageId;
	private int apiPageId;
	
	ComponentEditorInput componentEditorInput;

	private ComponentEditorInteraction editorInteraction = new ComponentEditorInteraction();

	private ComponentEditorOutline componentEditorOutline;

	private HtmlWodTab[] htmlWodTabs;

	private HtmlPreviewTab htmlPreviewTab;

	private ApiTab apiTab;

	private ComponentEditorTab[] componentEditorTabs;

	public ComponentEditorPart() {
		super();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
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

	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input);
		componentEditorInput = (ComponentEditorInput) input;
		if (input != null) {
			String inputName = input.getName();
			String partName = inputName.substring(0, inputName.indexOf("."));
			setPartName(partName);
		}
		site.setSelectionProvider(new ComponentEditorPartSelectionProvider(this));
	}

	public IEditorInput getEditorInput() {
		return componentEditorTabs[this.getActivePage()].getActiveEditorInput();
	}

	protected void createPages() {
		if (componentEditorInput == null) {
			return;
		}
		IEditorInput[] editorInput = componentEditorInput.getComponentEditors();
		ArrayList<ComponentEditorTab> componentEditorTabsList = new ArrayList<ComponentEditorTab>();
		ArrayList<HtmlWodTab> htmlWodTabsList = new ArrayList<HtmlWodTab>();
		// htmlwod tabs
		IFileEditorInput htmlInput = null;
		IFileEditorInput wodInput = null;
		IFileEditorInput wooInput = null;
		int j = 0;
		int tabIndex = 0;
		for (int i = 0; i < editorInput.length / 3; i++) {
			htmlInput = (IFileEditorInput) editorInput[j];
			j++;
			wodInput = (IFileEditorInput) editorInput[j];
			j++;
			wooInput = (IFileEditorInput) editorInput[j];
			j++;
			HtmlWodTab htmlWodTab = new HtmlWodTab(this, tabIndex, htmlInput, wodInput);
			componentEditorTabsList.add(htmlWodTab);
			htmlWodTabsList.add(htmlWodTab);
			htmlWodTab.createTab();
			htmlPageId = this.addPage(htmlWodTab);
			wodPageId = htmlPageId;
			this.setPageText(tabIndex, "Component");
			tabIndex++;
			
			

			WooTab wooTab = new WooTab(this, tabIndex, wooInput);
			componentEditorTabsList.add(wooTab);
			wooTab.createTab();
			wooPageId = this.addPage(wooTab);
			this.setPageText(tabIndex, "DisplayGroups");
			tabIndex++;
		}

		// html preview tab
		htmlPreviewTab = new HtmlPreviewTab(this, tabIndex, htmlInput);
		componentEditorTabsList.add(htmlPreviewTab);
		htmlPreviewTab.createTab();
		previewPageId = this.addPage(htmlPreviewTab);
		this.setPageText(tabIndex, "Preview");
		tabIndex++;
		
		// api tab
		IFileEditorInput apiInput = (IFileEditorInput) componentEditorInput.getApiEditor();
		apiTab = new ApiTab(this, tabIndex, apiInput);
		componentEditorTabsList.add(apiTab);
		apiTab.createTab();
		apiPageId = this.addPage(apiTab);
		this.setPageText(tabIndex, "Api");

		componentEditorTabs = componentEditorTabsList.toArray(new ComponentEditorTab[componentEditorTabsList.size()]);
		htmlWodTabs = htmlWodTabsList.toArray(new HtmlWodTab[htmlWodTabsList.size()]);
		
		CTabFolder tabFolder = (CTabFolder) this.getContainer();
		tabFolder.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				ComponentEditorPart.this.updateOutline();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				return;
			}

		});
		if (componentEditorInput.isDisplayWodPartOnReveal()) {
			this.switchToWod();
		} else if (componentEditorInput.isDisplayWooPartOnReveal()) {
			this.switchToWoo();
		} else if (componentEditorInput.isDisplayApiPartOnReveal()) {
			this.switchToApi();
		} else if (componentEditorInput.isDisplayHtmlPartOnReveal()) {
			this.switchToHtml();
		}
		return;
	}

	public void doSave(IProgressMonitor monitor) {
		for (int i = 0; i < componentEditorTabs.length; i++) {
			if (componentEditorTabs[i] != null && componentEditorTabs[i].isDirty()) {
				componentEditorTabs[i].doSave(monitor);
			}
		}
		return;
	}

	public void close(boolean save) {
		for (int i = 0; i < componentEditorTabs.length; i++) {
			if (componentEditorTabs[i] != null) {
				componentEditorTabs[i].close(save);
			}
		}
	}

	public void doSaveAs() {
		assert false;
		return;
	}

	public boolean isSaveAsAllowed() {
		return false;
	}

	public void updateOutline() {
		IEditorPart editorPart = this.getActiveEditor();
		if (editorPart != null) {
			IContentOutlinePage contentOutlinePage = (IContentOutlinePage) editorPart.getAdapter(IContentOutlinePage.class);
			this.getComponentEditorOutline().setPageActive(contentOutlinePage);
		}
	}

	public boolean isDirty() {
		if (super.isDirty()) {
			return true;
		}
		for (int i = 0; i < componentEditorTabs.length; i++) {
			if (componentEditorTabs[i].isDirty()) {
				return true;
			}
		}
		return false;
	}

	public IEditorPart switchTo(int targetEditorID) {
		switch (targetEditorID) {
		case IEditorTarget.TARGET_API:
			this.switchToApi();
			break;
		case IEditorTarget.TARGET_HTML:
			this.switchToHtml();
			break;
		case IEditorTarget.TARGET_PREVIEW:
			this.switchToPreview();
			break;
		case IEditorTarget.TARGET_WOD:
			this.switchToWod();
			break;
		case IEditorTarget.TARGET_WOO:
			this.switchToWoo();
			break;

		default:
			break;
		}
		IEditorPart editorPart = getActiveEditor();
		return editorPart;
	}

	public HtmlWodTab htmlWodTab() {
		return htmlWodTabs[0];
	}
	
	public void switchToHtml() {
		this.htmlWodTab().setHtmlActive();
		switchToPage(htmlPageId);
	}

	public void switchToWod() {
		this.htmlWodTab().setWodActive();
		switchToPage(wodPageId);
	}

	public void switchToWoo() {
		switchToPage(wooPageId);
	}

	public void switchToPreview() {
		switchToPage(previewPageId);
	}

	public void switchToApi() {
		switchToPage(apiPageId);
	}

	public void switchToPage(int page) {
		this.setActivePage(page);
		setFocus();
	}

	protected void pageChange(int newPageIndex) {
		super.pageChange(newPageIndex);
		componentEditorTabs[newPageIndex].editorSelected();
	}

	public ComponentEditorInput getComponentEditorInput() {
		return componentEditorInput;
	}
	
	public TemplateEditor getTemplateEditor() {
		return htmlWodTab().templateEditor();
	}
	
	public WodEditor getWodEditor() {
		return htmlWodTab().wodEditor();
	}

	public IEditorPart getEditor(int pageIndex) {
		return componentEditorTabs[this.getActivePage()].getActiveEmbeddedEditor();
	}

	public IEditorPart getActiveEditor() {
		int activePage = this.getActivePage();
		if(activePage == -1) {
			return null;
		}
		return componentEditorTabs[activePage].getActiveEmbeddedEditor();
	}

	private static class ComponentEditorPartSelectionProvider extends MultiPageSelectionProvider {
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

	public void publicHandlePropertyChange(int propertyId) {
		this.handlePropertyChange(propertyId);
	}

	public IEditorSite publicCreateSite(IEditorPart editor) {
		return this.createSite(editor);
	}

	public Composite publicGetContainer() {
		return super.getContainer();
	}

	public ComponentEditorInteraction getEditorInteraction() {
		return editorInteraction;
	}

	public void resourceChanged(final IResourceChangeEvent event) {
		if (event.getType() == IResourceChangeEvent.PRE_CLOSE) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					boolean closed = false;
					for (int i = 0; i < componentEditorInput.getInput().length; i++) {
						if (((FileEditorInput) componentEditorInput.getInput()[i]).getFile().getProject().equals(event.getResource())) {
							IWorkbenchPage[] pages = getSite().getWorkbenchWindow().getPages();
							for (int j = 0; j < pages.length; j++) {
								IEditorPart editorPart = pages[i].findEditor(componentEditorInput);
								if (editorPart != null) {
									if (pages[i].closeEditor(ComponentEditorPart.this, true)) {
										closed = true;
									}
								}
							}
						}
						if (closed) {
							break;
						}
					}
				}
			});
		}
	}

	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		for (int i = 0; i < componentEditorTabs.length; i++) {
			if (componentEditorTabs[i] != null) {
				componentEditorTabs[i].dispose();
			}
		}
		super.dispose();
	}

}
