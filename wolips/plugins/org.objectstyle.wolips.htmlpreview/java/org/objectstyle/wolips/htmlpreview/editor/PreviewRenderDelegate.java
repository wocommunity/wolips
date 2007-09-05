package org.objectstyle.wolips.htmlpreview.editor;

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
import org.objectstyle.wolips.core.resources.types.api.Wo;
import org.objectstyle.wolips.locate.LocatePlugin;
import org.objectstyle.wolips.locate.result.LocalizedComponentsLocateResult;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;
import org.objectstyle.wolips.wodclipse.core.model.IWodBinding;
import org.objectstyle.wolips.wodclipse.core.model.IWodElement;
import org.objectstyle.wolips.wodclipse.core.util.WodApiUtils;
import org.objectstyle.wolips.wodclipse.core.util.WodHtmlUtils;
import org.objectstyle.wolips.wodclipse.core.util.WodReflectionUtils;

public class PreviewRenderDelegate implements RenderDelegate {
	private Stack<WodParserCache> _caches;

	private Stack<FuzzyXMLNode> _nodes;

	public PreviewRenderDelegate(WodParserCache cache) {
		_caches = new Stack<WodParserCache>();
		_nodes = new Stack<FuzzyXMLNode>();
		_caches.push(cache);
	}

	public boolean renderNode(FuzzyXMLNode node, RenderContext renderContext, StringBuffer xmlBuffer) {
		if (node instanceof FuzzyXMLElement) {
			FuzzyXMLElement element = (FuzzyXMLElement) node;
			String tagName = element.getName();
			if (WodHtmlUtils.isWOTag(tagName)) {
				try {
					WodParserCache cache = _caches.peek();
					IWodElement wodElement;
					if (WodHtmlUtils.isInline(tagName)) {
						wodElement = WodHtmlUtils.toWodElement(element, false, cache);
					} else {
						String elementName = element.getAttributeValue("name");
						wodElement = cache.getWodModel().getElementNamed(elementName);
					}

					String elementTypeName = wodElement.getElementType();
					IType type = WodReflectionUtils.findElementType(cache.getJavaProject(), elementTypeName, false, cache);
					LocalizedComponentsLocateResult componentsLocateResults = LocatePlugin.getDefault().getLocalizedComponentsLocateResult(type.getJavaProject().getProject(), wodElement.getElementType());
					IFile htmlFile = componentsLocateResults.getFirstHtmlFile();
					if (htmlFile != null) {
						WodParserCache nestedCache = WodParserCache.parser(htmlFile);
						if (nestedCache != null) {
							Wo apiModel = WodApiUtils.findApiModelWo(type, nestedCache);
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
										}
										else {
											matcher.appendReplacement(previewBuffer, binding.getValue());
										}
									}
									matcher.appendTail(previewBuffer);
									
									nestedCache = nestedCache.cloneCache();
									nestedCache.setHtmlContents("<span>" + previewBuffer.toString() + "</span>");
								}
							}
							_caches.push(nestedCache);
							_nodes.push(node);
							try {
								FuzzyXMLDocument nestedDocument = nestedCache.getHtmlXmlDocument();
								if (nestedDocument != null) {
									nestedDocument.getDocumentElement().toXMLString(renderContext, xmlBuffer);
								}
							} finally {
								_nodes.pop();
								_caches.pop();
							}
						}
					} else if ("WOString".equals(elementTypeName)) {
						IWodBinding valueBinding = wodElement.getBindingNamed("value");
						if (valueBinding.isKeyPath()) {
							xmlBuffer.append("<span class = \"wodclipse_WOString\">[" + valueBinding.getValue() + "]</span>");
						} else {
							xmlBuffer.append(valueBinding.getValue());
						}
					} else if ("WOConditional".equals(elementTypeName)) {
						IWodBinding conditionBinding = wodElement.getBindingNamed("condition");
						String conditionName;
						if (conditionBinding != null) {
							conditionName = conditionBinding.getValue();
						} else {
							conditionName = "WOConditional";
						}
						IWodBinding negateBinding = wodElement.getBindingNamed("negate");
						if (negateBinding != null && ("true".equalsIgnoreCase(negateBinding.getValue()) || "yes".equalsIgnoreCase(negateBinding.getValue()))) {
							conditionName = "!" + conditionName;
						}
						xmlBuffer.append("<span class = \"wodclipse_block wodclipse_WOConditional\"><span class = \"wodclipse_tag wodclipse_open_tag\">[if " + conditionName + "]</span>");
						node.toXMLString(renderContext, xmlBuffer);
						xmlBuffer.append("<span class = \"wodclipse_tag wodclipse_close_tag\">[/if " + conditionName + "]</span></span>");
					} else if ("WOComponentContent".equals(elementTypeName)) {
						WodParserCache parentCache = _caches.pop();
						try {
							_nodes.peek().toXMLString(renderContext, xmlBuffer);
						} finally {
							_caches.push(parentCache);
						}
					} else if ("WORepetition".equals(elementTypeName)) {
						IWodBinding listBinding = wodElement.getBindingNamed("list");
						String listName;
						if (listBinding != null) {
							listName = listBinding.getValue();
						} else {
							listName = "WORepetition";
						}
						xmlBuffer.append("<span class = \"wodclipse_block wodclipse_WORepetition\"><span class = \"wodclipse_tag wodclipse_open_tag\">[loop " + listName + "]</span>");
						node.toXMLString(renderContext, xmlBuffer);
						xmlBuffer.append("<span class = \"wodclipse_tag wodclipse_close_tag\">[/loop " + listName + "]</span></span>");
					} else if ("WOGenericContainer".equals(elementTypeName)) {
						IWodBinding elementNameBinding = wodElement.getBindingNamed("elementName");
						String elementName;
						if (elementNameBinding != null) {
							elementName = elementNameBinding.getValue();
						} else {
							elementName = "div";
						}
						xmlBuffer.append("<" + elementName + ">");
						node.toXMLString(renderContext, xmlBuffer);
						xmlBuffer.append("</" + elementName + ">");
					} else if ("WOGenericElement".equals(elementTypeName)) {
						IWodBinding elementNameBinding = wodElement.getBindingNamed("elementName");
						String elementName;
						if (elementNameBinding != null) {
							elementName = elementNameBinding.getValue();
						} else {
							elementName = "div";
						}
						xmlBuffer.append("<" + elementName + " />");
					} else if ("WOTextField".equals(elementTypeName) || "FocusTextField".equals(elementTypeName)) {
						IWodBinding valueBinding = wodElement.getBindingNamed("value");
						String value;
						if (valueBinding != null) {
							value = valueBinding.getValue();
						} else {
							value = "";
						}
						xmlBuffer.append("<input type = \"text\" value = \"" + value + "\"/>");
					} else if ("WOCheckBox".equals(elementTypeName)) {
						xmlBuffer.append("<input type = \"checkbox\">");
					} else if ("WOPopUpButton".equals(elementTypeName)) {
						IWodBinding listBinding = wodElement.getBindingNamed("list");
						String list;
						if (listBinding != null) {
							list = listBinding.getValue();
						} else {
							list = "";
						}
						xmlBuffer.append("<select><option>" + list + "</option></select>");
					} else if ("WOText".equals(elementTypeName) || "FocusText".equals(elementTypeName)) {
						IWodBinding valueBinding = wodElement.getBindingNamed("value");
						String value;
						if (valueBinding != null) {
							value = valueBinding.getValue();
						} else {
							value = "";
						}
						xmlBuffer.append("<textarea>" + value + "</textarea>");
					} else if ("WOSubmitButton".equals(elementTypeName)) {
						IWodBinding valueBinding = wodElement.getBindingNamed("value");
						String value;
						if (valueBinding != null) {
							if (valueBinding.isKeyPath()) {
								value = valueBinding.getValue();
							} else {
								value = valueBinding.getValue().replaceAll("\"", "");
							}
						} else {
							value = "Submit";
						}
						xmlBuffer.append("<input type = \"submit\" value = \"" + value + "\"/>");
					} else if ("WOHyperlink".equals(elementTypeName)) {
						xmlBuffer.append("<a href = \"#\">");
						node.toXMLString(renderContext, xmlBuffer);
						xmlBuffer.append("</a>");
					} else {
						node.toXMLString(renderContext, xmlBuffer);
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
		}
		return renderSurroundingTags;
	}

	public void afterOpenTag(FuzzyXMLNode node, RenderContext renderContext, StringBuffer xmlBuffer) {
		// DO NOTHING
	}

	public void beforeCloseTag(FuzzyXMLNode node, RenderContext renderContext, StringBuffer xmlBuffer) {
		if (node instanceof FuzzyXMLElement) {
			FuzzyXMLElement element = (FuzzyXMLElement) node;
			if ("head".equalsIgnoreCase(element.getName())) {
				xmlBuffer.append("<style>");
				xmlBuffer.append("span.wodclipse_block {");
				xmlBuffer.append("  display: inline;");
				xmlBuffer.append("  padding: 3px;");
				xmlBuffer.append("}");
				xmlBuffer.append("span.wodclipse_block span.wodclipse_tag {");
				xmlBuffer.append("}");
				xmlBuffer.append("span.wodclipse_WOString {");
				xmlBuffer.append("  color: red;");
				xmlBuffer.append("}");
				xmlBuffer.append("span.wodclipse_WOConditional {");
				xmlBuffer.append("  /*border: 1px dashed blue;*/");
				xmlBuffer.append("}");
				xmlBuffer.append("span.wodclipse_WOConditional span.wodclipse_tag {");
				xmlBuffer.append("  color: blue;");
				xmlBuffer.append("}");
				xmlBuffer.append("span.wodclipse_WORepetition {");
				xmlBuffer.append("  /*border: 1px dashed green;*/");
				xmlBuffer.append("}");
				xmlBuffer.append("span.wodclipse_WORepetition span.wodclipse_tag {");
				xmlBuffer.append("  color: green;");
				xmlBuffer.append("}");
				xmlBuffer.append("</style>");
			}
		}
	}

	public void afterCloseTag(FuzzyXMLNode node, RenderContext renderContext, StringBuffer xmlBuffer) {
		// DO NOTHING
	}
}