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
package org.objectstyle.wolips.htmlpreview.editor;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.aonir.fuzzyxml.FuzzyXMLDocument;
import jp.aonir.fuzzyxml.FuzzyXMLElement;
import jp.aonir.fuzzyxml.FuzzyXMLNode;
import jp.aonir.fuzzyxml.FuzzyXMLText;
import jp.aonir.fuzzyxml.internal.RenderContext;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.StatusTextEvent;
import org.eclipse.swt.browser.StatusTextListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.MultiPageEditorSite;
import org.objectstyle.wolips.components.editor.EditorInteraction;
import org.objectstyle.wolips.components.editor.IEmbeddedEditor;
import org.objectstyle.wolips.components.editor.IEmbeddedEditorSelected;
import org.objectstyle.wolips.htmlpreview.HtmlPreviewPlugin;
import org.objectstyle.wolips.templateeditor.TemplateEditor;
import org.objectstyle.wolips.templateeditor.TemplateSourceEditor;
import org.objectstyle.wolips.wodclipse.core.Activator;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;
import org.objectstyle.wolips.wodclipse.core.model.IWodBinding;
import org.objectstyle.wolips.wodclipse.core.model.IWodElement;
import org.objectstyle.wolips.wodclipse.core.preferences.PreferenceConstants;
import org.objectstyle.wolips.wodclipse.core.util.WodHtmlUtils;

import tk.eclipse.plugin.htmleditor.HTMLUtil;

/**
 * based on an eclipse.org example
 * 
 * @author uli
 */
public class HtmlPreviewEditor implements IEmbeddedEditor, IEmbeddedEditorSelected, IEditorPart, StatusTextListener {

	private EditorInteraction _editorInteraction;

	private IEditorSite _site;

	private IEditorInput _input;

	private Browser _browser;

	private Map<String, FuzzyXMLNode> _nodeMap;

	private int _counter;

	/**
	 * Update the contents of the Preview page
	 */
	private void updatePreviewContent() {
		if (_editorInteraction == null) {
			return;
		}

		IDocument editDocument = _editorInteraction.getHtmlDocumentProvider().getHtmlEditDocument();
		String documentContents = editDocument.get();

		_counter = 0;
		_nodeMap = new HashMap<String, FuzzyXMLNode>();

		try {
			WodParserCache cache = ((TemplateEditor) _editorInteraction.getHtmlDocumentProvider()).getSourceEditor().getParserCache();
			FuzzyXMLDocument htmlDocument = cache.getHtmlXmlDocument();
			RenderContext renderContext = new RenderContext(true);
			FuzzyXMLElement documentElement = htmlDocument.getDocumentElement();
			// renderContext.setDelegate(new PreviewRenderDelegate(cache));
			// documentContents = documentElement.toXMLString(renderContext);
			StringBuffer documentContentsBuffer = new StringBuffer();
			documentContentsBuffer.append("<html><body>");
			documentContentsBuffer.append("<style>");
			documentContentsBuffer.append("body { font-family: Helvetica; font-size: 8pt; }");

			documentContentsBuffer.append("a { text-decoration: none; }");

			documentContentsBuffer.append("div.element { margin-top: 5px; margin-bottom: 10px; margin-right: 0px; padding: 0px; border: 1px solid rgb(230, 230, 230); border-right: none; }");
			documentContentsBuffer.append("div.element.wo { border: 1px solid rgb(200, 200, 255); border-right: none; }");
			documentContentsBuffer.append("div.element.document { margin: 0px; padding: 0px; border: none; }");

			documentContentsBuffer.append("div.element div.summary { background-color: rgb(240, 240, 240); border-bottom: 1px solid rgb(230, 230, 230); padding: 3px; }");
			documentContentsBuffer.append("div.element.wo div.summary { background-color: rgb(240, 240, 255); border-bottom: 1px solid rgb(200, 200, 255); }");
			documentContentsBuffer.append("div.element.document div.summary { margin: 0px; padding: 0px; border: none; display: none; }");

			documentContentsBuffer.append("div.element div.summary div.title { font-weight: bold; }");
			documentContentsBuffer.append("div.element div.summary div.title span.type { font-size: 0.80em; color: rgb(150, 150, 150); }");
			documentContentsBuffer.append("div.element div.summary div.title.nonwo { color: rgb(180, 180, 180); }");
			documentContentsBuffer.append("div.element div.summary div.title.missing { font-style: italic; }");
			documentContentsBuffer.append("div.element div.summary div.title.nonwo span.className { font-weight: bold; color: rgb(120, 120, 200); padding-left: 20px; display: none; }");

			documentContentsBuffer.append("div.element div.summary table.bindings { font-family: Helvetica; font-size: 8pt; margin: 0px; padding: 0px; }");
			documentContentsBuffer.append("div.element div.summary table.bindings th { text-align: right; font-weight: normal; color: rgb(220, 0, 0); padding-right: 3px; }");
			documentContentsBuffer.append("div.element div.summary table.bindings td { color: rgb(150, 0, 0); }");
			documentContentsBuffer.append("div.element div.summary table.bindings td.literal { color: rgb(0, 0, 150); }");

			documentContentsBuffer.append("div.element div.contents { background-color: rgb(255, 255, 255); padding-left: 10px; padding-right: 0px; padding-top: 5px; padding-bottom: 5px; }");
			documentContentsBuffer.append("div.element.document div.contents { margin: 0px; padding: 0px; border: none; }");
			documentContentsBuffer.append("div.element.wo div.contents { background-color: rgb(250, 250, 255); }");

			documentContentsBuffer.append("div.element div.summary:hover { border-color: rgb(210, 210, 210); }");
			documentContentsBuffer.append("div.element div.summary:hover { background-color: rgb(220, 220, 220); }");
			documentContentsBuffer.append("div.element.wo div.summary:hover { background-color: rgb(220, 220, 245); }");
			documentContentsBuffer.append("div.element.wo div.summary:hover { border-color: rgb(170, 170, 225); }");

			documentContentsBuffer.append("div.text { display: inline; }");

			documentContentsBuffer.append("</style>");
			renderElement(documentElement, renderContext, documentContentsBuffer, cache);
			documentContentsBuffer.append("</body></html>");
			documentContents = documentContentsBuffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		boolean rendered = _browser.setText(documentContents);
		if (!rendered) {
			HtmlPreviewPlugin.getDefault().log("Can't create preview of component HTML.");
		}
	}

	protected void renderElement(FuzzyXMLNode node, RenderContext renderContext, StringBuffer renderBuffer, WodParserCache cache) {
		String nodeID = "node" + (_counter++);
		_nodeMap.put(nodeID, node);
		if (node instanceof FuzzyXMLElement) {
			FuzzyXMLElement element = (FuzzyXMLElement) node;
			String nodeName = element.getName();
			String className = "element " + nodeName.replace(':', '_');

			boolean woTag = WodHtmlUtils.isWOTag(nodeName);

			if (woTag) {
				className = className + " wo";
			}

			renderBuffer.append("<div id = \"" + nodeID + "\" class = \"" + className + "\">");

			renderBuffer.append("<div class = \"summary\" onclick = \"window.status = 'open:" + nodeID + "'\">");

			if (woTag) {
				boolean wo54 = Activator.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.WO54_KEY);
				try {
					IWodElement wodElement = WodHtmlUtils.getOrCreateWodElement(element, wo54, cache);
					if (wodElement != null) {
						if (WodHtmlUtils.isInline(nodeName)) {
							renderBuffer.append("<div class = \"title\"><span class = \"nodeName\">" + wodElement.getElementType() + "</span></div>");
						} else {
							renderBuffer.append("<div class = \"title\"><span class = \"nodeName\">" + wodElement.getElementName() + "</span> : <span class = \"type\">" + wodElement.getElementType() + "</span></div>");
						}
						List<IWodBinding> wodBindings = wodElement.getBindings();
						if (wodBindings.size() > 0) {
							renderBuffer.append("<table class = \"bindings\">");
							for (IWodBinding wodBinding : wodBindings) {
								renderBuffer.append("<tr>");
								renderBuffer.append("<th>" + wodBinding.getName() + "</th>");
								String bindingClass;
								if (wodBinding.isKeyPath()) {
									bindingClass = "keypath";
								} else {
									bindingClass = "literal";
								}
								renderBuffer.append("<td class = \"" + bindingClass + "\">" + wodBinding.getValue() + "</td>");
								renderBuffer.append("</tr>");
							}
							renderBuffer.append("</table>");
						}
					} else {
						renderBuffer.append("<div class = \"title missing\">" + nodeName + "</div>");
					}
				} catch (Throwable t) {
					// IGNORE
					t.printStackTrace();
				}
			} else {
				renderBuffer.append("<div class = \"title nonwo\"><span class = \"nodeName\">");
				renderBuffer.append(nodeName);
				renderBuffer.append("</span>");
				String elementClass = element.getAttributeValue("class");
				if (elementClass != null) {
					renderBuffer.append("<span class = \"className\">" + elementClass + "</span>");
				}
				renderBuffer.append("</div>");
			}

			renderBuffer.append("</div>");

			if ("script".equalsIgnoreCase(nodeName)) {
				// don't show script
			} else if ("style".equalsIgnoreCase(nodeName)) {
				// don't show style
			} else {
				if (!element.isEmpty()) {
					renderBuffer.append("<div class = \"contents\">");
					FuzzyXMLNode[] children = element.getChildren();
					for (FuzzyXMLNode child : children) {
						renderElement(child, renderContext, renderBuffer, cache);
					}
					renderBuffer.append("</div>");
				}
			}

			renderBuffer.append("</div>");
		} else {
			StringBuffer nodeBuffer = new StringBuffer();
			node.toXMLString(renderContext, nodeBuffer);
			String nodeStr = HTMLUtil.escapeHTML(nodeBuffer.toString());
			boolean isText = (node instanceof FuzzyXMLText);
			if (isText) {
				renderBuffer.append("<div class = \"text\">" + nodeStr + "</div>");
			} else {
				renderBuffer.append(nodeStr);
			}
		}
	}

	public void initEditorInteraction(EditorInteraction editorInteraction) {
		this._editorInteraction = editorInteraction;
	}

	public IEditorInput getEditorInput() {
		return _input;
	}

	public IEditorSite getEditorSite() {
		return _site;
	}

	public void init(IEditorSite initSite, IEditorInput initInput) throws PartInitException {
		this._site = initSite;
		this._input = initInput;
	}

	public void addPropertyListener(IPropertyListener listener) {
		// do nothing
	}

	public void createPartControl(Composite parent) {
		_browser = new Browser(parent, SWT.READ_ONLY);
		_browser.addStatusTextListener(this);
		_browser.addLocationListener(new LocationListener() {
			public void changed(LocationEvent event) {
				// DO NOTHING
			}

			public void changing(LocationEvent event) {
				if ("about:blank".equals(_browser.getUrl())) {
					event.doit = false;
				}
			}
		});
	}

	public void changed(StatusTextEvent event) {
		String text = event.text;
		int colonIndex = text.indexOf(':');
		String command = text.substring(0, colonIndex);
		String target = text.substring(colonIndex + 1);
		if ("open".equals(command)) {
			FuzzyXMLNode selectedNode = _nodeMap.get(target);
			TemplateSourceEditor sourceEditor = ((TemplateEditor) _editorInteraction.getHtmlDocumentProvider()).getSourceEditor();
			sourceEditor.getViewer().setSelectedRange(selectedNode.getOffset(), selectedNode.getLength());
			sourceEditor.getViewer().revealRange(selectedNode.getOffset(), selectedNode.getLength());
			try {
				MultiPageEditorSite site = (MultiPageEditorSite)_site;
				// Circular references be damned
				site.getMultiPageEditor().getClass().getMethod("switchToHtml").invoke(site.getMultiPageEditor());
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}

	public IWorkbenchPartSite getSite() {
		return _site;
	}

	public String getTitle() {
		return null;
	}

	public Image getTitleImage() {
		return null;
	}

	public String getTitleToolTip() {
		return null;
	}

	public void removePropertyListener(IPropertyListener listener) {
		// do nothing
	}

	public Object getAdapter(Class adapter) {
		return null;
	}

	public void doSave(IProgressMonitor monitor) {
		// do nothing
	}

	public void doSaveAs() {
		// do nothing
	}

	public boolean isDirty() {
		return false;
	}

	public boolean isSaveAsAllowed() {
		return false;
	}

	public boolean isSaveOnCloseNeeded() {
		return false;
	}

	public void dispose() {
		_browser.dispose();
	}

	public void setFocus() {
		_browser.setFocus();
	}

	public void editorSelected() {
		updatePreviewContent();
	}

	public EditorInteraction getEditorInteraction() {
		return _editorInteraction;
	}

}