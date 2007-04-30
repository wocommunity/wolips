package tk.eclipse.plugin.jspeditor.editors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ui.IFileEditorInput;

import tk.eclipse.plugin.htmleditor.HTMLPlugin;
import tk.eclipse.plugin.htmleditor.HTMLUtil;
import tk.eclipse.plugin.htmleditor.ICustomTagAttributeAssist;
import tk.eclipse.plugin.htmleditor.assist.AssistInfo;
import tk.eclipse.plugin.htmleditor.assist.AttributeInfo;
import tk.eclipse.plugin.htmleditor.assist.HTMLAssistProcessor;
import tk.eclipse.plugin.htmleditor.assist.TagDefinition;
import tk.eclipse.plugin.htmleditor.assist.TagInfo;
import tk.eclipse.plugin.htmleditor.assist.TextInfo;
import tk.eclipse.plugin.htmleditor.editors.HTMLSourceEditor;
import tk.eclipse.plugin.xmleditor.editors.ClassNameAssistProcessor;

/**
 * This is an implementation of the AssistProcessor 
 * for the <code>JSPSourceEditor</code>.
 * 
 * @author Naoki Takezoe
 */
public class JSPAssistProcessor extends HTMLAssistProcessor {

  private List tagList = new ArrayList(TagDefinition.getTagInfoAsList());
  private static final int SCOPE = 100;
  private static final int CLASS = 101;
  private List cunstomTagList = new ArrayList();
  private HashMap namespace = new HashMap();
  private ClassNameAssistProcessor classNameProcessor = new ClassNameAssistProcessor();
  private JSPScriptletAssistProcessor scriptletProcessor = new JSPScriptletAssistProcessor();
  private IFile file = null;

  public JSPAssistProcessor() {
    // JSP actions
    TagInfo useBean = new TagInfo("jsp:useBean", true);
    useBean.addAttributeInfo(new AttributeInfo("id", true));
    useBean.addAttributeInfo(new AttributeInfo("scope", true, SCOPE));
    useBean.addAttributeInfo(new AttributeInfo("class", true, CLASS));
    tagList.add(useBean);

    TagInfo setProperty = new TagInfo("jsp:setProperty", false);
    setProperty.addAttributeInfo(new AttributeInfo("name", true));
    setProperty.addAttributeInfo(new AttributeInfo("param", true));
    setProperty.addAttributeInfo(new AttributeInfo("property", true));
    tagList.add(setProperty);

    TagInfo include = new TagInfo("jsp:include", false);
    include.addAttributeInfo(new AttributeInfo("page", true));
    tagList.add(include);

    TagInfo forward = new TagInfo("jsp:forward", true);
    forward.addAttributeInfo(new AttributeInfo("page", true));
    tagList.add(forward);

    TagInfo param = new TagInfo("jsp:param", false);
    param.addAttributeInfo(new AttributeInfo("name", true));
    param.addAttributeInfo(new AttributeInfo("value", true));
    tagList.add(param);

    TagInfo attribute = new TagInfo("jsp:attribute", true);
    attribute.addAttributeInfo(new AttributeInfo("name", true));
    tagList.add(attribute);

    TagInfo body = new TagInfo("jsp:body", true);
    tagList.add(body);

    TagInfo element = new TagInfo("jsp:element", true);
    element.addAttributeInfo(new AttributeInfo("name", true));
    tagList.add(element);

    TagInfo text = new TagInfo("jsp:text", true);
    tagList.add(text);

    // JSP directives
    tagList.add(new TextInfo("<%  %>", 3));
    tagList.add(new TextInfo("<%=  %>", 4));
    tagList.add(new TextInfo("<%@ page %>", 9));
    tagList.add(new TextInfo("<%@ include %>", "<%@ include file=\"\" %>", 18));
    tagList.add(new TextInfo("<%@ taglib %>", "<%@ taglib prefix=\"\" %>", 19));
  }

  public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int documentOffset) {
    // Java code completion for partitions which are not parted as HTMLPartitionScanner#HTML_SCRIPT.
    String rawText = viewer.getDocument().get();
    rawText = HTMLUtil.comment2space(rawText, false);
    String text = rawText.substring(0, documentOffset);
    int begin = text.lastIndexOf("<%");
    if (begin >= 0) {
      int end = rawText.indexOf("%>", begin);
      if (end >= 0 && documentOffset < end) {
        return scriptletProcessor.computeCompletionProposals(viewer, documentOffset);
      }
    }

    return super.computeCompletionProposals(viewer, documentOffset);
  }

  protected AssistInfo[] getAttributeValues(String tagName, String value, TagInfo tagInfo, AttributeInfo attrInfo) {
    if (tagName.indexOf(":") != -1) {
      String[] dim = tagName.split(":");
      String uri = getUri(dim[0]);
      ICustomTagAttributeAssist[] assists = HTMLPlugin.getDefault().getCustomTagAttributeAssists();
      for (int i = 0; i < assists.length; i++) {
        AssistInfo[] values = assists[i].getAttributeValues(dim[1], uri, value, attrInfo);
        if (values != null) {
          return values;
        }
      }
    }
    if (attrInfo.getAttributeType() == SCOPE) {
      return new AssistInfo[] { new AssistInfo("application"), new AssistInfo("page"), new AssistInfo("request"), new AssistInfo("session") };
    }
    if (attrInfo.getAttributeType() == CLASS && this.file != null) {
      return classNameProcessor.getClassAttributeValues(this.file, value);
    }
    return super.getAttributeValues(tagName, value, tagInfo, attrInfo);
  }

  protected TagInfo getTagInfo(String name) {
    List tagList = getTagList();
    for (int i = 0; i < tagList.size(); i++) {
      TagInfo info = (TagInfo) tagList.get(i);
      if (info.getTagName() != null) {
        if (name.equals(info.getTagName().toLowerCase())) {
          return info;
        }
      }
    }
    return null;
  }

  protected List getTagList() {
    List list = new ArrayList();
    list.addAll(tagList);
    list.addAll(cunstomTagList);
    return list;
  }

  /** Returns URI from taglib prefix. */
  private String getUri(String prefix) {
    return (String) namespace.get(prefix);
  }

  /**
   * Updates informations about code completion.
   * 
   * @param input the <code>HTMLSourceEditor</code> instance
   * @param source JSP source code
   */
  public void update(HTMLSourceEditor editor, String source) {
    super.update(editor, source);
    this.scriptletProcessor.update((JSPSourceEditor) editor);
    if (editor.getEditorInput() instanceof IFileEditorInput) {
      IFileEditorInput input = (IFileEditorInput) editor.getEditorInput();
      cunstomTagList.clear();
      namespace.clear();
      JSPInfo jspInfo = JSPInfo.getJSPInfo(input.getFile(), source);
      TLDInfo[] tlds = jspInfo.getTLDInfo();
      for (int i = 0; i < tlds.length; i++) {
        namespace.put(tlds[i].getPrefix(), tlds[i].getTaglibUri());
        cunstomTagList.addAll(tlds[i].getTagInfo());
      }
      this.file = input.getFile();
    }
  }

}
