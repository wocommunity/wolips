package org.objectstyle.wolips.htmlpreview.editor;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.aonir.fuzzyxml.FuzzyXMLDocument;
import jp.aonir.fuzzyxml.FuzzyXMLElement;
import jp.aonir.fuzzyxml.FuzzyXMLNode;
import jp.aonir.fuzzyxml.internal.RenderContext;
import jp.aonir.fuzzyxml.internal.RenderDelegate;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IType;
import org.objectstyle.wolips.bindings.api.ApiUtils;
import org.objectstyle.wolips.bindings.api.Wo;
import org.objectstyle.wolips.bindings.utils.BindingReflectionUtils;
import org.objectstyle.wolips.bindings.wod.IWodBinding;
import org.objectstyle.wolips.bindings.wod.IWodElement;
import org.objectstyle.wolips.htmlpreview.editor.tags.DefaultTagDelegate;
import org.objectstyle.wolips.htmlpreview.editor.tags.ERXStyleSheetTagDelegate;
import org.objectstyle.wolips.htmlpreview.editor.tags.WOBrowserTagDelegate;
import org.objectstyle.wolips.htmlpreview.editor.tags.WOCheckBoxTagDelegate;
import org.objectstyle.wolips.htmlpreview.editor.tags.WOComponentContentTagDelegate;
import org.objectstyle.wolips.htmlpreview.editor.tags.WOConditionalTagDelegate;
import org.objectstyle.wolips.htmlpreview.editor.tags.WOGenericContainerTagDelegate;
import org.objectstyle.wolips.htmlpreview.editor.tags.WOGenericElementTagDelegate;
import org.objectstyle.wolips.htmlpreview.editor.tags.WOHyperlinkTagDelegate;
import org.objectstyle.wolips.htmlpreview.editor.tags.WOImageTagDelegate;
import org.objectstyle.wolips.htmlpreview.editor.tags.WOPopUpButtonTagDelegate;
import org.objectstyle.wolips.htmlpreview.editor.tags.WORepetitionTagDelegate;
import org.objectstyle.wolips.htmlpreview.editor.tags.WOStringTagDelegate;
import org.objectstyle.wolips.htmlpreview.editor.tags.WOSubmitButtonTagDelegate;
import org.objectstyle.wolips.htmlpreview.editor.tags.WOTextFieldTagDelegate;
import org.objectstyle.wolips.htmlpreview.editor.tags.WOTextTagDelegate;
import org.objectstyle.wolips.locate.LocatePlugin;
import org.objectstyle.wolips.locate.result.LocalizedComponentsLocateResult;
import org.objectstyle.wolips.variables.BuildProperties;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;
import org.objectstyle.wolips.wodclipse.core.util.WodHtmlUtils;

public class PreviewRenderDelegate implements RenderDelegate {
	private static final String DEFAULT = "__default";

	private Map<String, TagDelegate> _tagDelegates;

	private Stack<WodParserCache> _caches;

	private Stack<FuzzyXMLNode> _nodes;

	private StringBuffer _cssBuffer;

	private boolean _previewStyleRendered;

	public PreviewRenderDelegate(WodParserCache cache) {
		_caches = new Stack<WodParserCache>();
		_nodes = new Stack<FuzzyXMLNode>();
		_caches.push(cache);
		_cssBuffer = new StringBuffer();

		_tagDelegates = new HashMap<String, TagDelegate>();
		_tagDelegates.put("WOString", new WOStringTagDelegate());
		_tagDelegates.put("ERXLocalizedString", new WOStringTagDelegate());
		_tagDelegates.put("WOConditional", new WOConditionalTagDelegate());
		_tagDelegates.put("WOComponentContent", new WOComponentContentTagDelegate());
		_tagDelegates.put("WORepetition", new WORepetitionTagDelegate());
		_tagDelegates.put("WOGenericContainer", new WOGenericContainerTagDelegate());
		_tagDelegates.put("WOGenericElement", new WOGenericElementTagDelegate());
		_tagDelegates.put("WOTextField", new WOTextFieldTagDelegate());
		_tagDelegates.put("FocusTextField", new WOTextFieldTagDelegate());
		_tagDelegates.put("WOCheckBox", new WOCheckBoxTagDelegate());
		_tagDelegates.put("WOPopUpButton", new WOPopUpButtonTagDelegate());
		_tagDelegates.put("WOText", new WOTextTagDelegate());
		_tagDelegates.put("FocusText", new WOTextTagDelegate());
		_tagDelegates.put("WOSubmitButton", new WOSubmitButtonTagDelegate());
		_tagDelegates.put("WOHyperlink", new WOHyperlinkTagDelegate());
		_tagDelegates.put("WOImage", new WOImageTagDelegate());
		_tagDelegates.put("WOBrowser", new WOBrowserTagDelegate());
		_tagDelegates.put("ERXStyleSheet", new ERXStyleSheetTagDelegate());
		_tagDelegates.put(PreviewRenderDelegate.DEFAULT, new DefaultTagDelegate());
	}

	public void beforeRender(RenderContext renderContext, StringBuffer xmlBuffer) {
		_previewStyleRendered = false;
		_cssBuffer = new StringBuffer();
	}

	public void afterRender(RenderContext renderContext, StringBuffer xmlBuffer) {
		if (!_previewStyleRendered) {
			renderPreviewStyle(xmlBuffer);
		}
	}

	public boolean renderNode(FuzzyXMLNode node, RenderContext renderContext, StringBuffer xmlBuffer) {
		if (node instanceof FuzzyXMLElement) {
			FuzzyXMLElement element = (FuzzyXMLElement) node;
			String tagName = element.getName();
			if (WodHtmlUtils.isWOTag(tagName)) {
				try {
					WodParserCache cache = _caches.peek();
					BuildProperties buildProperties = (BuildProperties)cache.getProject().getAdapter(BuildProperties.class);
					IWodElement wodElement = WodHtmlUtils.getWodElement(element, buildProperties, true, cache);
					if (wodElement == null) {
						return true;
					}

					String elementTypeName = wodElement.getElementType();
					
					TagDelegate tagDelegate = _tagDelegates.get(elementTypeName);
					if (tagDelegate != null) {
						tagDelegate.renderNode(wodElement, element, renderContext, xmlBuffer, _cssBuffer, _caches, _nodes);
					}
					else {
						IType type = BindingReflectionUtils.findElementType(cache.getJavaProject(), elementTypeName, false, WodParserCache.getTypeCache());
						LocalizedComponentsLocateResult componentsLocateResults = LocatePlugin.getDefault().getLocalizedComponentsLocateResult(type.getJavaProject().getProject(), wodElement.getElementType());
						IFile htmlFile = componentsLocateResults.getFirstHtmlFile();
						if (htmlFile != null) {
							WodParserCache nestedCache = WodParserCache.parser(htmlFile);
							if (nestedCache != null) {
								Wo apiModel = ApiUtils.findApiModelWo(type, WodParserCache.getTypeCache().getApiCache(cache.getJavaProject()));
								if (apiModel != null) {
									String preview = apiModel.getPreview();
									if (preview != null) {
										StringBuffer previewBuffer = new StringBuffer();
										Pattern bindingPattern = Pattern.compile("\\$([a-zA-Z0-9_]+)");
										Matcher matcher = bindingPattern.matcher(preview);
										while (matcher.find()) {
											String bindingName = matcher.group(1);
											IWodBinding binding = wodElement.getBindingNamed(bindingName);
											if (binding == null) {
												matcher.appendReplacement(previewBuffer, "");
											} else {
												matcher.appendReplacement(previewBuffer, binding.getValue());
											}
										}
										matcher.appendTail(previewBuffer);
	
										nestedCache = nestedCache.cloneCache();
										nestedCache.getHtmlEntry().setContents("<span>" + previewBuffer.toString() + "</span>");
									}
								}
								_caches.push(nestedCache);
								_nodes.push(node);
								try {
									FuzzyXMLDocument nestedDocument = nestedCache.getHtmlEntry().getModel();
									if (nestedDocument != null) {
										nestedDocument.getDocumentElement().toXMLString(renderContext, xmlBuffer);
									}
								} finally {
									_nodes.pop();
									_caches.pop();
								}
							}
						} else {
							tagDelegate = _tagDelegates.get(PreviewRenderDelegate.DEFAULT);
							tagDelegate.renderNode(wodElement, element, renderContext, xmlBuffer, _cssBuffer, _caches, _nodes);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				node.toXMLString(renderContext, xmlBuffer);
			}
		} else {
			node.toXMLString(renderContext, xmlBuffer);
		}
		return false;
	}

	public boolean beforeOpenTag(FuzzyXMLNode node, RenderContext renderContext, StringBuffer xmlBuffer) {
		boolean renderSurroundingTags = true;
		if (node instanceof FuzzyXMLElement) {
			FuzzyXMLElement element = (FuzzyXMLElement) node;
			String tagName = element.getName();
			if (WodHtmlUtils.isWOTag(tagName)) {
				renderSurroundingTags = false;
			}
			else if ("document".equals(tagName)) {
				renderSurroundingTags = false;
			}
		}
		return renderSurroundingTags;
	}

	public void afterOpenTag(FuzzyXMLNode node, RenderContext renderContext, StringBuffer xmlBuffer) {
		// DO NOTHING
	}

	public void beforeCloseTag(FuzzyXMLNode node, RenderContext renderContext, StringBuffer xmlBuffer) {
		// DO NOTHING
	}

	public void afterCloseTag(FuzzyXMLNode node, RenderContext renderContext, StringBuffer xmlBuffer) {
		// DO NOTHING
	}

	protected void renderPreviewStyle(StringBuffer xmlBuffer) {
		xmlBuffer.append("<style>");
		xmlBuffer.append("span.wodclipse_block {");
		xmlBuffer.append("  display: block;");
		xmlBuffer.append("  padding: 3px;");
		xmlBuffer.append("}");
		xmlBuffer.append("span.wodclipse_block:hover {");
		xmlBuffer.append("  background-color: rgba(220, 220, 255, 0.50);");
		xmlBuffer.append("}");
		xmlBuffer.append("span.wodclipse_block span.wodclipse_tag {");
		xmlBuffer.append("}");
		xmlBuffer.append(_cssBuffer);
		xmlBuffer.append("</style>");
		_previewStyleRendered = true;
	}
}