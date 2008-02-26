/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2006 The ObjectStyle Group and individual authors of the
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
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.PartInitException;
import org.objectstyle.wolips.componenteditor.ComponenteditorPlugin;
import org.objectstyle.wolips.templateeditor.TemplateEditor;
import org.objectstyle.wolips.wodclipse.WodclipsePlugin;
import org.objectstyle.wolips.wodclipse.core.Activator;
import org.objectstyle.wolips.wodclipse.editor.WodEditor;

public class HtmlWodTab extends ComponentEditorTab {
	private static final String SASH_WEIGHTS_KEY = "org.objectstyle.wolips.componenteditor.sashWeights";

	
	private TemplateEditor templateEditor;

	private WodEditor wodEditor;

	private boolean htmlActive;

	private IEditorInput htmlInput;

	private IEditorInput wodInput;
	
	private SashForm htmlSashForm;

	private SashForm wodSashForm;
	
	private Composite wodContainer;
	
	private Label nonEmptyWodWarning;

	public HtmlWodTab(ComponentEditorPart componentEditorPart, int tabIndex, IEditorInput htmlInput, IEditorInput wodInput) {
		super(componentEditorPart, tabIndex);
		this.htmlInput = htmlInput;
		this.wodInput = wodInput;
	}

	public IEditorPart getActiveEmbeddedEditor() {
		if (htmlActive) {
			return templateEditor;
		}
		return wodEditor;
	}

	public void createTab() {
		this.htmlSashForm = new SashForm(this.getParentSashForm(), SWT.VERTICAL);
		this.wodSashForm = new SashForm(this.getParentSashForm(), SWT.VERTICAL);
		this.wodSashForm.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));

		templateEditor = new TemplateEditor();
		IEditorSite htmlSite = this.getComponentEditorPart().publicCreateSite(templateEditor);
		try {
			templateEditor.init(htmlSite, htmlInput);
		} catch (PartInitException e) {
			ComponenteditorPlugin.getDefault().log(e);
		}
		createInnerPartControl(htmlSashForm, templateEditor);
		templateEditor.addPropertyListener(new IPropertyListener() {
			public void propertyChanged(Object source, int propertyId) {
				HtmlWodTab.this.getComponentEditorPart().publicHandlePropertyChange(propertyId);
			}
		});
		wodEditor = new WodEditor();
		IEditorSite wodSite = this.getComponentEditorPart().publicCreateSite(wodEditor);
		try {
			wodEditor.init(wodSite, wodInput);
		} catch (PartInitException e) {
			ComponenteditorPlugin.getDefault().log(e);
		}
		this.wodContainer = createInnerPartControl(wodSashForm, wodEditor);
		wodEditor.addPropertyListener(new IPropertyListener() {
			public void propertyChanged(Object source, int propertyId) {
				HtmlWodTab.this.getComponentEditorPart().publicHandlePropertyChange(propertyId);
			}
		});
		wodEditor.getSelectionProvider().addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				WodclipsePlugin.getDefault().updateWebObjectsTagNames(null);
			}
		});
		WodclipsePlugin.getDefault().updateWebObjectsTagNames(wodEditor);
		htmlSashForm.addListener(SWT.Activate, new Listener() {
			public void handleEvent(Event event) {
				setHtmlActive(true);
				HtmlWodTab.this.getComponentEditorPart().pageChange(HtmlWodTab.this.getTabIndex());
				HtmlWodTab.this.getComponentEditorPart().updateOutline();
			}
		});
		wodSashForm.addListener(SWT.Activate, new Listener() {
			public void handleEvent(Event event) {
				setHtmlActive(false);
				HtmlWodTab.this.getComponentEditorPart().pageChange(HtmlWodTab.this.getTabIndex());
				HtmlWodTab.this.getComponentEditorPart().updateOutline();
			}
		});
		
		restoreSashWeights();
		hideWodIfNecessary();
		
		htmlSashForm.addControlListener(new ControlListener() {
			public void controlMoved(ControlEvent e) {
				// DO NOTHING
			}

			public void controlResized(ControlEvent e) {
				HtmlWodTab.this.saveSashWeights();
				HtmlWodTab.this.hideWodIfNecessary();
			}
		});

		templateEditor.initEditorInteraction(this.getComponentEditorPart().getEditorInteraction());
		wodEditor.initEditorInteraction(this.getComponentEditorPart().getEditorInteraction());
		
		this.addWebObjectsTagNamesListener();
	}
	
	public boolean isHtmlActive() {
		return this.htmlActive;
	}
	
	protected void setHtmlActive(boolean htmlActive) {
		this.htmlActive = htmlActive;
	}
	
	protected void hideWodIfNecessary() {
		int[] weights = getParentSashForm().getWeights();
		if (weights.length >= 2 && weights[1] < 132) {
			this.wodContainer.setVisible(false);
			if (this.nonEmptyWodWarning == null) {
				String wodContents = this.wodEditor.getWodEditDocument().get();
				if (wodContents != null && wodContents.trim().length() > 0) {
					this.nonEmptyWodWarning = new Label(this.wodSashForm, SWT.CENTER);
					this.nonEmptyWodWarning.setBackground(this.wodSashForm.getBackground());
					this.nonEmptyWodWarning.setForeground(this.wodSashForm.getDisplay().getSystemColor(SWT.COLOR_GRAY));
					this.nonEmptyWodWarning.setText("wod file is not empty");
				}
			}
		}
		else {
			this.wodContainer.setVisible(true);
			if (this.nonEmptyWodWarning != null) {
				this.nonEmptyWodWarning.dispose();
				this.nonEmptyWodWarning = null;
			}
		}
	}
	
	protected void restoreSashWeights() {
		String sashWeightsStr = Activator.getDefault().getPluginPreferences().getString(HtmlWodTab.SASH_WEIGHTS_KEY);
		if (sashWeightsStr != null && sashWeightsStr.length() > 0) {
			String[] sashWeightStrs = sashWeightsStr.split(",");
			int[] sashWeights = new int[sashWeightStrs.length];
			for (int sashWeightNum = 0; sashWeightNum < sashWeightStrs.length; sashWeightNum++) {
				sashWeights[sashWeightNum] = Integer.parseInt(sashWeightStrs[sashWeightNum]);
			}
			if (sashWeights.length == getParentSashForm().getWeights().length) {
				getParentSashForm().setWeights(sashWeights);
			}
		}
	}

	protected void saveSashWeights() {
		int[] weights = getParentSashForm().getWeights();
		StringBuffer weightsBuffer = new StringBuffer();
		for (int weight : weights) {
			weightsBuffer.append(weight);
			weightsBuffer.append(",");
		}
		if (weightsBuffer.length() > 0) {
			weightsBuffer.setLength(weightsBuffer.length() - 1);
		}
		Activator.getDefault().getPluginPreferences().setValue(HtmlWodTab.SASH_WEIGHTS_KEY, weightsBuffer.toString());
	}

	/**
	 * @return the template editor for this html/wod composite.
	 */

	public TemplateEditor templateEditor() {
		return templateEditor;
	}

	/**
	 * @return the wod editor for this html/wod composite.
	 */

	public WodEditor wodEditor() {
		return wodEditor;
	}

	public void doSave(IProgressMonitor monitor) {
		if (wodEditor.isDirty()) {
			wodEditor.doSave(monitor);
		}
		if (templateEditor.isDirty()) {
			templateEditor.doSave(monitor);
		}
	}

	public void close(boolean save) {
		wodEditor.close(save);
		// templateEditor.close(save);
	}

	public void dispose() {
		wodEditor.dispose();
		templateEditor.dispose();
	}

	public boolean isDirty() {
		return wodEditor.isDirty() || templateEditor.isDirty();
	}

	private void addWebObjectsTagNamesListener() {
		// templateEditor.getSelectionProvider().addSelectionChangedListener(new
		// ISelectionChangedListener() {
		// public void selectionChanged(SelectionChangedEvent event) {
		// WodclipsePlugin.getDefault().updateWebObjectsTagNames(null);
		// }
		// });
		final WodEditor finalWodEditor = wodEditor;
		wodEditor.getSelectionProvider().addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				WodclipsePlugin.getDefault().updateWebObjectsTagNames(finalWodEditor);
			}

		});
		WodclipsePlugin.getDefault().updateWebObjectsTagNames(wodEditor);
	}

	public void setHtmlActive() {
		this.htmlActive = true;
	}

	public void setWodActive() {
		this.htmlActive = false;
	}

	public IEditorInput getActiveEditorInput() {
		if (this.htmlActive) {
			return this.htmlInput;
		}
		return this.wodInput;
	}

}
